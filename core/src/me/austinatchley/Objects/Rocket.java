package me.austinatchley.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.States.State;

public class Rocket extends SpaceObject {
    private static final int VERTICAL_OFF = 20;

    public Rocket(World world){
        super(world);
        image = new Texture("rocket.png");
        sprite = new Sprite(image);
        initialize();
    }

    public void render(SpriteBatch batch){
        float posX = body.getPosition().x;
        float posY = body.getPosition().y;
        float rotation = (float) Math.toDegrees(body.getAngle());
        sprite.setPosition(posX, posY);
        sprite.setRotation(rotation);

        // Then we simply draw it as a normal sprite.
        sprite.draw(batch);
    }

    public void initialize() {
        BodyDef rocketBodyDef = new BodyDef();
        rocketBodyDef.type = BodyDef.BodyType.KinematicBody;
        rocketBodyDef.position.set((State.WIDTH - image.getWidth())/ 2, VERTICAL_OFF);

        body = world.createBody(rocketBodyDef);

        MassData rocketMassData = new MassData();
        rocketMassData.mass = 0;
        body.setMassData(rocketMassData);

        PolygonShape rocketShape = new PolygonShape();
        rocketShape.setAsBox(image.getWidth()/2, image.getHeight());

        FixtureDef rocketFixtureDef = new FixtureDef();
        rocketFixtureDef.shape = rocketShape;

        Fixture rocketFixture = body.createFixture(rocketFixtureDef);
        rocketFixture.setUserData("Rocket");
        rocketShape.dispose();
    }
}
