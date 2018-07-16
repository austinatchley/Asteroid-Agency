package me.austinatchley.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import me.austinatchley.Objects.Rocket;

public class Starfield {
    private boolean useVelocity;

    private int num;
    public Array<Star> stars;

    private ShapeRenderer renderer;
    private Camera cam;

    public Rocket rocket;
    private Color color;
    private float vx, vy, val;

    public Starfield(int num, Camera cam, Rocket rocket) {
        this.num = num;
        this.stars = new Array<Star>();
        this.cam = cam;
        this.rocket = rocket;

        renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(cam.combined);

        float r, g, b;
        for (int i = 0; i < num; i++) {
            r = 120f - MathUtils.random(-30, 30);
            g = 70f - MathUtils.random(-30, 30);
            b = 90f - MathUtils.random(-30, 30);
            stars.add(
                    new Star(
                            MathUtils.random(Utils.WIDTH),
                            MathUtils.random(Utils.HEIGHT),
                            10f - MathUtils.random() * MathUtils.random() * 9f,
                            r / 255f,
                            g / 255f,
                            b / 255f));
        }
    }

    public void render() {
        Gdx.gl.glLineWidth(10);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < num; i++) {
            Star p = stars.get(i);

            vx = MathUtils.random(-5f, 5f);
            vy = 80f;

            if (useVelocity && rocket != null) {
                vx += rocket.getVelocity().x;
                vx += rocket.getVelocity().y;
            }

            p.x = (p.x - vx / p.z) % Utils.WIDTH;
            if (p.x < 0) p.x += Utils.WIDTH;

            p.y = (p.y - vy / p.z) % Utils.HEIGHT;
            if (p.y < 0) {
                p.y += Utils.HEIGHT;
                p.x = MathUtils.random(Utils.WIDTH);
            }

            val = 1f - p.z / 20f;
            color = new Color(val * p.r, val * p.g, val * p.b, 1f);
            renderer.setColor(color);
            renderer.line(p.x, p.y, p.x - vx / p.z, p.y - vy / p.z);
        }
        renderer.end();
    }

    public void useVelocity(boolean use) {
        useVelocity = use;
    }
}
