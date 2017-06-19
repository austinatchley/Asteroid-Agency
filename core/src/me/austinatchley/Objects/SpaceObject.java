package me.austinatchley.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by austi on 6/18/2017.
 */

public abstract class SpaceObject {
    Body body;
    Texture image;
    Sprite sprite;
    World world;

    public SpaceObject(World world){
        this.world = world;
    }

    abstract void initialize();
    abstract void render(SpriteBatch batch);

    public float getWidth(){
        return image.getWidth();
    }

    public float getHeight(){
        if(image == null)
            return 0f;
        return image.getHeight();
    }

    public Body getBody(){
        return body;
    }

    public Vector2 getPosition(){
        if(body == null)
            return null;
        return body.getPosition();
    }

    public float getAngle(){
        if(body == null)
            return 0f;
        return body.getAngle();
    }

    public void setTransform(Vector2 pos, float angle){
        if(body == null)
            return;
        body.setTransform(pos, angle);
    }

    public void setTransform(float x, float y, float angle){
        if(body == null)
            return;
        body.setTransform(x, y, angle);
    }

    public void dispose(){
        image.dispose();
    }

}
