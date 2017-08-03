package me.austinatchley;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

import me.austinatchley.States.State;

public class GameStateManager {

    private final String SCORE = "highScore";

    private Stack<State> states;
    private Preferences pref;

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

    public int getHighScore(){
        return pref.getInteger(SCORE);
    }

    public void setHighScore(int score){
        pref.putInteger(SCORE, score);
        pref.flush();
    }

    public void tryHighScore(int score){
        if(score > pref.getInteger(SCORE))
            setHighScore(score);
    }
}

