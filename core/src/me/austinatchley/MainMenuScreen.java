package me.austinatchley;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;



public class MainMenuScreen implements Screen {
    private final int SCREEN_X = 1080;
    private final int SCREEN_Y = 1920;
    final RocketGame game;
    OrthographicCamera camera;

    public MainMenuScreen(final RocketGame game){
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_X, SCREEN_Y);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "JANUARY SKY", SCREEN_X / 2, 2 * SCREEN_Y / 3);
        game.font.draw(game.batch, "TAP ANYWHERE TO BEGIN", SCREEN_X / 2, 2 * SCREEN_Y / 3 - 50);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            Gdx.app.log("state", "touch registered");
            game.setScreen(new GameScreen(game));
            dispose();
        }
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
    public void dispose() {

    }
}
