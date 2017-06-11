package me.austinatchley;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenuScene implements ApplicationListener {
    Skin skin;
    SpriteBatch batch;
    Stage stage;
    Table table;
    BitmapFont font;

    @Override
    public void create(){
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setDebug(true);

        skin = new Skin();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        font = new BitmapFont(Gdx.files.internal("fonts/mc.fnt"),
                Gdx.files.internal("fonts/mc.png"),
                false);
        skin.add("default", new BitmapFont());

        TextButtonStyle style = new TextButtonStyle();
        style.up = skin.newDrawable("white", Color.DARK_GRAY);
        style.down = skin.newDrawable("white", Color.DARK_GRAY);
        style.checked = skin.newDrawable("white", Color.BLUE);
        style.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        style.font = skin.getFont("default");
        skin.add("default", style);

        final TextButton button = new TextButton("Play", skin);
        table.add(button);

        button.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                System.out.println("Clicked! Is checked: " + button.isChecked());
                button.setText("Good job!");
            }
        });

        table.add(new Image(skin.newDrawable("white", Color.RED))).size(64);

//        MenuActor menuActor = new MenuActor(new Texture(pixmap));
//        menuActor.setTouchable(Touchable.enabled);
//        stage.addActor(menuActor);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());

//        if (Gdx.input.isTouched()) {
//            stage.act(Gdx.graphics.getDeltaTime());
//            dispose();
//        }
        stage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
    }
}
