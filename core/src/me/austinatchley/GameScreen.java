package me.austinatchley;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {
    final RocketGame game;
    private final int SCREEN_X = 1080;
    private final int SCREEN_Y = 1920;
    private final int VERTICAL_OFF = 20;

    private OrthographicCamera camera;
    private Rectangle rocket;
    private Texture rocketImage;

    public GameScreen(final RocketGame game){
        this.game = game;

        rocketImage = new Texture("rocket.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_X, SCREEN_Y);

        rocket = new Rectangle();
        rocket.x = (SCREEN_X - rocket.getWidth())/ 2;
        rocket.y = VERTICAL_OFF;
        rocket.width = rocketImage.getWidth();
        rocket.height = rocketImage.getHeight();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, .2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(rocketImage, rocket.x, rocket.y);
        game.batch.end();

        if(Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            Vector3 targetPos = new Vector3(touchPos.x - rocket.width / 2,
                                            touchPos.y,
                                            0);
            Vector3 currentPos = new Vector3(rocket.x, rocket.y, 0);
            currentPos.lerp(targetPos, 0.7f);
            rocket.x = currentPos.x;
        }

        if(rocket.x < 0)
            rocket.x = 0;
        else if(rocket.x > SCREEN_X - rocket.width)
            rocket.x = SCREEN_X - rocket.width;
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
}

