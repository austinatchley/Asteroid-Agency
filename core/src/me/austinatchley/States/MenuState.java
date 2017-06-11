package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.austinatchley.GameStateManager;


public class MenuState extends State {
    private Texture playButton;
    private Texture name;
    private Texture background;

    public MenuState(GameStateManager gsm){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        playButton = new Texture("playbutton.png");
        name = new Texture("name.png");
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0.4f, 0.6f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(playButton, (WIDTH - playButton.getWidth()) / 2,
                (HEIGHT - playButton.getHeight()) / 2);
        batch.draw(name, (WIDTH - name.getWidth()) / 2,
                (HEIGHT - name.getHeight()) * 2 / 3);
        batch.end();

//        camera.update();
//        batch.setProjectionMatrix(camera.combined);

//        if (Gdx.input.isTouched()) {
//            game.setScreen(new GameState(game));
//            dispose();
//        }
    }

    @Override
    public void dispose() {
        playButton.dispose();
    }


    @Override
    protected void handleInput() {
        gsm.set(new GameState(gsm));
    }

    @Override
    public void update(float dt) {
        if(Gdx.input.justTouched())
            handleInput();
    }
}
