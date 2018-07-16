package me.austinatchley.Objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.Tools.Utils;

public class PlayerMissile extends Missile {

    public PlayerMissile(World world, Vector2 start) {
        super(world, start);
        tag = "PMissile";
    }

    public PlayerMissile(World world, Vector2 start, float dx, float dy) {
        this(world, start);
        this.dx = dx;
        this.dy = dy;

        body.applyLinearImpulse(new Vector2(dx, dy), body.getWorldCenter(), true);
    }

    @Override
    public void init() {
        BodyDef missileBodyDef = new BodyDef();
        missileBodyDef.type = BodyDef.BodyType.DynamicBody;
        missileBodyDef.position.set(Utils.p2m(start));
        missileBodyDef.bullet = true;

        body = world.createBody(missileBodyDef);

        MassData missileMassData = new MassData();
        missileMassData.mass = 1f;
        body.setMassData(missileMassData);
        body.setUserData("PMissile");
        body.setGravityScale(0f);

        PolygonShape missileShape = new PolygonShape();
        Vector2 boxSize = Utils.p2m(image.getWidth() / 2, image.getHeight() / 2);
        missileShape.setAsBox(boxSize.x, boxSize.y);

        FixtureDef missileFixtureDef = new FixtureDef();
        missileFixtureDef.shape = missileShape;
        missileFixtureDef.isSensor = true;

        Fixture missileFixture = body.createFixture(missileFixtureDef);
        missileFixture.setUserData("PMissile");
        missileShape.dispose();
    }
}
