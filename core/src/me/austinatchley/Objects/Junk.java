package me.austinatchley.Objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.States.GameState;
import me.austinatchley.States.State;

public class Junk extends Obstacle {

    public Junk(World world){
        super(world);
    }

    @Override
    public void init() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                GameState.p2m(
                        MathUtils.random(0, State.WIDTH - 90),
                        State.HEIGHT
                )
        );

        body = world.createBody(bodyDef);
        body.setLinearDamping(MathUtils.random(0f,3f));
        body.setAngularVelocity(MathUtils.random(-2f, 2f));

        CircleShape shape = new CircleShape();
        shape.setRadius(10f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = 0x0001;
        fixtureDef.filter.maskBits = 0x0002;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("Junk");
    }
}
