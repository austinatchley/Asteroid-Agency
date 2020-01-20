package me.austinatchley.Tools;

import com.badlogic.gdx.math.Vector2;

import me.austinatchley.Objects.SpaceObject;

public class SpawnPair {
    private final SpaceObject obj;
    private final Vector2 loc;

    public SpawnPair(SpaceObject obj, Vector2 loc) {
        this.obj = obj;
        this.loc = loc;
    }

    public SpaceObject getObject() {
        return obj;
    }

    public Vector2 getLocation() {
        return loc;
    }
}
