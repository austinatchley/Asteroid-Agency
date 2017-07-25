package me.austinatchley.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;

import me.austinatchley.States.GameState;
import me.austinatchley.States.State;

public class Enemy extends SpaceObject {
    private static final int OFFSET = 200;

    public Vector2 spawnLocation;
    public float xDir, yDir;

    public ArrayList<Missile> shots;
    private long lastShotTime;

    private int numShotsTaken, shotLimit;

    public Enemy(World world){
        super(world);
//        image = new Texture(Math.random() > .5f ? "flynnhead.png" : "dadhead.png");
        image = new Texture("outline.png");
        sprite = new Sprite(image);
        spawnLocation = new Vector2(MathUtils.random(State.WIDTH), State.HEIGHT - OFFSET);
        shots = new ArrayList<Missile>();
        init();
    }

    public Enemy(World world, Vector2 spawnLocation){
        super(world);
        image = new Texture("outline.png");
        sprite = new Sprite(image);
        this.spawnLocation = spawnLocation;
        shots = new ArrayList<Missile>();
        init();
    }

    public Enemy(World world, int numX, float height){
        super(world);
        image = new Texture("outline.png");
        sprite = new Sprite(image);
        shots = new ArrayList<Missile>();
        this.spawnLocation = new Vector2(GameState.p2m(
                (numX * image.getWidth()) % (Gdx.graphics.getWidth() / image.getWidth()),
                height));
        init();
//        spawnLocation = new Vector2(numX * image.getWidth(), height);
    }

    public Enemy(World world, int numX, float height, int shotLimit){
        this(world, numX, height);
        this.shotLimit = shotLimit;
    }

    @Override
    public void init() {
        BodyDef enemyBodyDef = new BodyDef();
        enemyBodyDef.type = BodyDef.BodyType.KinematicBody;
        enemyBodyDef.position.set(spawnLocation);
        enemyBodyDef.fixedRotation = true;
        enemyBodyDef.active = false;
        enemyBodyDef.angle = (float) Math.PI;

        body = world.createBody(enemyBodyDef);

        MassData enemyMassData = new MassData();
        enemyMassData.mass = 0f;
        body.setMassData(enemyMassData);
        body.setUserData("Enemy");

        Vector2 boxSize = GameState.p2m(image.getWidth()/2, image.getHeight());
        PolygonShape enemyShape = new PolygonShape();
        enemyShape.setAsBox(boxSize.x, boxSize.y);

        FixtureDef enemyFixtureDef = new FixtureDef();
        enemyFixtureDef.shape = enemyShape;

        Fixture enemyFixture = body.createFixture(enemyFixtureDef);
        enemyFixture.setUserData("Enemy");
        enemyShape.dispose();
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        Iterator<Missile> iterator = shots.iterator();
        while(iterator.hasNext()){
            Missile shot = iterator.next();
            shot.render(batch);
            if(shot.isOutOfBounds()){
                shot.dispose();
                iterator.remove();
            }
        }
    }

    public void move(float dt){
        setTransform(new Vector2(getPosition().x + dt*xDir,
                getPosition().y + dt*yDir), body.getAngle());
    }

    public void shoot(String type){
        Missile shot;
        if(type.equals("fast"))
            shot = new Missile(world,
                new Vector2(getPosition().x, getPosition().y),
                0f,
                -100f);
        else if(type.equals("curvy"))
            shot = new Missile(world,
                    new Vector2(getPosition().x, getPosition().y),
                    (float) (Math.random() * 100f) - 50f,
                    -30f);
        else
            shot = new Missile(world,
                    new Vector2(getPosition().x, getPosition().y),
                    0f,
                    -80f);

        shots.add(shot);
        numShotsTaken++;
        lastShotTime = TimeUtils.nanoTime();
    }

    public boolean canShoot(){
        return (TimeUtils.nanoTime() - lastShotTime > 2000000000l) && (numShotsTaken <= shotLimit);
    }
}
