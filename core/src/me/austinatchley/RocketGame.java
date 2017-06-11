package me.austinatchley;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.austinatchley.States.*;
import me.austinatchley.States.MenuState;

public class RocketGame extends Game {

	private GameStateManager gsm;
	SpriteBatch batch;
	BitmapFont font;

	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"),
				Gdx.files.internal("fonts/test.png"),
				false);
		gsm = new GameStateManager();
		Gdx.gl.glClearColor(0.4f, 0.6f, 1f, 1);
		gsm.push(new MenuState(gsm));
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
