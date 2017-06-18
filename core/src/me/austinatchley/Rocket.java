package me.austinatchley;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.States.State;

public class Rocket {
    private static final int VERTICAL_OFF = 20;

    private Body rocket;
    private Texture rocketImage;
    private Sprite rocketSprite;

    public Rocket(World world){
        rocketImage = new Texture("rocket.png");
        rocketSprite = new Sprite(rocketImage);
        initializeRocket(world);
    }

    public void render(SpriteBatch batch){
        float posX = rocket.getPosition().x;
        float posY = rocket.getPosition().y;
        float rotation = (float) Math.toDegrees(rocket.getAngle());
        rocketSprite.setPosition(posX, posY);
        rocketSprite.setRotation(rotation);

        // Then we simply draw it as a normal sprite.
        rocketSprite.draw(batch);
    }

    private void initializeRocket(World world) {
        BodyDef rocketBodyDef = new BodyDef();
        rocketBodyDef.type = BodyDef.BodyType.KinematicBody;
        rocketBodyDef.position.set((State.WIDTH - rocketImage.getWidth())/ 2, VERTICAL_OFF);

        rocket = world.createBody(rocketBodyDef);

        MassData rocketMassData = new MassData();
        rocketMassData.mass = 0;
        rocket.setMassData(rocketMassData);

        PolygonShape rocketShape = new PolygonShape();
        rocketShape.setAsBox(rocketImage.getWidth()/2, rocketImage.getHeight());

        FixtureDef rocketFixtureDef = new FixtureDef();
        rocketFixtureDef.shape = rocketShape;

        Fixture rocketFixture = rocket.createFixture(rocketFixtureDef);
        rocketFixture.setUserData("Rocket");
        rocketShape.dispose();
    }

    public float getWidth(){
        return rocketImage.getWidth();
    }

    public float getHeight(){
        return rocketImage.getHeight();
    }

    public Vector2 getPosition(){
        return rocket.getPosition();
    }

    public void setTransform(Vector2 pos, float angle){
        rocket.setTransform(pos, angle);
    }

    public void setTransform(float x, float y, float angle){
        rocket.setTransform(x, y, angle);
    }

    public void dispose(){

    }
}
