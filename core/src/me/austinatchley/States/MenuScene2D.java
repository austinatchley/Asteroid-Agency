package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import me.austinatchley.GameStateManager;
import me.austinatchley.Starfield;
import me.austinatchley.Utils;

import static me.austinatchley.Utils.BG_COLOR;
import static me.austinatchley.Utils.HEIGHT;
import static me.austinatchley.Utils.WIDTH;

public class MenuScene2D extends State {
    private Stage stage;
    private Table table;
    private Skin skin;

    private Label playLabel;

    private Starfield starfield;

    private float elapsedTime;
    private boolean visible;

    public MenuScene2D(final GameStateManager gsm){
        super(gsm);

        skin = gsm.getSkin();

        camera.setToOrtho(false, WIDTH, HEIGHT);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
//        table.setDebug(true);

        Label titleLabel = new Label("Asteroid Agency", skin, "title");
        table.add(titleLabel).spaceBottom(titleLabel.getPrefHeight() / 2f);
        table.row();

        Drawable buttonImage = new TextureRegionDrawable(new TextureRegion(new Texture("playbutton.png")));
        final ImageButton playButton = new ImageButton(buttonImage);

        table.add(playButton);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new GameState(gsm, starfield));
            }
        });
        table.row();

        playLabel = new Label("Tap to Play", skin, "subtitle");
        table.add(playLabel).spaceTop(playLabel.getPrefHeight() / 2f);

        starfield = new Starfield(400, camera, null);
        starfield.useVelocity(false);

        elapsedTime = 0f;
        visible = true;
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        stage.act(Gdx.graphics.getDeltaTime());

        elapsedTime += dt;
        if(elapsedTime >= .4f){
            visible = !visible;
            playLabel.setVisible(visible);
            elapsedTime = 0;
        }
    }

    @Override
    public void render (SpriteBatch batch) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, BG_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        starfield.render();

        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}