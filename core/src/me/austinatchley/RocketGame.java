package me.austinatchley;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.austinatchley.States.*;
import me.austinatchley.States.MenuState;

public class RocketGame extends Game {

	private GameStateManager gsm;
	SpriteBatch batch;
	BitmapFont font;
	Music gameMusic;

	@Override
	public void create() {
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        gameMusic.setVolume(.75f);
        gameMusic.setLooping(true);
        gameMusic.play();

		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"),
				Gdx.files.internal("fonts/test.png"),
				false);

		gsm = new GameStateManager();
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
