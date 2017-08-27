package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import me.austinatchley.GameStateManager;
import me.austinatchley.RocketGame;

import static me.austinatchley.Tools.Utils.BG_COLOR;
import static me.austinatchley.Tools.Utils.HEIGHT;
import static me.austinatchley.Tools.Utils.TEXT_COLOR;
import static me.austinatchley.Tools.Utils.WIDTH;

public class LoadingState extends State {
    private RocketGame game;
    private ShapeRenderer renderer;

    public LoadingState(GameStateManager gsm, RocketGame game){
        super(gsm);

        this.game = game;

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setColor(new Color(TEXT_COLOR));

        game.manager = new AssetManager();
        load();
    }

    private void load() {
        game.manager.load("asteroid.png", Texture.class);
        game.manager.load("pause.png", Texture.class);
        game.manager.load("playbutton.png", Texture.class);
        game.manager.load("rocks.png", Texture.class);
        game.manager.load("shot.png", Texture.class);
        game.manager.load("spaceCraft1.png", Texture.class);
        game.manager.load("spaceCraft4.png", Texture.class);

        game.manager.load("shoot.wav", Sound.class);
        game.manager.load("enemyshoot.wav", Sound.class);
        game.manager.load("gameover.wav", Sound.class);
        game.manager.load("explosion.wav", Sound.class);
        game.manager.load("music.mp3", Music.class);

        game.manager.load("uiskin.atlas", TextureAtlas.class);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, BG_COLOR.a);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        if(game.manager.update()) {
            game.manager.finishLoading();
            gsm.generateMusic(.5f);
            gsm.generateSkin();
            gsm.push(new MenuState(gsm));
        }

        float progress = game.manager.getProgress();


        renderer.begin();
        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.box(WIDTH/2 - 250,HEIGHT/2,0,progress*500, 40, 0);
        renderer.end();
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void dispose() {

    }
}
