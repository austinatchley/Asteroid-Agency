package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import me.austinatchley.GameStateManager;

import static me.austinatchley.Tools.Utils.BG_COLOR;

public abstract class InterfaceState extends State {
    protected Skin skin;
    protected Stage stage;
    protected Table table;

    protected Color bgColor;

    protected InterfaceState(final GameStateManager gsm) {
        super(gsm);

        skin = gsm.getSkin();

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
//        table.setDebug(true);

        bgColor = BG_COLOR;
    }

    @Override
    public void update(float dt) {
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
