package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.austinatchley.GameStateManager;

public abstract class State {
    protected OrthographicCamera camera;
    protected GameStateManager gsm;
    protected BitmapFont font;

    protected State(GameStateManager gsm){
        this.gsm = gsm;
        camera = new OrthographicCamera();
        font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"),
                Gdx.files.internal("fonts/test.png"),
                false);
    }

    protected abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);
    public abstract void dispose();
}
