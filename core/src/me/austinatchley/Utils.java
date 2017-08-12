package me.austinatchley;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Utils {
    public static final float PPM = 1/8f;

    public static boolean IS_DESKTOP = false;
    public static int WIDTH = Gdx.graphics.getWidth();
    public static int HEIGHT = Gdx.graphics.getHeight();

    public static final int NUM_ASTEROID_SPRITES = 64;
    public static final float FRAME_TIME = .06f;
    public static final int ENEMY_LIMIT = 10;
    public static final int ASTEROID_LIMIT = 16;

    public static final Color TEXT_COLOR = new Color(0xD3BCC0FF);
    public static final Color BG_COLOR = new Color(0x0E103DFF);

    public static final int DEFAULT_FONT_SIZE = 36;

    public static final float MOVE_DIST = 25f;

    /*
    Converts meters to pixels for use with LibGDX
    @param  xMeters float x distance in meters
    @param  yMeters float y distance in meters
    @return Vector2 representation of distance in pixels
     */
    public static Vector2 m2p(float xMeters, float yMeters){
        return new Vector2(xMeters / PPM, yMeters / PPM);
    }


    public static Vector2 m2p(Vector2 meters){
        return new Vector2(meters.x / PPM, meters.y / PPM);
    }

    /*
    Converts pixels to meters for use with Box2D
    @param  xPixels float x distance in pixels
    @param  yPixels float y distance in pixels
    @return Vector2 representation of distance in meters
     */
    public static Vector2 p2m(float xPixels, float yPixels){
        return new Vector2(xPixels * PPM, yPixels * PPM);
    }

    public static Vector2 p2m(Vector2 pixels){
        return new Vector2(pixels.x * PPM, pixels.y * PPM);
    }

}
