package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.w3c.dom.css.Rect;

import me.austinatchley.GameStateManager;
import me.austinatchley.Starfield;


public class PauseState extends State {
    private GlyphLayout pauseLayout;
    private String pauseText;

    private Texture playButton;
    private Vector2 playButtonLocation;
    private Rectangle playButtonBounds;

    private Starfield starfield;

    private Color bgColor;

    protected PauseState(GameStateManager gsm) {
        super(gsm);

        camera.setToOrtho(false, WIDTH, HEIGHT);

        init();

        starfield = new Starfield(400, camera, null);
    }

    private void init() {
        pauseLayout = new GlyphLayout();
        pauseText = "FLYNN PAUSED\nGET REKT TO RESUME";

        playButton = new Texture("flynnhead.png");
        playButtonLocation = new Vector2((WIDTH - playButton.getWidth()) / 2, 0);
        playButtonBounds = new Rectangle(playButtonLocation.x, HEIGHT - playButton.getHeight(),
                playButton.getWidth(), playButton.getHeight());

        bgColor = new Color(0x0E103DFF);
    }

    protected PauseState(GameStateManager gsm, Starfield starfield) {
        super(gsm);

        init();

        this.starfield = starfield;
        this.starfield.rocket = null;
    }

    @Override
    protected void handleInput() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        if(playButtonBounds.contains(touchPos.x, touchPos.y))
            gsm.pop();
    }

    @Override
    public void update(float dt) {
        if(Gdx.input.justTouched())
            handleInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        starfield.render();

        batch.begin();
        pauseLayout.setText(font, pauseText);
        font.draw(batch, pauseLayout, (WIDTH - pauseLayout.width)/ 2, HEIGHT * (3f/4f));
        batch.draw(playButton, playButtonBounds.x, HEIGHT - playButtonBounds.y - playButton.getHeight());

        batch.end();
    }

    @Override
    public void dispose() {

    }
}
