package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;
import java.util.Iterator;

import me.austinatchley.Objects.Asteroid;
import me.austinatchley.GameStateManager;
import me.austinatchley.Objects.Enemy;
import me.austinatchley.Objects.Missile;
import me.austinatchley.Objects.Rocket;
import me.austinatchley.Objects.SpaceObject;
import me.austinatchley.Star;
import me.austinatchley.Starfield;
import me.austinatchley.Utils;

public class GameState extends State {
    private static final int NUM_ASTEROID_SPRITES = 64;
    private static final float FRAME_TIME = .06f;
    private static final int ENEMY_LIMIT = 10;
    private static final int ASTEROID_LIMIT = 16;

    private Box2DDebugRenderer debugRenderer;
    private Matrix4 debugMatrix;

    private World world;
    private Starfield starfield;

    private Color bgColor;

    private Array<Asteroid> asteroids;
    private Array<Enemy> enemies;
    private Array<SpaceObject> destroyArray;
    private Rocket rocket;

    private Vector2 pauseLocation;
    private Rectangle pauseBounds;
    private Texture pauseButton;

    private Animation<TextureRegion> asteroidAnimation;
    private Texture asteroidSheet;
    private float stateTime;

    private int score;
    private GlyphLayout scoreLayout;
    private GlyphLayout livesLayout;

    private int lives;

    private long lastDropTime;
    private long lastEnemyTime;

    private int enemyNum = 1;
    private int totalEnemyNum = enemyNum;
    private float enemySpeed = MathUtils.random(-180f, 180f);
    private int enemySpawnLocation;

    public GameState(GameStateManager gsm){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);

        init();

        setupContactListener();

//        debugRenderer = new Box2DDebugRenderer();
//        debugMatrix = new Matrix4(camera.combined).scale(1/PPM,1/PPM,1f);

        starfield = new Starfield(300, camera, rocket);
    }

    public GameState(GameStateManager gsm, Starfield starfield){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);

        init();

        setupContactListener();

//        debugRenderer = new Box2DDebugRenderer();
        debugMatrix = new Matrix4(camera.combined).scale(1/ Utils.PPM,1/Utils.PPM,1f);

        this.starfield = starfield;
        this.starfield.rocket = rocket;
    }

    @Override
    public void update(float dt) {
        //step as much as possible
//        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        world.step(1f/60f, 6, 2);

        //destroy all the objects we need to
        cleanDestroyArray();

        //only call handleInput on touch
        if(Gdx.input.isTouched())
            handleInput();

        updateEnemies();

        checkRocketBounds();

        //spawn asteroid every 2 seconds
        if (TimeUtils.nanoTime() - lastDropTime > 2000000000 && asteroids.size < ASTEROID_LIMIT) {
            spawnAsteroid();
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


        for(Enemy enemy : enemies)
            enemy.update();


        checkAsteroids();
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

        setUpAsteroidAnimation();
        asteroids = new Array<Asteroid>();
        enemies = new Array<Enemy>();
        destroyArray = new Array<SpaceObject>();

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

    private void checkAsteroids() {
        //iterate through asteroids
        Iterator<Asteroid> iter = asteroids.iterator();
        while(iter.hasNext()){
            Asteroid asteroid = iter.next();
            //check to see if asteroid is below screen
            if (asteroid.getPosition().y + asteroid.getHeight() < 0) {
                iter.remove();
            }
        }
    }

    private void updateEnemies(){
        Iterator<Enemy> iter = enemies.iterator();
        while(iter.hasNext()) {
            Enemy enemy = iter.next();
            enemy.move(Gdx.graphics.getDeltaTime());

            enemy.yDir = -180f;

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

    private void setUpAsteroidAnimation() {
        asteroidSheet = new Texture("asteroidSheet.png");

        TextureRegion[][] tmpFrames = TextureRegion.split(asteroidSheet,
                asteroidSheet.getWidth() / NUM_ASTEROID_SPRITES, asteroidSheet.getHeight());

        TextureRegion[] spinFrames = new TextureRegion[NUM_ASTEROID_SPRITES];
        int index = 0;
        for (TextureRegion[] tmpFrame : tmpFrames)
            for (int j = 0; j < tmpFrames[0].length; j++)
                spinFrames[index++] = tmpFrame[j];

        asteroidAnimation = new Animation<TextureRegion>(FRAME_TIME, spinFrames);
        stateTime = 0f;
    }

    private void setupContactListener() {
        world.setContactListener(new ContactListener() {
            String asteroidTag = "Asteroid";
            String rocketTag = "Rocket";
            String enemyTag = "Enemy";
            String missileTag = "Missile";
            String playerMissileTag = "PMissile";

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
                    for (Enemy enemy : enemies) {
                        for (Missile shot : enemy.shots) {
                            if (b.getBody().equals(shot.getBody())) {
                                destroyArray.add(shot);
                                enemy.shots.remove(shot);
                                loseLife();
                                break;
                            }
                        }
                    }
                } else if (rocketTag.equals(b.getBody().getUserData()) &&
                        missileTag.equals(a.getUserData())) {
                    for (Enemy enemy : enemies) {
                        for (Missile shot : enemy.shots) {
                            if (a.getBody().equals(shot.getBody())) {
                                destroyArray.add(shot);
                                enemy.shots.remove(shot);
                                loseLife();
                                break;
                            }
                        }
                    }
                } else if (enemyTag.equals(a.getBody().getUserData()) &&
                        playerMissileTag.equals(b.getUserData())) {
                    for(Enemy enemy : enemies){
                        if(a.getBody().equals(enemy.getBody())){
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
                            destroyArray.add(enemy);
                            enemies.removeValue(enemy, false);
                            score++;
                            enemyNum--;
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
        if(rocket.canShoot())
            rocket.shootMissile();

        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        if(pauseBounds.contains(touchPos.x, touchPos.y)) {
            //fix this
            Starfield dummy = new Starfield(400, camera, null);
            dummy.stars = this.starfield.stars;
            gsm.push(new PauseState(gsm, dummy));
        }


        camera.unproject(touchPos);

        Vector2 targetPos = new Vector2(touchPos.x - (rocket.getWidth() / 1.5f),
                touchPos.y - rocket.getHeight() / 2);
        Vector2 currentPos = new Vector2(rocket.getPosition().x, rocket.getPosition().y);
        currentPos.lerp(targetPos, 0.15f);
        rocket.moveTo(currentPos);
    }

    private void spawnAsteroid() {
        Asteroid asteroid = new Asteroid(world);

        asteroids.add(asteroid);
        lastDropTime = TimeUtils.nanoTime();
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
        Enemy enemy = new Enemy(world, numX, height, numShots);
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
        asteroidSheet.dispose();
    }

    private void gameOver(){
        gsm.tryHighScore(score);
        gsm.push(new GameOverState(gsm, score));
    }

    private void loseLife() {
        lives--;
        if(lives <= 0)
            gameOver();
    }
}

