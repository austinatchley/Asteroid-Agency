package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

import java.util.ArrayList;
import java.util.Iterator;

import me.austinatchley.GameStateManager;
import me.austinatchley.RocketGame;

public class GameState extends State {
    private final int VERTICAL_OFF = 20;

    private World world;

    private Body rocket;
    private ArrayList<Body> obstacles;
    private Texture rocketImage;
    private Texture asteroidImage;
    private int score;
    private long lastDropTime;

    public GameState(GameStateManager gsm){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);

        rocketImage = new Texture("rocket.png");
        asteroidImage = new Texture("asteroid.png");

        world = new World(new Vector2(0, -98f), true);

        BodyDef rocketBodyDef = new BodyDef();
        rocketBodyDef.type = BodyDef.BodyType.KinematicBody;
        rocketBodyDef.position.set((WIDTH - rocketImage.getWidth())/ 2, VERTICAL_OFF);
        rocket = world.createBody(rocketBodyDef);
        MassData rocketMassData = new MassData();
        rocketMassData.mass = 0;
        rocket.setMassData(rocketMassData);
        PolygonShape rocketShape = new PolygonShape();
        rocketShape.setAsBox(rocketImage.getWidth()/2, rocketImage.getHeight() * 5 / 6);

        FixtureDef rocketFixtureDef = new FixtureDef();
        rocketFixtureDef.shape = rocketShape;

        Fixture rocketFixture = rocket.createFixture(rocketFixtureDef);
        rocketFixture.setUserData("Rocket");
        rocketShape.dispose();

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
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        if(Gdx.input.isTouched()){
            handleInput();
        }

        if(rocket.getPosition().x < 0)
            rocket.setTransform(0f,rocket.getPosition().y,0f);
        else if(rocket.getPosition().x > WIDTH - rocketImage.getWidth())
            rocket.setTransform(WIDTH - rocketImage.getWidth(),
                    rocket.getPosition().y, 0);
        if(rocket.getPosition().y > HEIGHT / 2)
            rocket.setTransform(rocket.getPosition().x,HEIGHT / 2,0f);

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnAsteroid();

        Iterator<Body> iter = obstacles.iterator();
        while(iter.hasNext()){
            Body asteroid = iter.next();
            if (asteroid.getPosition().y + 90 < 0) {
                iter.remove();
                score++;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, .2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.setColor(1,1,1,0.75f);
        font.draw(batch, "Score: " + score, WIDTH / 2 - 100, HEIGHT - 50);
        batch.draw(rocketImage, rocket.getPosition().x, rocket.getPosition().y);
        for(Body asteroid : obstacles){
            batch.draw(asteroidImage, asteroid.getPosition().x, asteroid.getPosition().y);
        }
        batch.end();
    }

    private void spawnAsteroid() {
        BodyDef asteroidBodyDef = new BodyDef();
        asteroidBodyDef.type = BodyDef.BodyType.DynamicBody;
        asteroidBodyDef.position.set(MathUtils.random(0, WIDTH - 90), HEIGHT);

        Body asteroid = world.createBody(asteroidBodyDef);
//        asteroid.applyForceToCenter(o, MathUtils.random(1f,100f), false);

        CircleShape asteroidShape = new CircleShape();
        asteroidShape.setRadius(45f);

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
    }

    private void gameOver(){
        score = 0;
    }
}

