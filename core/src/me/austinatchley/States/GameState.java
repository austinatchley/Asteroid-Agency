package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.sun.java.accessibility.util.java.awt.TextComponentTranslator;

import java.util.ArrayList;
import java.util.Iterator;

import me.austinatchley.GameStateManager;
import me.austinatchley.RocketGame;

public class GameState extends State {
    private static final int VERTICAL_OFF = 20;
    private static final int NUM_ASTEROID_SPRITES = 64;

    private World world;
    private Body rocket;
    private ArrayList<Body> obstacles;
    private Texture rocketImage;
    private Texture asteroidImage;

    private Animation<TextureRegion> asteroidAnimation;
    private Texture asteroidSheet;
    private float stateTime;

    private int score;
    private long lastDropTime;

    public GameState(GameStateManager gsm){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);

        rocketImage = new Texture("rocket.png");
        asteroidImage = new Texture("asteroid.png");
        asteroidSheet = new Texture("asteroidSheet.png");

        TextureRegion[][] tmpFrames = TextureRegion.split(asteroidSheet,
                asteroidSheet.getWidth() / NUM_ASTEROID_SPRITES, asteroidSheet.getHeight());

        TextureRegion[] spinFrames = new TextureRegion[NUM_ASTEROID_SPRITES];
        int index = 0;
        for(int i = 0; i < tmpFrames.length; i++)
            for(int j = 0; j < tmpFrames[0].length; j++)
                spinFrames[index++] = tmpFrames[i][j];

        asteroidAnimation = new Animation<TextureRegion>(.06f, spinFrames);
        stateTime = 0f;

        world = new World(new Vector2(0, -120f), true);

        initializeRocket();

        obstacles  = new ArrayList<Body>();


        score = 0;

        world.setContactListener(new ContactListener() {
            String asteroidTag = "Asteroid";
            @Override
            public void beginContact(Contact contact) {
                gameOver();
                if(asteroidTag.equals(contact.getFixtureA().getUserData()))
                    obstacles.remove(contact.getFixtureA().getBody());
                else if(asteroidTag.equals(contact.getFixtureB().getUserData()))
                    obstacles.remove(contact.getFixtureB().getBody());
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

    private void initializeRocket() {
        BodyDef rocketBodyDef = new BodyDef();
        rocketBodyDef.type = BodyDef.BodyType.KinematicBody;
        rocketBodyDef.position.set((WIDTH - rocketImage.getWidth())/ 2, VERTICAL_OFF);

        rocket = world.createBody(rocketBodyDef);

        MassData rocketMassData = new MassData();
        rocketMassData.mass = 0;
        rocket.setMassData(rocketMassData);

        PolygonShape rocketShape = new PolygonShape();
        rocketShape.setAsBox(rocketImage.getWidth()/2, rocketImage.getHeight());

        FixtureDef rocketFixtureDef = new FixtureDef();
        rocketFixtureDef.shape = rocketShape;

        Fixture rocketFixture = rocket.createFixture(rocketFixtureDef);
        rocketFixture.setUserData("Rocket");
        rocketShape.dispose();
    }

    @Override
    protected void handleInput() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        Vector2 targetPos = new Vector2(touchPos.x - rocketImage.getWidth() / 2,
                touchPos.y - rocketImage.getHeight() / 2);
        Vector2 currentPos = new Vector2(rocket.getPosition().x, rocket.getPosition().y);
        currentPos.lerp(targetPos, 0.25f);
        rocket.setTransform(currentPos, 0);
    }

    @Override
    public void update(float dt) {
        //step as much as possible
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        //only call handleInput on touch
        if(Gdx.input.isTouched()){
            handleInput();
        }

        //limit rocket position
        if(rocket.getPosition().x < 0)
            rocket.setTransform(0f, rocket.getPosition().y, 0f);
        else if(rocket.getPosition().x > WIDTH - rocketImage.getWidth())
            rocket.setTransform(WIDTH - rocketImage.getWidth(),
                    rocket.getPosition().y, 0);
        if(rocket.getPosition().y > HEIGHT / 2)
            rocket.setTransform(rocket.getPosition().x,HEIGHT / 2,0f);

        //spawn asteroid every second
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnAsteroid();

        //iterate through asteroids
        Iterator<Body> iter = obstacles.iterator();
        while(iter.hasNext()){
            Body asteroid = iter.next();
            //check to see if asteroid is below screen
            if (asteroid.getPosition().y + asteroidImage.getHeight() < 0) {
                iter.remove();
                score++;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        //background color
        Gdx.gl.glClearColor(0, 0, .2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = asteroidAnimation.getKeyFrame(stateTime, true);

        batch.begin();

        //draw rocket
        batch.draw(rocketImage, rocket.getPosition().x, rocket.getPosition().y);

        //iterate through asteroids to draw
        for(Body asteroid : obstacles){
            batch.draw(currentFrame, asteroid.getPosition().x, asteroid.getPosition().y);
        }

        //draw score last to stay on top
        font.setColor(1,1,1,0.75f);
        font.draw(batch, "Score: " + score, WIDTH / 2 - 100, HEIGHT - 50);

        batch.end();
    }

    private void spawnAsteroid() {
        BodyDef asteroidBodyDef = new BodyDef();
        asteroidBodyDef.type = BodyDef.BodyType.DynamicBody;
        asteroidBodyDef.position.set(MathUtils.random(0, WIDTH - 90), HEIGHT);

        Body asteroid = world.createBody(asteroidBodyDef);
        asteroid.setLinearDamping(MathUtils.random(0f,3f));

        CircleShape asteroidShape = new CircleShape();
        asteroidShape.setRadius(32f);

        FixtureDef asteroidFixtureDef = new FixtureDef();
        asteroidFixtureDef.shape = asteroidShape;
        asteroidFixtureDef.density = 1f;

        Fixture asteroidFixture = asteroid.createFixture(asteroidFixtureDef);
        asteroidFixture.setUserData("Asteroid");

        obstacles.add(asteroid);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void dispose () {
        rocketImage.dispose();
        asteroidSheet.dispose();
    }

    private void gameOver(){
        score = 0;
    }
}

