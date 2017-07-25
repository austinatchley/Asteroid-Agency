package me.austinatchley.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.States.GameState;
import me.austinatchley.States.State;

public class Asteroid extends SpaceObject {
    private static final int NUM_ASTEROID_SPRITES = 64;
    private Animation<TextureRegion> animation;


    public Asteroid(World world){
        super(world);
        image = new Texture("asteroid.png");
        sprite = new Sprite(image);
        init();
    }

    public Asteroid(World world, Animation<TextureRegion> animation){
        this(world);
        this.animation = animation;
    }

    public void init() {
        BodyDef asteroidBodyDef = new BodyDef();
        asteroidBodyDef.type = BodyDef.BodyType.DynamicBody;
        asteroidBodyDef.position.set(
                GameState.p2m(
                        MathUtils.random(0, State.WIDTH - 90),
                        State.HEIGHT
                )
        );

        body = world.createBody(asteroidBodyDef);
//        body.setLinearDamping(MathUtils.random(0f,3f));
        body.setAngularVelocity(MathUtils.random(-5f, 5f));

        CircleShape asteroidShape = new CircleShape();
        asteroidShape.setRadius(10f);

        FixtureDef asteroidFixtureDef = new FixtureDef();
        asteroidFixtureDef.shape = asteroidShape;
        asteroidFixtureDef.density = 1f;

        Fixture asteroidFixture = body.createFixture(asteroidFixtureDef);
        asteroidFixture.setUserData("Asteroid");
    }

    public void render(SpriteBatch batch){
        Vector2 pos = getPosition();
        float rotation = (float) Math.toDegrees(body.getAngle());
        sprite.setPosition(pos.x - image.getWidth()/2f, pos.y - image.getHeight()/2f);
        sprite.setRotation(rotation);

        // Then we simply draw it as a normal sprite.
        sprite.draw(batch);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
