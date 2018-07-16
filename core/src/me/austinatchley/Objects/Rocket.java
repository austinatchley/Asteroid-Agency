package me.austinatchley.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;
import java.util.Iterator;

import me.austinatchley.Tools.Utils;

public class Rocket extends SpaceObject {
    private static final int VERTICAL_OFF = 20;
    private static final float CHANGE = 10f * DEG2RAD;

    public ParticleEffect thruster1, thruster2;

    public ArrayList<Missile> shots;
    private long lastShotTime;

    private Vector2 velocity;
    private Vector2 lastPos;

    PhysicsShapeCache physicsShapes;

    public Rocket(World world) {
        super(world);
        image = new Texture("spaceCraft4.png");
        sprite = new Sprite(image);

        velocity = new Vector2();
        lastPos = new Vector2();

        thruster1 = new ParticleEffect();
        thruster2 = new ParticleEffect();
        thruster1.load(Gdx.files.internal("rocket_thruster.p"), Gdx.files.internal(""));
        thruster2.load(Gdx.files.internal("rocket_thruster.p"), Gdx.files.internal(""));

        physicsShapes = new PhysicsShapeCache("rocket_body.xml");

        init();

        shots = new ArrayList<Missile>();

        thruster1.start();
        thruster1.setPosition(body.getPosition().x, body.getPosition().y);
        thruster1.getEmitters().first().getAngle().setLow(-85f);
        thruster1.getEmitters().first().getAngle().setHigh(-95f);

        thruster2.start();
        thruster2.setPosition(body.getPosition().x, body.getPosition().y);
        thruster2.getEmitters().first().getAngle().setLow(-85f);
        thruster2.getEmitters().first().getAngle().setHigh(-95f);
    }

    public void init() {
        BodyDef rocketBodyDef = new BodyDef();
        rocketBodyDef.type = BodyDef.BodyType.KinematicBody;
        rocketBodyDef.position.set((Utils.WIDTH - image.getWidth()) * Utils.PPM / 2, VERTICAL_OFF);

        body = physicsShapes.createBody("spaceCraft4", world, rocketBodyDef, Utils.PPM, Utils.PPM);
        body.setUserData("Rocket");

        Filter filter = new Filter();
        filter.categoryBits = 0x0002;
        filter.maskBits = 0x7fff;

        for (Fixture fix : body.getFixtureList()) fix.setFilterData(filter);
    }

    public void render(SpriteBatch batch) {
        Vector2 pos = getPosition();
        float rotation = body.getAngle() / DEG2RAD;

        sprite.setPosition(pos.x, pos.y);
        sprite.setRotation(rotation);

        if (thruster1.isComplete()) thruster1.reset();

        if (thruster2.isComplete()) thruster2.reset();

        thruster1.setPosition(
                sprite.getX() + sprite.getWidth() * 0.3f,
                sprite.getY() + sprite.getHeight() * 0.3f);
        thruster2.setPosition(
                sprite.getX() + sprite.getWidth() * 0.7f,
                sprite.getY() + sprite.getHeight() * 0.3f);

        thruster1.getEmitters().first().getAngle().setLow(rotation - 90f);
        thruster1.getEmitters().first().getAngle().setHigh(rotation - 90f);

        thruster2.getEmitters().first().getAngle().setLow(rotation - 90f);
        thruster2.getEmitters().first().getAngle().setHigh(rotation - 90f);

        thruster1.update(Gdx.graphics.getDeltaTime());
        thruster1.draw(batch);
        thruster2.update(Gdx.graphics.getDeltaTime());
        thruster2.draw(batch);

        Iterator<Missile> iterator = shots.iterator();
        while (iterator.hasNext()) {
            Missile shot = iterator.next();
            shot.render(batch);
            if (shot.isOutOfBounds()) {
                shot.dispose();
                iterator.remove();
            }
        }

        // draw it as a normal sprite (on top)
        sprite.draw(batch);
    }

    public void rotateTowards(Vector2 target) {
        Vector2 toTarget =
                new Vector2(target.x - body.getPosition().x, target.y - body.getPosition().y);

        float desiredAngle = MathUtils.atan2(-toTarget.x, toTarget.y);
        float totalRotation = desiredAngle - body.getAngle();
        while (totalRotation < -180 * DEG2RAD) totalRotation += 360 * DEG2RAD;
        while (totalRotation > 180 * DEG2RAD) totalRotation -= 360 * DEG2RAD;
        float newAngle = body.getAngle() + Math.min(CHANGE, Math.max(-CHANGE, totalRotation));

        setTransform(body.getPosition(), newAngle);
    }

    public void moveTo(Vector2 target) {
        lastPos = getPosition();
        setTransform(target.x + Utils.PPM * image.getWidth() / 4f, target.y, body.getAngle());
        velocity.x = getPosition().x - lastPos.x;
        velocity.y = getPosition().y - lastPos.y;
    }

    public void shootMissile(Sound missileSound) {
        missileSound.play();

        PlayerMissile shot =
                new PlayerMissile(
                        world,
                        new Vector2(
                                getPosition().x + image.getWidth() / 2f,
                                getPosition().y + image.getHeight()),
                        0f,
                        600f);
        shot.flip();

        shots.add(shot);
        lastShotTime = TimeUtils.nanoTime();
    }

    public boolean canShoot() {
        return TimeUtils.nanoTime() - lastShotTime > 250000000;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
