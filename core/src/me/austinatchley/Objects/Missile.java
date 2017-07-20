package me.austinatchley.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


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

        //TODO: GET THIS TO APPLY MORE FORCE BY CREATING A CONVERSION FACTOR
        body.applyLinearImpulse(new Vector2(dx,dy), body.getWorldCenter(), true);
    }

    @Override
    void init() {
        BodyDef missileBodyDef = new BodyDef();
        missileBodyDef.type = BodyDef.BodyType.DynamicBody;
        missileBodyDef.position.set(start);
        missileBodyDef.bullet = true;

        body = world.createBody(missileBodyDef);

        MassData missileMassData = new MassData();
        missileMassData.mass = 1f;
        body.setMassData(missileMassData);
        body.setUserData("Missile");
        body.setGravityScale(0f);

        PolygonShape missileShape = new PolygonShape();
        missileShape.setAsBox(image.getWidth() / 2, image.getHeight() / 2);

        FixtureDef missileFixtureDef = new FixtureDef();
        missileFixtureDef.shape = missileShape;
        missileFixtureDef.isSensor = true;

        Fixture missileFixture = body.createFixture(missileFixtureDef);
        missileFixture.setUserData("Missile");
        missileShape.dispose();
    }

    @Override
    public void render(SpriteBatch batch) {
        float posX = body.getPosition().x;
        float posY = body.getPosition().y;
        float rotation = body.getAngle() / DEG2RAD;

        sprite.setPosition(posX, posY);
        sprite.setRotation(rotation);

        // Then we simply draw it as a normal sprite.
        sprite.draw(batch);
    }

    public boolean isOutOfBounds(){
        return body.getPosition().y < -sprite.getHeight() || body.getPosition().y > Gdx.graphics.getHeight() ||
                body.getPosition().x < -sprite.getWidth() || body.getPosition().x > Gdx.graphics.getWidth();
    }

    public void flip(){
        body.setTransform(body.getPosition(), MathUtils.PI);
    }
}
