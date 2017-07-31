package me.austinatchley;

import com.badlogic.gdx.math.Vector2;

public class Utils {
    public static final float PPM = 1/8f;

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
