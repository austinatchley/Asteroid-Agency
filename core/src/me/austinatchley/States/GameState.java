package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;

import me.austinatchley.Objects.Asteroid;
import me.austinatchley.GameStateManager;
import me.austinatchley.Objects.Rocket;

public class GameState extends State {
    private static final int NUM_ASTEROID_SPRITES = 64;
    private static final float FRAME_TIME = .06f;

    private World world;
    private ArrayList<Asteroid> asteroids;
    private Rocket rocket;

    private Vector2 pauseLocation;
    private Rectangle pauseBounds;
    private Texture pauseButton;

    private Animation<TextureRegion> asteroidAnimation;
    private Texture asteroidSheet;
    private float stateTime;

    private int score;
    private long lastDropTime;

    public GameState(GameStateManager gsm){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);


        world = new World(new Vector2(0, -120f), true);

        rocket = new Rocket(world);

        setUpAsteroidAnimation();
        asteroids = new ArrayList<Asteroid>();

        pauseButton = new Texture("pause.png");
        pauseLocation = new Vector2(WIDTH, HEIGHT);
        pauseBounds = new Rectangle(pauseLocation.x - pauseButton.getWidth(),
                0,
                pauseButton.getWidth(),
                pauseButton.getHeight());

        score = 0;

        world.setContactListener(new ContactListener() {
            String asteroidTag = "Asteroid";
            @Override
            public void beginContact(Contact contact) {

                if(asteroidTag.equals(contact.getFixtureA().getUserData())) {
                    for(Asteroid asteroid : asteroids) {
                        if(contact.getFixtureA().getBody().equals(asteroid.getBody())) {
                            asteroids.remove(asteroid);
                            gameOver();
                            break;
                        }
                    }
                }
                else if(asteroidTag.equals(contact.getFixtureB().getUserData())) {
                    for(Asteroid asteroid : asteroids) {
                        if(contact.getFixtureB().getBody().equals(asteroid.getBody())) {
                            asteroids.remove(asteroid);
                            gameOver();
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

    @Override
    protected void handleInput() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        if(pauseBounds.contains(touchPos.x, touchPos.y)) {
            gsm.push(new PauseState(gsm));
//            Gdx.app.log("PAUSE","PAUSE");
        }

        camera.unproject(touchPos);
        Vector2 targetPos = new Vector2(touchPos.x - rocket.getWidth() / 2,
                touchPos.y - rocket.getHeight() / 2);
        Vector2 currentPos = new Vector2(rocket.getPosition().x, rocket.getPosition().y);
        currentPos.lerp(targetPos, 0.15f);
        rocket.moveTo(currentPos);
    }

    @Override
    public void update(float dt) {
        //step as much as possible
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        //only call handleInput on touch
        if(Gdx.input.isTouched())
            handleInput();


        //limit rocket position
        if(rocket.getPosition().x < 0)
            rocket.setTransform(0f, rocket.getPosition().y, rocket.getBody().getAngle());
        else if(rocket.getPosition().x > WIDTH - rocket.getWidth())
            rocket.setTransform(WIDTH - rocket.getWidth(),
                    rocket.getPosition().y, rocket.getBody().getAngle());
//        if(rocket.getPosition().y > HEIGHT / 2)
//            rocket.setTransform(rocket.getPosition().x,HEIGHT / 2, rocket.getBody().getAngle());

        //spawn asteroid every second
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnAsteroid();

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

    @Override
    public void render(SpriteBatch batch) {
        //background color
        Gdx.gl.glClearColor(0, 0, .2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;

        batch.begin();

        //draw rocket
        rocket.render(batch);

        //iterate through asteroids to draw
//        float tmpTime;
//        for(int i = 0; i < asteroids.size(); i++){
//            tmpTime = stateTime + ((float) i) * FRAME_TIME;
//            currentFrame = asteroidAnimation.getKeyFrame(tmpTime, true);
//            batch.draw(currentFrame,
//                    asteroids.get(i).getPosition().x,
//                    asteroids.get(i).getPosition().y);
//        }
        for(Asteroid ast : asteroids)
            ast.render(batch);

        //draw score last to stay on top
        font.setColor(1,1,1,1);
        font.draw(batch, "Score: " + score, WIDTH / 2 - 100, HEIGHT - 50);

        batch.draw(pauseButton, pauseLocation.x - pauseButton.getWidth() ,
                pauseLocation.y - pauseButton.getHeight());
        batch.end();
    }

    private void spawnAsteroid() {
        Asteroid asteroid = new Asteroid(world);

        asteroids.add(asteroid);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void dispose () {
        rocket.dispose();
        asteroidSheet.dispose();
    }

    private void gameOver(){
        score = 0;
    }
}

