package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.austinatchley.GameStateManager;


public class MenuState extends State {
    private Texture playButton;
    private Texture name;
    private Texture start;

    private Animation<TextureRegion> blinking;
    private float stateTime;

    private Vector2 playLocation;
    private Vector2 nameLocation;
    private Vector2 startLocation;

    private Rectangle playBounds;

    public MenuState(GameStateManager gsm){
        super(gsm);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        playButton = new Texture("playbutton.png");
        start = new Texture("start.png");
        name = new Texture("name.png");

        playLocation = new Vector2((WIDTH - playButton.getWidth()) / 2,
                (HEIGHT - playButton.getHeight()) / 2);
        nameLocation = new Vector2((WIDTH - name.getWidth()) / 2,
                (HEIGHT - name.getHeight()) * 2 / 3);
        startLocation = new Vector2((WIDTH - start.getWidth() / 2) / 2,
                playLocation.y - 150 );

        playBounds = new Rectangle(playLocation.x, playLocation.y,
                playButton.getWidth(), playButton.getHeight());

        TextureRegion[][] tmpFrames = TextureRegion.split(start, start.getWidth()/2, start.getHeight());
        TextureRegion[] frames = new TextureRegion[2];
        int index = 0;
        for (TextureRegion[] tmpFrame : tmpFrames)
            for (int j = 0; j < tmpFrames[0].length; j++)
                frames[index++] = tmpFrame[j];

        blinking = new Animation<TextureRegion>(.4f, frames);
        stateTime = 0;
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0.4f, 0.6f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion startCurrentFrame = blinking.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(playButton, playLocation.x, playLocation.y);
        batch.draw(name, nameLocation.x, nameLocation.y);
        batch.draw(startCurrentFrame, startLocation.x, startLocation.y);
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
