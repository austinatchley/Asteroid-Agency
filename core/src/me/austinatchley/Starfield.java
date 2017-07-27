package me.austinatchley;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import me.austinatchley.States.GameState;


public class Starfield {
    private int num;
    private Array<Star> stars;

    public Starfield(int num){
        this.num = num;
        this.stars = new Array<Star>();
        for (int i = 0; i < num; i++) {
            int r = 255 - (int)(Math.random()*10f);
            int g = 225 - (int)(Math.random()*180);
            int b = 255 - (int)(Math.random()*15);
            stars.add(new Star(
                    MathUtils.random()* GameState.WIDTH,
                    MathUtils.random()* GameState.HEIGHT,
                    11f-MathUtils.random()*MathUtils.random()*10,
                    r,
                    g,
                    b
			));
        }
    }

    public void update(float timeDelta){
        for(int i = 0; i < num; i++){
            Star p = stars.get(i);

            float vx = 100f;
            float vy = -100f;

            p.x = (p.x - vx/p.z) % GameState.WIDTH;
            if (p.x < 0) p.x += GameState.WIDTH;
            p.y = (p.y - vy/p.z) % GameState.HEIGHT;
            if (p.y < 0) p.y += GameState.HEIGHT;

            float val = 1- p.z/11;
//            this.gfx.lineStyle(1, (val*p.b) | ((val*p.g)<<8) | ((val*p.r)<<16));
//            this.gfx.moveTo(p.x, p.y);
//            this.gfx.lineTo(p.x - vx/p.z, p.y - vy/p.z + 1);
        }
    }
}
