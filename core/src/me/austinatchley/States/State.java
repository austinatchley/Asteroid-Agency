package me.austinatchley.States;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.austinatchley.GameStateManager;
import me.austinatchley.Tools.Utils;

import static me.austinatchley.Tools.Utils.HEIGHT;
import static me.austinatchley.Tools.Utils.WIDTH;

public abstract class State {
    public OrthographicCamera camera;
    protected final GameStateManager gsm;
    protected BitmapFont font;

    protected State(GameStateManager gsm) {
        this.gsm = gsm;
        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.setToOrtho(false, WIDTH, HEIGHT);

        font = gsm.getFont();
    }

    protected abstract void handleInput();

    public abstract void update(float dt);

    public abstract void render(SpriteBatch batch);

    public abstract void dispose();
}
