package me.austinatchley;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Stack;

import me.austinatchley.States.State;

import static me.austinatchley.Tools.Utils.SCORE;

public class GameStateManager {

    public RocketGame game;

    private static Preferences pref;

    private Stack<State> states;

    public static Music gameMusic;
    private static BitmapFont font;
    private static Skin skin;

    public GameStateManager(RocketGame game) {
        this.game = game;

        states = new Stack<State>();

        pref = Gdx.app.getPreferences("PreferenceName");

        if (!pref.contains(SCORE)) pref.putInteger(SCORE, 0);

        font = generateFont(me.austinatchley.Tools.Utils.DEFAULT_FONT_SIZE);
    }

    public void push(State state) {
        states.push(state);
    }

    public State pop() {
        peek().dispose();
        return states.pop();
    }

    public void set(State state) {
        states.pop();
        states.push(state);
    }

    public State peek() {
        return states.peek();
    }

    public void update(float dt) {
        states.peek().update(dt);
    }

    public void render(SpriteBatch batch) {
        states.peek().render(batch);
    }

    public int getHighScore() {
        return pref.getInteger(SCORE);
    }

    public void setHighScore(int score) {
        pref.putInteger(SCORE, score);
        pref.flush();
    }

    public void tryHighScore(int score) {
        if (score > pref.getInteger(SCORE)) setHighScore(score);
    }

    public void generateMusic(float volume) {
        Music music = game.manager.get("music.mp3", Music.class);
        music.setVolume(volume);
        music.setLooping(true);
        music.play();
        gameMusic = music;
    }

    public BitmapFont getFont() {
        if (font == null) font = generateFont(me.austinatchley.Tools.Utils.DEFAULT_FONT_SIZE);
        return font;
    }

    public BitmapFont generateFont(int size) {
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/FFF.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        return generator.generateFont(parameter);
    }

    public Skin getSkin() {
        if (skin == null) generateSkin();
        return skin;
    }

    public void generateSkin() {
        Skin newSkin = new Skin();

        newSkin.add("button-font", font, BitmapFont.class);
        newSkin.add("text-color", me.austinatchley.Tools.Utils.TEXT_COLOR, Color.class);
        newSkin.add("title-font", generateFont(96), BitmapFont.class);
        newSkin.add("subtitle-font", generateFont(72), BitmapFont.class);
        newSkin.add("h2-font", generateFont(48), BitmapFont.class);
        newSkin.add("text-font", generateFont(36), BitmapFont.class);

        newSkin.addRegions(game.manager.get("uiskin.atlas", TextureAtlas.class));
        newSkin.load(Gdx.files.internal("uiskin.json"));

        skin = newSkin;
    }
}
