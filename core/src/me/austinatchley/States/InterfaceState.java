package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import me.austinatchley.GameStateManager;

import static me.austinatchley.Utils.BG_COLOR;

public abstract class InterfaceState extends State {
    protected Skin skin;
    protected Stage stage;
    protected Table table;

    protected InterfaceState(final GameStateManager gsm){
        super(gsm);

        skin = gsm.getSkin();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
//        table.setDebug(true);
    }

    @Override
    public void update(float dt) {
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, BG_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
