package me.austinatchley;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Locale;

import me.austinatchley.States.LoadingState;
import me.austinatchley.States.MenuState;
import me.austinatchley.Tools.Utils;

public class RocketGame extends Game {

    private GameStateManager gsm;
    SpriteBatch batch;
    BitmapFont font;

    public I18NBundle strings;

    public AssetManager manager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/FFF.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        font = generator.generateFont(parameter);

        //		font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"),
        //				Gdx.files.internal("fonts/test.png"),
        //				false);

        strings = I18NBundle.createBundle(
                Gdx.files.internal("strings/strings"),
                Locale.getDefault());

        gsm = new GameStateManager(this);
        gsm.push(new LoadingState(gsm, this));
    }

    @Override
    public void render() {
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
