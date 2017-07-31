package me.austinatchley.Objects;

import com.badlogic.gdx.physics.box2d.World;

public abstract class Obstacle extends SpaceObject {
    public Obstacle(World world) {
        super(world);
    }

    @Override
    public int scoreEffect() {
        return -1;
    }
}
