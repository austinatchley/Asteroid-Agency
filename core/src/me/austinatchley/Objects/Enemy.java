package me.austinatchley.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import me.austinatchley.Tools.Utils;

public class Enemy extends SpaceObject {
    private static final int OFFSET = 200;
    public Vector2 spawnLocation;

    private long lastShotTime;
    public float xDir, yDir;

    private Array<Missile> shots;

    private int numShotsTaken, shotLimit;

    private PhysicsShapeCache physicsShapes;

    private Sound shotSound;

    public Enemy(World world){
        super(world);
//        image = new Texture(Math.random() > .5f ? "flynnhead.png" : "dadhead.png");
        image = new Texture("spaceCraft1.png");
        sprite = new Sprite(image);
        spawnLocation = new Vector2(MathUtils.random(Utils.WIDTH), Utils.HEIGHT - OFFSET);
        shots = new Array<Missile>();
        numShotsTaken = 0;

        physicsShapes = new PhysicsShapeCache("rocket_body.xml");
        init();
    }

    public Enemy(World world, Vector2 spawnLocation){
        super(world);
        image = new Texture("spaceCraft1.png");
        sprite = new Sprite(image);
        this.spawnLocation = spawnLocation;
        shots = new Array<Missile>();

        physicsShapes = new PhysicsShapeCache("rocket_body.xml");
        init();
    }

    public Enemy(World world, int numX, float height){
        super(world);
        image = new Texture("spaceCraft1.png");
        sprite = new Sprite(image);
        shots = new Array<Missile>();
        this.spawnLocation = new Vector2(Utils.p2m(
                (numX + 1) * image.getWidth(),
                height));

        physicsShapes = new PhysicsShapeCache("rocket_body.xml");
        init();
    }

    public Enemy(World world, int numX, float height, int shotLimit){
        this(world, numX, height);
        this.shotLimit = shotLimit;
    }

    public Enemy(World world, int numX, float height, int shotLimit, Array<Missile> shots){
        this(world, numX, height, shotLimit);
        this.shots = shots;
    }

    @Override
    public void init() {
        shotSound = Gdx.audio.newSound(Gdx.files.internal("enemyshoot.wav"));

        BodyDef enemyBodyDef = new BodyDef();
        enemyBodyDef.type = BodyDef.BodyType.KinematicBody;
        enemyBodyDef.position.set(spawnLocation);
        enemyBodyDef.fixedRotation = true;
        enemyBodyDef.angle = (float) Math.PI;

        body = physicsShapes.createBody("spaceCraft1", world, enemyBodyDef, Utils.PPM, Utils.PPM);

        Filter filter = new Filter();
        filter.categoryBits = 0x0004;
        filter.maskBits = 0x0003;

        for(Fixture fix : body.getFixtureList())
            fix.setFilterData(filter);

        body.setUserData("Enemy");
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 pos = getPosition();
        float rotation = body.getAngle() / DEG2RAD;
        sprite.setPosition(pos.x - image.getWidth(), pos.y - image.getHeight());
        sprite.setRotation(rotation);

        // Then we simply draw it as a normal sprite.
        sprite.draw(batch);
    }

    public void update(){
        if(canShoot())
            shoot("fast", shotSound);
    }

    public void move(float dt){
        setTransform(new Vector2(getPosition().x + dt*xDir,
                getPosition().y + dt*yDir), body.getAngle());
    }

    public void shoot(String type, Sound shotSound){
        shotSound.play();

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
        boolean result = (numShotsTaken == 0);
        result = result || ((TimeUtils.nanoTime() - lastShotTime > 2000000000l) && (numShotsTaken <= shotLimit));
        return isOnScreen() && result;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public boolean isOnScreen(){
        return new Rectangle(0, 0, Utils.WIDTH, Utils.HEIGHT).contains(sprite.getBoundingRectangle());
    }
}
