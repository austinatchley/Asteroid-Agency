package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.austinatchley.GameStateManager;
import me.austinatchley.Tools.Utils;

import static me.austinatchley.Tools.Utils.HEIGHT;
import static me.austinatchley.Tools.Utils.WIDTH;

public abstract class State {
    public OrthographicCamera camera;
    public Viewport viewport;

    protected final GameStateManager gsm;
    protected BitmapFont font;

    protected State(GameStateManager gsm) {
        this.gsm = gsm;

        camera = new OrthographicCamera(WIDTH * Utils.PPM, HEIGHT * Utils.PPM);

        viewport = new ScreenViewport(camera);
        viewport.apply();

        camera.setToOrtho(false, WIDTH * Utils.PPM / 2f, HEIGHT * Utils.PPM / 2f);

        font = gsm.getFont();
    }

    protected abstract void handleInput();

    public abstract void update(float dt);

    public abstract void render(SpriteBatch batch);

    public abstract void dispose();
}
