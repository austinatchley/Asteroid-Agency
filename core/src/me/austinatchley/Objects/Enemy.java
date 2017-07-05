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

import java.util.ArrayList;

import me.austinatchley.States.State;

public class Enemy extends SpaceObject {
    private static final int OFFSET = 200;
    public Vector2 spawnLocation;
    public float xDir, yDir;
    private ArrayList<Missile> shots;

    public Enemy(World world){
        super(world);
        image = new Texture(Math.random() > .5f ? "flynnhead.png" : "dadhead.png");
        sprite = new Sprite(image);
        spawnLocation = new Vector2(MathUtils.random(State.WIDTH), State.HEIGHT - OFFSET);
        shots = new ArrayList<Missile>();
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

        for(Missile shot : shots)
            shot.render(batch);

        for(int i = 0; i < shots.size(); i++)
            if(shots.get(i).getPosition().y < 0 || shots.get(i).getPosition().y > Gdx.graphics.getHeight() ||
                    shots.get(i).getPosition().x < 0 || shots.get(i).getPosition().x > Gdx.graphics.getWidth()) {
                shots.get(i).dispose();
                shots.remove(i);
            }
    }

    public void move(float dt){
        setTransform(new Vector2(body.getPosition().x + dt*xDir,
                body.getPosition().y + dt*yDir), body.getAngle());
    }

    public void shoot(String type){
        Missile shot = new Missile(world,
                new Vector2(body.getPosition().x, body.getPosition().y),
                0f,
                -300f);
        shots.add(shot);
    }
}
