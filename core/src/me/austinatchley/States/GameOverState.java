package me.austinatchley.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import me.austinatchley.GameStateManager;
import me.austinatchley.Starfield;

import static me.austinatchley.Utils.HEIGHT;
import static me.austinatchley.Utils.WIDTH;


public class GameOverState extends State {
    private Texture playButton;
    private Texture gameOverImage;
    private Texture start;
    private Animation<TextureRegion> blinking;

    private float stateTime;
    private Vector2 playLocation;

    private Vector2 gameOverLocation;
    private Vector2 startLocation;
    private Rectangle playBounds;

    private Starfield starfield;

    private int score;
    private GlyphLayout scoreLayout;
    private GlyphLayout highScoreLayout;

    public GameOverState(GameStateManager gsm){
        super(gsm);

        playButton = new Texture("playbutton.png");
        gameOverImage = new Texture("gameover.png");
        start = new Texture("start.png");

        gameOverLocation = new Vector2((WIDTH - gameOverImage.getWidth()) / 2,
                HEIGHT - gameOverImage.getHeight() * 2.5f);
        playLocation = new Vector2((WIDTH - playButton.getWidth()) / 2,
                (HEIGHT - playButton.getHeight()) / 2);
        playBounds = new Rectangle(playLocation.x, playLocation.y,
                playButton.getWidth(), playButton.getHeight());
        startLocation = new Vector2((WIDTH - start.getWidth() / 2) / 2,
                playLocation.y - (start.getHeight() * 1.5f));

        starfield = new Starfield(400, camera, null);

        TextureRegion[][] tmpFrames = TextureRegion.split(start, start.getWidth()/2, start.getHeight());
        TextureRegion[] frames = new TextureRegion[2];
        int index = 0;
        for (TextureRegion[] tmpFrame : tmpFrames)
            for (int j = 0; j < tmpFrames[0].length; j++)
                frames[index++] = tmpFrame[j];

        blinking = new Animation<TextureRegion>(.4f, frames);
        stateTime = 0;

        font.setColor(new Color(0xD3BCC0FF));
        scoreLayout = new GlyphLayout();
        scoreLayout.setText(font, "Score: " + score);
        highScoreLayout = new GlyphLayout();
        highScoreLayout.setText(font, "High Score: " + gsm.getHighScore());

        starfield.useVelocity(false);
    }

    public GameOverState(GameStateManager gsm, int score){
        this(gsm);
        this.score = score;
        scoreLayout.setText(font, "Score: " + score);
    }

    @Override
    public void render(SpriteBatch batch) {
        Color bgColor = new Color(0x0E103DFF);
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        starfield.render();

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion startCurrentFrame = blinking.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(playButton, playLocation.x, playLocation.y);
        batch.draw(gameOverImage, gameOverLocation.x, gameOverLocation.y);
        batch.draw(startCurrentFrame, startLocation.x, startLocation.y);

        float scoreX = (WIDTH - scoreLayout.width) / 2f;
        float scoreY = gameOverLocation.y - scoreLayout.height * 3;
        font.draw(batch, scoreLayout, scoreX, scoreY);
        float highScoreX = (WIDTH - highScoreLayout.width) / 2f;
        float highScoreY = scoreY - highScoreLayout.height * 1.5f;
        font.draw(batch, highScoreLayout, highScoreX, highScoreY);

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
        if(playBounds.contains(touchPos)) {
            gsm.gameMusic.setVolume(.5f);
            gsm.set(new GameState(gsm, starfield));
        }
    }

    @Override
    public void update(float dt) {
        if(Gdx.input.justTouched())
            handleInput();
    }
}
