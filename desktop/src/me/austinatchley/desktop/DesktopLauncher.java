package me.austinatchley.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import me.austinatchley.RocketGame;
import me.austinatchley.Tools.Utils;

public class DesktopLauncher {
	private static final int WIDTH = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
	private static final int HEIGHT = LwjglApplicationConfiguration.getDesktopDisplayMode().height;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();


        int actualWidth  = 540;
        int actualHeight = 960;

        if (actualWidth > WIDTH || actualHeight > HEIGHT) {
            actualWidth = (int)(actualWidth / ((float)actualHeight / HEIGHT));
            actualHeight = HEIGHT;
        }

		config.fullscreen = false;
        config.width = actualWidth;
        config.height = actualHeight;
		config.title = "Asteroid Agency";
		config.resizable = false;

		new LwjglApplication(new RocketGame(), config);

		Utils.WIDTH = actualWidth;
		Utils.HEIGHT = actualHeight;
		Utils.IS_DESKTOP = true;
	}
}
