package me.austinatchley.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.Tools.Utils;

import static me.austinatchley.Tools.Utils.HEIGHT;
import static me.austinatchley.Tools.Utils.WIDTH;


public class Missile extends SpaceObject {
    Vector2 start;
    float dx,dy;
    String tag;

    public Missile(World world, Vector2 start){
        super(world);
        image = new Texture("shot.png");
        sprite = new Sprite(image);
        this.start = start;
        this.tag = "Missile";
        init();
    }

    public Missile(World world, Vector2 start, float dx, float dy){
        this(world, start);
        this.dx = dx;
        this.dy = dy;

        body.applyLinearImpulse(new Vector2(dx,dy), body.getWorldCenter(), true);
//        body.setLinearVelocity(new Vector2(dx,dy));
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
        body.setUserData(tag);
           body.setGravityScale(0f);

        PolygonShape missileShape = new PolygonShape();
        Vector2 boxSize = Utils.p2m(image.getWidth() / 2, image.getHeight() / 2);
        missileShape.setAsBox(boxSize.x, boxSize.y);

        FixtureDef missileFixtureDef = new FixtureDef();
        missileFixtureDef.shape = missileShape;
        missileFixtureDef.isSensor = true;

        missileFixtureDef.filter.categoryBits = 0x0001;
        missileFixtureDef.filter.maskBits = 0x0006;

        Fixture missileFixture = body.createFixture(missileFixtureDef);
        missileFixture.setUserData(tag);
        missileShape.dispose();
    }

    public boolean isOutOfBounds(){
        return getPosition().y < -sprite.getHeight() || getPosition().y > HEIGHT ||
                getPosition().x < -sprite.getWidth() || getPosition().x > WIDTH;
    }

    public void flip(){
        body.setTransform(body.getPosition(), MathUtils.PI);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
