package me.austinatchley.Objects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.States.GameState;

/**
 * Created by austi on 6/18/2017.
 */

public abstract class SpaceObject {
    static final float DEG2RAD = MathUtils.degreesToRadians;

    Body body;
    Texture image;
    Sprite sprite;
    World world;

    public SpaceObject(World world){
        this.world = world;
    }

    abstract void init();

    public void render(SpriteBatch batch){
        float posX = body.getPosition().x;
        float posY = body.getPosition().y;
        float rotation = (float) Math.toDegrees(body.getAngle());
        sprite.setPosition(posX, posY);
        sprite.setRotation(rotation);

        // Then we simply draw it as a normal sprite.
        sprite.draw(batch);
    }

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


    /*
    Returns position of SpaceObject in pixels
     */
    public Vector2 getPosition(){
        if(body == null)
            return null;
        return GameState.m2p(body.getPosition());
    }

    /*
    Returns position of SpaceObject body in meters
     */
    public Vector2 getBodyPosition(){
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
        body.setTransform(GameState.p2m(pos), angle);
    }

    public void setTransform(float x, float y, float angle){
        if(body == null)
            return;
        body.setTransform(GameState.p2m(x, y), angle);
    }

    public void dispose(){
        image.dispose();
        world.destroyBody(body);
    }

}
