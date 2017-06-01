package me.austinatchley;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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

public class GameScreen implements Screen {
    final RocketGame game;
    private final int SCREEN_X = Gdx.graphics.getWidth();
    private final int SCREEN_Y = Gdx.graphics.getHeight();
    private final int VERTICAL_OFF = 20;

    private OrthographicCamera camera;
    private World world;

    private Body rocket;
    private ArrayList<Body> obstacles;
    private Texture rocketImage;
    private Texture asteroidImage;
    private int score;
    private long lastDropTime;

    public GameScreen(final RocketGame game){
        this.game = game;

        rocketImage = new Texture("rocket.png");
        asteroidImage = new Texture("asteroid.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_X, SCREEN_Y);
        world = new World(new Vector2(0, -98f), true);

        BodyDef rocketBodyDef = new BodyDef();
        rocketBodyDef.type = BodyDef.BodyType.KinematicBody;
        rocketBodyDef.position.set((SCREEN_X - rocketImage.getWidth())/ 2, VERTICAL_OFF);
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
    public void render (float delta) {
        Gdx.app.debug("render","render");

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        Gdx.gl.glClearColor(0, 0, .2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.setColor(1,1,1,0.75f);
        game.font.draw(game.batch, "Score: " + score, SCREEN_X / 2 - 100, SCREEN_Y - 50);
        game.batch.draw(rocketImage, rocket.getPosition().x, rocket.getPosition().y);
        for(Body asteroid : obstacles){
            game.batch.draw(asteroidImage, asteroid.getPosition().x, asteroid.getPosition().y);
        }
        game.batch.end();

        if(Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            Vector2 targetPos = new Vector2(touchPos.x - rocketImage.getWidth() / 2,
                                            touchPos.y - rocketImage.getHeight() / 2);
            Vector2 currentPos = new Vector2(rocket.getPosition().x, rocket.getPosition().y);
            currentPos.lerp(targetPos, 0.25f);
            rocket.setTransform(currentPos, 0);
        }

        if(rocket.getPosition().x < 0)
            rocket.setTransform(0f,rocket.getPosition().y,0f);
        else if(rocket.getPosition().x > SCREEN_X - rocketImage.getWidth())
            rocket.setTransform(SCREEN_X - rocketImage.getWidth(),
                    rocket.getPosition().y, 0);
        if(rocket.getPosition().y > SCREEN_Y / 2)
            rocket.setTransform(rocket.getPosition().x,SCREEN_Y / 2,0f);

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

    private void spawnAsteroid() {
        BodyDef asteroidBodyDef = new BodyDef();
        asteroidBodyDef.type = BodyDef.BodyType.DynamicBody;
        asteroidBodyDef.position.set(MathUtils.random(0, SCREEN_X - 90), SCREEN_Y);
        Body asteroid = world.createBody(asteroidBodyDef);
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
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        game.batch.dispose();
        rocketImage.dispose();
    }

    private void gameOver(){
        score = -1;
    }
}

