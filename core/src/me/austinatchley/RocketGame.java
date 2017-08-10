package me.austinatchley;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import me.austinatchley.States.MenuState;

public class RocketGame extends Game {

	private GameStateManager gsm;
	SpriteBatch batch;
	BitmapFont font;

	@Override
	public void create() {
		batch = new SpriteBatch();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/FFF.TTF"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 36;
		font = generator.generateFont(parameter);

//		font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"),
//				Gdx.files.internal("fonts/test.png"),
//				false);


		gsm = new GameStateManager();
//		gsm.push(new MenuState(gsm));
		gsm.push(new MenuState(gsm));
	}

	@Override
	public void render() {
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		super.dispose();
	}
}
