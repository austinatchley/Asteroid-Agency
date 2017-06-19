package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.austinatchley.GameStateManager;


public class PauseState extends State {
    private GlyphLayout pauseLayout;
    private String pauseText;

    protected PauseState(GameStateManager gsm) {
        super(gsm);

        camera.setToOrtho(false, WIDTH, HEIGHT);
        pauseLayout = new GlyphLayout();
        pauseText = "FLYNN PAUSED\nTAP TO RESUME";
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
        pauseLayout.setText(font, pauseText);
        font.draw(batch, pauseLayout, (WIDTH - pauseLayout.width)/ 2, HEIGHT * (3f/4f));

        batch.end();
    }

    @Override
    public void dispose() {

    }
}
