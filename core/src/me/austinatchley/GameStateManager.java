package me.austinatchley;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

import me.austinatchley.States.State;

public class GameStateManager {

    private Stack<State> states;
    private int highScore;

    public GameStateManager(){
        states = new Stack<State>();
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
        return highScore;
    }

    public void setHighScore(int score){
        highScore = score;
    }

    public void tryHighScore(int score){
        highScore = Math.max(score, highScore);
    }
}

