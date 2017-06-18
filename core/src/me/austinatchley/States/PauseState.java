package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.austinatchley.GameStateManager;


public class PauseState extends State {
    protected PauseState(GameStateManager gsm) {
        super(gsm);

        camera.setToOrtho(false, WIDTH, HEIGHT);
    }

    @Override
    protected void handleInput() {
        gsm.pop();
    }

    @Override
    public void update(float dt) {
        if(Gdx.input.justTouched())
            handleInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        font.draw(batch, "Flynn has paused the game.", WIDTH / 2 - 250, HEIGHT / 2);

        batch.end();
    }

    @Override
    public void dispose() {

    }
}
