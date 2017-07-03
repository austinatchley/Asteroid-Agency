package me.austinatchley.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import me.austinatchley.States.State;

public class Enemy extends SpaceObject {
    private static final int OFFSET = 200;
    public Vector2 spawnLocation;
    private int direction;

    public Enemy(World world){
        super(world);
        image = new Texture("flynnhead.png");
        sprite = new Sprite(image);
        direction = 1;
        spawnLocation = new Vector2(MathUtils.random(State.WIDTH), State.HEIGHT - OFFSET);
        init();
    }

    public Enemy(World world, Vector2 spawnLocation){
        this(world);
        this.spawnLocation = spawnLocation;
    }

    public Enemy(World world, int numX, int numY){
        this(world);
        spawnLocation = new Vector2(numX * image.getWidth(),
                State.HEIGHT - (State.HEIGHT + numY * image.getHeight()));
    }

    @Override
    public void init() {
        BodyDef enemyBodyDef = new BodyDef();
        enemyBodyDef.type = BodyDef.BodyType.KinematicBody;
        enemyBodyDef.position.set(spawnLocation);

        body = world.createBody(enemyBodyDef);

        MassData enemyMassData = new MassData();
        enemyMassData.mass = 10f;
        body.setMassData(enemyMassData);
        body.setUserData("Rocket");

        PolygonShape enemyShape = new PolygonShape();
        enemyShape.setAsBox(image.getWidth()/2, image.getHeight());

        FixtureDef enemyFixtureDef = new FixtureDef();
        enemyFixtureDef.shape = enemyShape;

        Fixture enemyFixture = body.createFixture(enemyFixtureDef);
        enemyFixture.setUserData("Rocket");
        enemyShape.dispose();
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
    }

    public void move(float dx, float dy){
        setTransform(new Vector2(body.getPosition().x + dx,
                body.getPosition().y + dy), body.getAngle());
    }
}
