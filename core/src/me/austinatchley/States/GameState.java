package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import me.austinatchley.GameStateManager;
import me.austinatchley.Objects.Asteroid;
import me.austinatchley.Objects.Enemy;
import me.austinatchley.Objects.Junk;
import me.austinatchley.Objects.Missile;
import me.austinatchley.Objects.Rocket;
import me.austinatchley.Objects.SpaceObject;
import me.austinatchley.RocketGame;
import me.austinatchley.Tools.Starfield;

import static me.austinatchley.Tools.Utils.ASTEROID_LIMIT;
import static me.austinatchley.Tools.Utils.ENEMY_LIMIT;
import static me.austinatchley.Tools.Utils.HEIGHT;
import static me.austinatchley.Tools.Utils.IS_DESKTOP;
import static me.austinatchley.Tools.Utils.MOVE_DIST;
import static me.austinatchley.Tools.Utils.WIDTH;

public class GameState extends State {

//    private Box2DDebugRenderer debugRenderer;
//    private Matrix4 debugMatrix;

    private World world;
    private Starfield starfield;
    private Rocket rocket;

    private Color bgColor;
    private Array<Asteroid> asteroids;
    private Array<Enemy> enemies;
    public Array<Missile> shots;
    private Array<SpaceObject> destroyArray;
    private Array<Junk> junks;

    private Vector2 pauseLocation;
    private Rectangle pauseBounds;
    private Texture pauseButton;

    private int score;
    private GlyphLayout scoreLayout;
    private GlyphLayout livesLayout;

    private int lives;

    private long lastDropTime;
    private long lastJunkDropTime;
    private long lastEnemyTime;

    private int enemyNum = 1;
    private int totalEnemyNum = enemyNum;
    private float enemySpeed;
    private int enemySpawnLocation;

    private Sound missileSound;
    private Sound explosionSound;
    private Sound gameOverSound;

    private RocketGame game;

    public GameState(GameStateManager gsm, Starfield starfield){
        super(gsm);

        this.starfield = starfield;
        this.starfield.rocket = rocket;

        this.game = gsm.game;

        init();

        setupContactListener();
//        debugRenderer = new Box2DDebugRenderer();
//        debugMatrix = new Matrix4(camera.combined).scale(1/ Utils.PPM,1/Utils.PPM,1f);
    }

    @Override
    public void update(float dt) {
        //locked at 60 fps
        world.step(1f/60f, 6, 2);

        //shoot missiles
        if(rocket.canShoot())
            rocket.shootMissile(missileSound);

        //destroy all the objects we need to
        cleanDestroyArray();

        //only call handleInput on touch
        if(isControlled())
            handleInput();
        else
            starfield.useVelocity(false);

        updateEnemies();

        checkRocketBounds();

        //spawn asteroid every 2 seconds
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000 + MathUtils.random(1000000000)&& asteroids.size < ASTEROID_LIMIT) {
            spawnAsteroid();
        }

        if (TimeUtils.nanoTime() - lastJunkDropTime > 1400000000 + MathUtils.random(600000000)  && junks.size < ASTEROID_LIMIT) {
            spawnJunk();
        }

        if (TimeUtils.nanoTime() - lastEnemyTime > 1000000000){
            if(totalEnemyNum % ENEMY_LIMIT == 0) {
                enemySpawnLocation = MathUtils.random((int) (WIDTH / rocket.getWidth()));
//                System.out.println("diff pos " + enemySpawnLocation);
            }
            if(enemyNum <= ENEMY_LIMIT) {
                spawnEnemy(enemySpawnLocation, HEIGHT + rocket.getHeight(), 2);
                enemyNum++;
                totalEnemyNum++;
            }
        }

