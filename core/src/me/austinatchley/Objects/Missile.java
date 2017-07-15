package me.austinatchley.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class Missile extends SpaceObject {
    Rectangle hitbox;
    Vector2 start;
    float dx,dy;

    public Missile(World world, Vector2 start){
        super(world);
        image = new Texture("shot.png");
        sprite = new Sprite(image);
        this.start = start;
        init();
    }

    public Missile(World world, Vector2 start, float dx, float dy){
        this(world, start);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    void init() {
        hitbox = new Rectangle(start.x, start.y, sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void render(SpriteBatch batch) {
        float posX = hitbox.x;
        float posY = hitbox.y;
        sprite.setPosition(posX, posY);

        // Then we simply draw it as a normal sprite.
        sprite.draw(batch);

//        System.out.println(hitbox.x + ", " + hitbox.y);

        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float dt){
        hitbox.setPosition(hitbox.x + dt*dx,
                hitbox.y + dt*dy);
    }

    public boolean isOutOfBounds(){
        return hitbox.y < -hitbox.getHeight() || hitbox.y > Gdx.graphics.getHeight() ||
                hitbox.x < -hitbox.getWidth() || hitbox.x > Gdx.graphics.getWidth();
    }
}
