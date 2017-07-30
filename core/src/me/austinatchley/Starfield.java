package me.austinatchley;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import me.austinatchley.Objects.Rocket;
import me.austinatchley.States.GameState;


public class Starfield {
    private int num;
    private Array<Star> stars;
    private ShapeRenderer renderer;
    private Camera cam;
    private Rocket rocket;

    public Starfield(int num, Camera cam, Rocket rocket){
        this.num = num;
        this.stars = new Array<Star>();
        this.cam = cam;
        this.rocket = rocket;

        renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(cam.combined);

        float r,g,b;
        for (int i = 0; i < num; i++) {
            r = 100f - MathUtils.random(-30,30);
            g = 50f - MathUtils.random(-30,30);
            b = 110f - MathUtils.random(-30,30);
            stars.add(new Star(
                    MathUtils.random(GameState.WIDTH),
                    MathUtils.random(GameState.HEIGHT),
                    10f - MathUtils.random()*MathUtils.random()*9f,
                    r / 255f,
                    g / 255f,
                    b / 255f
			));
        }
    }

    public void update(){
        Gdx.gl.glLineWidth(10);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        for(int i = 0; i < num; i++){
            Star p = stars.get(i);

            float vx = MathUtils.random(-5f, 5f) + rocket.getVelocity().x / 2f;
            float vy = 80f + rocket.getVelocity().y;

            p.x = (p.x - vx/p.z) % GameState.WIDTH;
            if (p.x < 0)
                p.x += GameState.WIDTH;

            p.y = (p.y - vy/p.z) % GameState.HEIGHT;
            if (p.y < 0) {
                p.y += GameState.HEIGHT;
                p.x = MathUtils.random(GameState.WIDTH);
            }

            float val = 1f - p.z / 20f;
            Color color = new Color(val*p.r, val*p.g, val*p.b, 1f);
            renderer.setColor(color);
            renderer.line(p.x, p.y, p.x - vx/p.z, p.y - vy/p.z);
        }
        renderer.end();
    }
}
