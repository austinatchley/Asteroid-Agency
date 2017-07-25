package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import java.util.ArrayList;
import java.util.Iterator;

import me.austinatchley.Objects.Asteroid;
import me.austinatchley.GameStateManager;
import me.austinatchley.Objects.Enemy;
import me.austinatchley.Objects.Missile;
import me.austinatchley.Objects.Rocket;
import me.austinatchley.Objects.SpaceObject;

public class GameState extends State {
    private static final int NUM_ASTEROID_SPRITES = 64;
    private static final float FRAME_TIME = .06f;
    private static final int ENEMY_LIMIT = 10;
    private static final int ASTEROID_LIMIT = 16;
    public static final float PPM = 1/4f;

    private Box2DDebugRenderer render;
    private Matrix4 debugMatrix;

    private World world;

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
    private long lastDropTime;
    private long lastEnemyTime;

    private int enemyNum = 1;
    private int totalEnemyNum = enemyNum;
    private float enemySpeed = MathUtils.random(-180f, 180f);

    public GameState(GameStateManager gsm){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);

        init();

        render = new Box2DDebugRenderer();
        debugMatrix = new Matrix4(camera.combined).scale(1/PPM,1/PPM,1f);
    }

    @Override
    public void update(float dt) {
        //step as much as possible
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        cleanDestroyArray();
        //only call handleInput on touch
        if(Gdx.input.isTouched())
            handleInput();

        //limit rocket position
        if(rocket.getPosition().x < 0)
            rocket.setTransform(0f, rocket.getPosition().y,
                    rocket.getBody().getAngle());
        else if(rocket.getPosition().x > WIDTH - rocket.getWidth()) {
            rocket.setTransform(WIDTH - rocket.getWidth(), rocket.getPosition().y,
                    rocket.getBody().getAngle());
            System.out.println("hit right " + rocket.getPosition());
        }

        //spawn asteroid every 2 seconds
        if (TimeUtils.nanoTime() - lastDropTime > 2000000000 && asteroids.size < ASTEROID_LIMIT) {
            spawnAsteroid();
        }

        if (TimeUtils.nanoTime() - lastEnemyTime > 1000000000 && enemyNum <= ENEMY_LIMIT){
            spawnEnemy(enemyNum++, Gdx.graphics.getHeight() + 100f, 2);
            totalEnemyNum++;
        }


        for(Enemy enemy : enemies)
            if(enemy.canShoot())
//                enemy.shoot(Math.random() > 0.5f ? "fast" : "curvy");
                enemy.shoot("fast");


        //iterate through asteroids
        Iterator<Asteroid> iter = asteroids.iterator();
        while(iter.hasNext()){
            Asteroid asteroid = iter.next();
            //check to see if asteroid is below screen
            if (asteroid.getPosition().y + asteroid.getHeight() < 0) {
                iter.remove();
                score++;
            }
        }
    }

    private void cleanDestroyArray() {
        for(int i = destroyArray.size - 1; i >= 0; i--) {
            destroyArray.get(i).dispose();
            destroyArray.removeIndex(i);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        //background color
        Gdx.gl.glClearColor(0, 0, .2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        render.render(world, debugMatrix);

        batch.begin();

        //draw rocket
        rocket.render(batch);

        for(Asteroid ast : asteroids)
            ast.render(batch);

        Iterator<Enemy> iter = enemies.iterator();
        while(iter.hasNext()) {
            Enemy enemy = iter.next();
            enemy.render(batch);

            enemy.yDir = -180f;
            enemy.move(Gdx.graphics.getDeltaTime());

            if(enemy.getPosition().x >= Gdx.graphics.getWidth() - enemy.getWidth()) {
                enemy.xDir *= -1;
//                System.out.println("hit");
            }
            if(enemy.getPosition().x <= 0){
                enemy.xDir *= -1;
            }

            if(enemy.getPosition().y < -2f * enemy.getHeight()) {
                enemyNum--;
                System.out.println(enemyNum);
                enemy.dispose();
                iter.remove();
                if(totalEnemyNum % ENEMY_LIMIT == 1)
                    enemySpeed = randomNumInRange(250f, 250f, true);

            }
        }

        //draw score last to stay on top
        font.setColor(1,1,1,1);
        font.draw(batch, "Score: " + score, WIDTH / 2 - 100, HEIGHT - 50);

        batch.draw(pauseButton, pauseLocation.x - pauseButton.getWidth(),
                pauseLocation.y - pauseButton.getHeight());
        batch.end();
    }

    private float randomNumInRange(float start, float range, boolean canBeNeg) {
        float rand = MathUtils.random(0,range) + start;
        if(MathUtils.random() > 0.5f)
            rand *= -1;
        return rand;
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

        setupContactListener();
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
            @Override
            public void beginContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();
                if(asteroidTag.equals(a.getUserData()) &&
                        rocketTag.equals(b.getUserData())){
                    for(Asteroid asteroid : asteroids) {
                        if(a.getBody().equals(asteroid.getBody())) {
                            destroyArray.add(asteroid);
                            asteroids.removeValue(asteroid, false);
                            gameOver();
                            break;
                        }
                    }
                } else if(asteroidTag.equals(b.getUserData()) &&
                        rocketTag.equals(a.getUserData())) {
                    for(Asteroid asteroid : asteroids) {
                        if(b.getBody().equals(asteroid.getBody())) {
                            destroyArray.add(asteroid);
                            asteroids.removeValue(asteroid, false);
                            gameOver();
                            break;
                        }
                    }
                } else if(enemyTag.equals(a.getUserData()) &&
                        missileTag.equals(b.getUserData())){
                    System.out.println("enemy+missile");
                } else if(enemyTag.equals(b.getUserData()) &&
                        missileTag.equals(a.getUserData())) {
                    System.out.println("missile+enemy");
                } else if(rocketTag.equals(a.getUserData()) &&
                        missileTag.equals(b.getUserData())){
                    for(Enemy enemy : enemies) {
                        for (Missile shot : enemy.shots) {
                            if (b.getBody().equals(shot.getBody())) {
                                destroyArray.add(shot);
                                enemy.shots.remove(shot);
                                gameOver();
                                break;
                            }
                        }
                    }
                } else if(rocketTag.equals(b.getUserData()) &&
                        missileTag.equals(a.getUserData())){
                    for(Enemy enemy : enemies) {
                        for (Missile shot : enemy.shots) {
                            if (a.getBody().equals(shot.getBody())) {
                                destroyArray.add(shot);
                                enemy.shots.remove(shot);
                                gameOver();
                                break;
                            }
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

        if(pauseBounds.contains(touchPos.x, touchPos.y))
            gsm.push(new PauseState(gsm));


        camera.unproject(touchPos);

        Vector2 targetPos = new Vector2(touchPos.x - rocket.getWidth() / 2,
                touchPos.y - rocket.getHeight() / 2);
        Vector2 currentPos = new Vector2(rocket.getPosition().x, rocket.getPosition().y);
        currentPos.lerp(targetPos, 0.15f);
        rocket.moveTo(currentPos);

//        for(Enemy enemy : enemies){
//            enemy.shoot("normal");
//            System.out.println("shoot");
//        }
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
        //trying to start spawning enemies from other side of x axis
        //doesn't work right now
        if((numX % 2*enemyNum) >= enemyNum && (numX % 2*enemyNum) <= 2*enemyNum)
            numX = enemyNum - numX;

        Enemy enemy = new Enemy(world, numX, height);
        enemySpawned(enemy);
    }

    private void spawnEnemy(int numX, float height, int numShots){
        if((numX % 2*enemyNum) >= enemyNum && (numX % 2*enemyNum) <= 2*enemyNum)
            numX = enemyNum - numX;

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
        score = 0;
    }

    /*
    Converts meters to pixels for use with LibGDX
    @param  xMeters float x distance in meters
    @param  yMeters float y distance in meters
    @return Vector2 representation of distance in pixels
     */
    public static Vector2 m2p(float xMeters, float yMeters){
        return new Vector2(xMeters / PPM, yMeters / PPM);
    }


    public static Vector2 m2p(Vector2 meters){
        return new Vector2(meters.x / PPM, meters.y / PPM);
    }

    /*
    Converts pixels to meters for use with Box2D
    @param  xPixels float x distance in pixels
    @param  yPixels float y distance in pixels
    @return Vector2 representation of distance in meters
     */
    public static Vector2 p2m(float xPixels, float yPixels){
        return new Vector2(xPixels * PPM, yPixels * PPM);
    }

    public static Vector2 p2m(Vector2 pixels){
        return new Vector2(pixels.x * PPM, pixels.y * PPM);
    }
}

