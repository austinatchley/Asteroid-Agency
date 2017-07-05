package me.austinatchley.Objects;

import com.badlogic.gdx.Gdx;
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


public class Missile extends SpaceObject {
    Vector2 start;
    float dx,dy;

    public Missile(World world, Vector2 start){
        super(world);
        image = new Texture("shot.png");
        sprite = new Sprite(image);
        this.start = start;
        init();
    }

    public Missile(World world, Vector2 start, float dx, float dy){
        this(world, start);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void init() {
        BodyDef missileBodyDef = new BodyDef();
        missileBodyDef.type = BodyDef.BodyType.KinematicBody;
        missileBodyDef.position.set(start);

        body = world.createBody(missileBodyDef);

        MassData missileMassData = new MassData();
        missileMassData.mass = 10f;
        body.setMassData(missileMassData);
        body.setUserData("Missile");

        PolygonShape missileShape = new PolygonShape();
        missileShape.setAsBox(image.getWidth()/2, image.getHeight());

        FixtureDef missileFixtureDef = new FixtureDef();
        missileFixtureDef.shape = missileShape;

        Fixture missileFixture = body.createFixture(missileFixtureDef);
        missileFixture.setUserData("Missile");
        missileShape.dispose();
    }

    @Override
    public void render(SpriteBatch batch) {
        float posX = body.getPosition().x;
        float posY = body.getPosition().y;
        float rotation = (float) Math.toDegrees(body.getAngle());
        sprite.setPosition(posX, posY);
        sprite.setRotation(rotation);

        // Then we simply draw it as a normal sprite.
        sprite.draw(batch);

//        System.out.println(body.getPosition().x + ", " + body.getPosition().y);

        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float dt){
        body.setTransform(body.getPosition().x + dt*dx,
                body.getPosition().y + dt*dy,
                body.getAngle());
    }
}
