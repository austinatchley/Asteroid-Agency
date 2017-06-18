package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.austinatchley.GameStateManager;


public class MenuState extends State {
    private Texture playButton;
    private Texture name;
    private Vector2 playLocation;
    private Vector2 nameLocation;
    private Rectangle playBounds;

    public MenuState(GameStateManager gsm){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        playButton = new Texture("playbutton.png");
        name = new Texture("name.png");

        playLocation = new Vector2((WIDTH - playButton.getWidth()) / 2,
                (HEIGHT - playButton.getHeight()) / 2);
        nameLocation = new Vector2((WIDTH - name.getWidth()) / 2,
                (HEIGHT - name.getHeight()) * 2 / 3);

        playBounds = new Rectangle(playLocation.x, playLocation.y,
                playButton.getWidth(), playButton.getHeight());
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0.4f, 0.6f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(playButton, playLocation.x, playLocation.y);
        batch.draw(name, nameLocation.x, nameLocation.y);
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
        Vector2 touchPos = new Vector2();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY());
        if(playBounds.contains(touchPos))
            gsm.set(new GameState(gsm));
    }

    @Override
    public void update(float dt) {
        if(Gdx.input.justTouched())
            handleInput();
    }
}
