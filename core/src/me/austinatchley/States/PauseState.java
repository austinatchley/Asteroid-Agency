package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import org.w3c.dom.css.Rect;

import me.austinatchley.GameStateManager;
import me.austinatchley.Starfield;
import me.austinatchley.Utils;

import static me.austinatchley.Utils.BG_COLOR;
import static me.austinatchley.Utils.HEIGHT;
import static me.austinatchley.Utils.WIDTH;


public class PauseState extends InterfaceState {
    private Starfield starfield;

    private PauseState(final GameStateManager gsm) {
        super(gsm);

        final Label pauseText = new Label("PAUSED", skin, "subtitle");
        table.add(pauseText).spaceBottom(pauseText.getPrefHeight() / 2);

        table.row();

        final Drawable buttonImage = new TextureRegionDrawable(new TextureRegion(new Texture("flynnhead.png")));
        final ImageButton playButton = new ImageButton(buttonImage);

        table.add(playButton);
        table.row();

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.pop();
            }
        });

        final Label playText = new Label("Press to Resume", skin, "h2");

        table.add(playText).spaceTop(playText.getPrefHeight() / 2);
    }

    public PauseState(GameStateManager gsm, Starfield starfield) {
        this(gsm);
        this.starfield = starfield;
        this.starfield.useVelocity(false);
    }

    @Override
    protected void handleInput() {
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        starfield.render();

        stage.draw();
    }
}
