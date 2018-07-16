package me.austinatchley.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.Tools.Utils;

public class Asteroid extends SpaceObject {
    private static final int NUM_ASTEROID_SPRITES = 64;
    private Animation<TextureRegion> animation;

    public Asteroid(World world) {
        super(world);
        image = new Texture("meteorFull.png");
        sprite = new Sprite(image);
        init();
    }

    public Asteroid(World world, Animation<TextureRegion> animation) {
        this(world);
        this.animation = animation;
    }

    public void init() {
        BodyDef asteroidBodyDef = new BodyDef();
        asteroidBodyDef.type = BodyDef.BodyType.DynamicBody;
        asteroidBodyDef.position.set(
                Utils.p2m(
                        MathUtils.random(0, Utils.WIDTH - image.getWidth()),
                        Utils.HEIGHT + image.getHeight()));

        body = world.createBody(asteroidBodyDef);
        body.setLinearDamping(MathUtils.random(0f, 3f));
        body.setAngularVelocity(MathUtils.random(-5f, 5f));

        CircleShape asteroidShape = new CircleShape();
        asteroidShape.setRadius(5f);

        FixtureDef asteroidFixtureDef = new FixtureDef();
        asteroidFixtureDef.shape = asteroidShape;
        asteroidFixtureDef.density = 1f;
        asteroidFixtureDef.filter.categoryBits = 0x0001;
        asteroidFixtureDef.filter.maskBits = 0x0002;

        Fixture asteroidFixture = body.createFixture(asteroidFixtureDef);
        asteroidFixture.setUserData("Asteroid");
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