        checkBounds(asteroids);
        checkBounds(junks);
    }

    private boolean isControlled() {
        if(IS_DESKTOP)
            return Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                    Gdx.input.isKeyPressed(Input.Keys.DOWN) ||Gdx.input.isKeyPressed(Input.Keys.UP);
        return Gdx.input.isTouched();
    }

    @Override
    public void render(SpriteBatch batch) {
        //background color
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

//        debugRenderer.render(world, debugMatrix);

        //update the stars
        starfield.render();

        batch.begin();

        //draw rocket
        rocket.render(batch);

        for(Asteroid ast : asteroids)
            ast.render(batch);

        for(Enemy enemy : enemies)
            enemy.render(batch);

        for(Junk junk : junks)
            junk.render(batch);

        Iterator<Missile> iterator = shots.iterator();
        while(iterator.hasNext()){
            Missile shot = iterator.next();
            shot.render(batch);
            if(shot.isOutOfBounds()){
                shot.dispose();
                iterator.remove();
            }
        }

        //draw score last to stay on top
        scoreLayout.setText(font, "Score: " + score);
        float scoreX = (WIDTH - scoreLayout.width) / 2f;
        float scoreY = HEIGHT - scoreLayout.height * 5f/4f;

        livesLayout.setText(font, "Lives: " + lives);
        float livesX = (WIDTH - livesLayout.width) / 2f;
        float livesY = scoreY - (livesLayout.height * 1.5f);

        font.draw(batch, scoreLayout, scoreX, scoreY);
        font.draw(batch, livesLayout, livesX, livesY);

        batch.draw(pauseButton, pauseLocation.x - pauseButton.getWidth(),
                pauseLocation.y - pauseButton.getHeight());
        batch.end();
    }

    private void init() {
        Box2D.init();
        world = new World(new Vector2(0, -100f), true);

        rocket = new Rocket(world);

        asteroids = new Array<Asteroid>();
        enemies = new Array<Enemy>();
        destroyArray = new Array<SpaceObject>();
        shots = new Array<Missile>();
        junks = new Array<Junk>();

        enemySpeed = randomNumInRange(250f, 250f, true);

        pauseButton = new Texture("pause.png");
        pauseLocation = new Vector2(WIDTH, HEIGHT);
        pauseBounds = new Rectangle(pauseLocation.x - pauseButton.getWidth(),
                0,
                pauseButton.getWidth(),
                pauseButton.getHeight());

        score = 0;
        lives = 3;
        scoreLayout = new GlyphLayout();
        livesLayout = new GlyphLayout();
        font.setColor(new Color(0xD3BCC0FF));

        bgColor = new Color(0x0E103DFF);

        missileSound = game.manager.get("shoot.wav", Sound.class);
        explosionSound = game.manager.get("explosion.wav", Sound.class);
        gameOverSound = game.manager.get("gameover.wav", Sound.class);
    }

    //check bounds to keep rocket on screen
    private void checkRocketBounds() {
        //limit rocket position to screen
        if(rocket.getPosition().x < 0)
            rocket.setTransform(0, rocket.getPosition().y,
                    rocket.getBody().getAngle());
        else if(rocket.getPosition().x > WIDTH - rocket.getWidth() / 2) {
            rocket.setTransform(WIDTH - rocket.getWidth() / 2, rocket.getPosition().y,
                    rocket.getBody().getAngle());
        }
    }

    private void checkBounds(Array bodies) {
        //iterate through asteroids
        Iterator<SpaceObject> iter = ((Array<SpaceObject>) bodies).iterator();
        while(iter.hasNext()){
            SpaceObject body = iter.next();
            //check to see if asteroid is below screen
            if (body.getPosition().y + body.getHeight() < 0) {
                iter.remove();
            }
        }
    }

    private void updateEnemies(){
        Iterator<Enemy> iter = enemies.iterator();
        while(iter.hasNext()) {
            Enemy enemy = iter.next();
            enemy.move(Gdx.graphics.getDeltaTime());
            enemy.update();

            enemy.yDir = -180f - (score * 2f);

            checkEnemyBounds(enemy);

            if(enemy.getPosition().y < -2f * enemy.getHeight()) {
                enemyNum--;
                enemy.dispose();
                iter.remove();
                if(totalEnemyNum % ENEMY_LIMIT == 1)
                    enemySpeed = randomNumInRange(250f, 250f, true);
            }
        }
    }

    //move enemies and check bounds
    private void checkEnemyBounds(Enemy enemy) {
        if(enemy.getPosition().x >= Gdx.graphics.getWidth())
            enemy.xDir = -Math.abs(enemy.xDir);

        if(enemy.getPosition().x <= enemy.getWidth())
            enemy.xDir = Math.abs(enemy.xDir);
    }

    private void cleanDestroyArray() {
        for(int i = destroyArray.size - 1; i >= 0; i--) {
            destroyArray.get(i).dispose();
            destroyArray.removeIndex(i);
        }
    }

    private float randomNumInRange(float start, float range, boolean canBeNeg) {
        float rand = MathUtils.random(0,range) + start;
        if(MathUtils.random() > 0.5f)
            rand *= -1;
        return rand;
    }

    private void setupContactListener() {
        world.setContactListener(new ContactListener() {
            String asteroidTag = "Asteroid";
            String rocketTag = "Rocket";
            String enemyTag = "Enemy";
            String missileTag = "Missile";
            String playerMissileTag = "PMissile";
            String junkTag = "Junk";

            @Override
            public void beginContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();

                if (asteroidTag.equals(a.getUserData()) &&
                        rocketTag.equals(b.getBody().getUserData())) {
                    for (Asteroid asteroid : asteroids) {
                        if (a.getBody().equals(asteroid.getBody())) {
                            destroyArray.add(asteroid);
                            asteroids.removeValue(asteroid, false);
                            score++;
                            break;
                        }
                    }
                } else if (asteroidTag.equals(b.getUserData()) &&
                        rocketTag.equals(a.getBody().getUserData())) {
                    for (Asteroid asteroid : asteroids) {
                        if (b.getBody().equals(asteroid.getBody())) {
                            destroyArray.add(asteroid);
                            asteroids.removeValue(asteroid, false);
                            score++;
                            break;
                        }
                    }
                } else if (rocketTag.equals(a.getBody().getUserData()) &&
                        missileTag.equals(b.getUserData())) {
                        for (Missile shot : shots) {
                            if (b.getBody().equals(shot.getBody())) {
                                long id = explosionSound.play();
                                explosionSound.setPitch(id, .5f);

                                destroyArray.add(shot);
                                shots.removeValue(shot, false);
                                loseLife();
                                break;
                            }
                        }
                } else if (rocketTag.equals(b.getBody().getUserData()) &&
                        missileTag.equals(a.getUserData())) {
                        for (Missile shot : shots) {
                            if (a.getBody().equals(shot.getBody())) {
                                long id = explosionSound.play();
                                explosionSound.setPitch(id, .5f);

                                destroyArray.add(shot);
                                shots.removeValue(shot, false);
                                loseLife();
                                break;
                            }
                        }
                } else if (enemyTag.equals(a.getBody().getUserData()) &&
                        playerMissileTag.equals(b.getUserData())) {
                    for(Enemy enemy : enemies){
                        if(a.getBody().equals(enemy.getBody())){
                            explosionSound.play();
                            destroyArray.add(enemy);
                            enemies.removeValue(enemy, false);
                            score++;
                            enemyNum--;
                            break;
                        }
                    }
                } else if (enemyTag.equals(b.getBody().getUserData()) &&
                        playerMissileTag.equals(a.getUserData())) {
                    for(Enemy enemy : enemies){
                        if(b.getBody().equals(enemy.getBody())){
                            explosionSound.play();
                            destroyArray.add(enemy);
                            enemies.removeValue(enemy, false);
                            score++;
                            enemyNum--;
                            break;
                        }
                    }
                } else if (rocketTag.equals(a.getBody().getUserData()) &&
                        junkTag.equals(b.getUserData())) {
                    for(Junk junk : junks){
                        if(b.getBody().equals(junk.getBody())){
                            destroyArray.add(junk);
                            junks.removeValue(junk, false);
                            loseLife();
                            break;
                        }
                    }
                } else if (rocketTag.equals(b.getBody().getUserData()) &&
                        junkTag.equals(a.getUserData())) {
                    for(Junk junk : junks){
                        if(a.getBody().equals(junk.getBody())){
                            destroyArray.add(junk);
                            junks.removeValue(junk, false);
                            loseLife();
                            break;
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    @Override
    protected void handleInput() {
        starfield.useVelocity(true);

        controls();
    }

    private void controls() {
        if(IS_DESKTOP)
            arrowControls();
        else
            touchControls();
    }

    private void touchControls() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

        if(pauseBounds.contains(touchPos.x, touchPos.y)) {
            //build a dummy starfield with no rocket for pause
            Starfield dummy = new Starfield(400, camera, null);
            dummy.stars = this.starfield.stars;
            gsm.push(new PauseState(gsm, dummy));
        }


        camera.unproject(touchPos);

        Vector2 targetPos = new Vector2(touchPos.x - (rocket.getWidth() / 1.5f),
                touchPos.y - rocket.getHeight() / 2);
        Vector2 currentPos = new Vector2(rocket.getPosition().x, rocket.getPosition().y);
        Vector2 finalPos = currentPos.lerp(targetPos, 0.15f);
        rocket.moveTo(finalPos);
    }

    private void arrowControls() {
        float posX = rocket.getPosition().x;
        float posY = rocket.getPosition().y;

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            posX -= MOVE_DIST;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            posX += MOVE_DIST;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            posY -= MOVE_DIST;
        if(Gdx.input.isKeyPressed(Input.Keys.UP))
            posY += MOVE_DIST;


        Vector2 newPos = new Vector2(posX, posY);
        Vector2 finalPos = rocket.getPosition().lerp(newPos, 0.45f);
        rocket.setTransform(finalPos, rocket.getBody().getAngle());
    }

    private void spawnAsteroid() {
        Asteroid asteroid = new Asteroid(world);

        asteroids.add(asteroid);
        lastDropTime = TimeUtils.nanoTime();
    }

    private void spawnJunk() {
        Junk junk = new Junk(world);

        junks.add(junk);
        lastJunkDropTime = TimeUtils.nanoTime();
    }

    private void spawnEnemy(float x, float y) {
        Enemy enemy = new Enemy(world, new Vector2(x, y));
        enemySpawned(enemy);
    }

    private void spawnEnemy(int numX, float height){
        Enemy enemy = new Enemy(world, numX, height);
        enemySpawned(enemy);
    }

    private void spawnEnemy(int numX, float height, int numShots){
        Enemy enemy = new Enemy(world, numX, height, numShots, shots);
        enemySpawned(enemy);
    }

    private void spawnEnemy(Vector2 spawnLocation){
        spawnEnemy(spawnLocation.x, spawnLocation.y);
    }

    private void enemySpawned(Enemy enemy){
        enemy.xDir = enemySpeed;
        enemies.add(enemy);
        lastEnemyTime = TimeUtils.nanoTime();
    }

    @Override
    public void dispose () {
        rocket.dispose();
        gameOverSound.dispose();
        explosionSound.dispose();
        missileSound.dispose();
        pauseButton.dispose();
        world.dispose();

    }

    private void gameOver(){
        gsm.tryHighScore(score);
        gameOverSound.play();
        gsm.set(new GameOverState(gsm, score));
    }

    private void loseLife() {
        if(--lives <= 0)
            gameOver();
    }
}

