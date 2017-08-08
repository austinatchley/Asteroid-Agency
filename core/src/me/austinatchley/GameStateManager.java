package me.austinatchley;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

import me.austinatchley.States.State;

public class GameStateManager {

    private static final String SCORE = "highScore";
    private static Preferences pref;

    private Stack<State> states;

    public GameStateManager(){
        states = new Stack<State>();
        pref = Gdx.app.getPreferences("PreferenceName");

        if(!pref.contains(SCORE))
            pref.putInteger(SCORE, 0);
    }

    public void push(State state){
        states.push(state);
    }

    public State pop(){
        states.peek().dispose();
        return states.pop();
    }

    public void set(State state){
        states.pop();
        states.push(state);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch batch){
        states.peek().render(batch);
    }

    public static int getHighScore(){
        return pref.getInteger(SCORE);
    }

    public static void setHighScore(int score){
        pref.putInteger(SCORE, score);
        pref.flush();
    }

    public static void tryHighScore(int score){
        if(score > pref.getInteger(SCORE))
            setHighScore(score);
    }
}

