package me.austinatchley.States;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import me.austinatchley.GameStateManager;

import static me.austinatchley.Utils.HEIGHT;
import static me.austinatchley.Utils.WIDTH;

public abstract class State {
    public OrthographicCamera camera;
    protected final GameStateManager gsm;
    protected BitmapFont font;
    private Skin skin;

    protected State(GameStateManager gsm){
        this.gsm = gsm;
        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.setToOrtho(false, WIDTH, HEIGHT);

        font = gsm.getFont();
        skin = gsm.getSkin();
    }

    protected abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);
    public abstract void dispose();
}
