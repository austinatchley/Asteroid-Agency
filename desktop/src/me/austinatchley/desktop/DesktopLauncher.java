package me.austinatchley.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import me.austinatchley.RocketGame;
import me.austinatchley.Utils;

public class DesktopLauncher {
	private static final int WIDTH = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
	private static final int HEIGHT = LwjglApplicationConfiguration.getDesktopDisplayMode().height;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.fullscreen = true;
        config.width = WIDTH;
        config.height = HEIGHT;
		config.title = "Asteroid Agency";
		config.resizable = false;

		new LwjglApplication(new RocketGame(), config);

		Utils.HEIGHT = HEIGHT;
		Utils.WIDTH = WIDTH;
		Utils.IS_DESKTOP = true;
	}
}
