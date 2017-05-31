package me.austinatchley;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class RocketGame extends ApplicationAdapter {
	private final int SCREEN_X = 1080;
	private final int SCREEN_Y = 1920;
	private final int VERTICAL_OFF = 20;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle rocket;
	private Texture rocketImage;


	
	@Override
	public void create () {
		batch = new SpriteBatch();
		rocketImage = new Texture("rocket.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_X, SCREEN_Y);
		batch = new SpriteBatch();

		rocket = new Rectangle();
		rocket.x = (SCREEN_X - rocket.getWidth())/ 2;
		rocket.y = VERTICAL_OFF;
		rocket.width = rocketImage.getWidth();
		rocket.height = rocketImage.getHeight();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, .2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(rocketImage, rocket.x, rocket.y);
		batch.end();

		if(Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			rocket.x = touchPos.x - rocket.width / 2;
		}

		if(rocket.x < 0)
			rocket.x = 0;
		else if(rocket.x > SCREEN_X - rocket.width)
			rocket.x = SCREEN_X - rocket.width;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		rocketImage.dispose();
	}
}
