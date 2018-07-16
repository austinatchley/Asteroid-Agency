package me.austinatchley.States;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import javax.swing.GroupLayout;

import me.austinatchley.GameStateManager;
import me.austinatchley.RocketGame;
import me.austinatchley.Tools.Starfield;
import me.austinatchley.Tools.Utils;

public class InfoState extends InterfaceState {

    private Starfield starfield;

    public InfoState(final GameStateManager gsm, final String titleString, final String messageString) {
        super(gsm);

        performUISetup(gsm, titleString, messageString);

        starfield = new Starfield(400, camera, null);
        starfield.useVelocity(false);
    }

    public InfoState(final GameStateManager gsm, Starfield starfield, final String titleString, final String messageString) {
        super(gsm);

        performUISetup(gsm, titleString, messageString);

        this.starfield = starfield;
    }

    private void performUISetup(final GameStateManager gsm, final String titleString, final String messageString) {
        Table scrollableTable = new Table();

        ScrollPane scrollPane = new ScrollPane(scrollableTable);
        scrollPane.setTouchable(Touchable.enabled);

        String title = gsm.game.strings.get(titleString);
        String message = gsm.game.strings.get(messageString);

        Label titleLabel = new Label(title, skin, "title");
        titleLabel.setAlignment(Align.center);
        titleLabel.setWrap(true);
        scrollableTable.add(titleLabel).width(Utils.WIDTH).padTop(Utils.HEIGHT / 12);
        scrollableTable.row();

        Label textLabel = new Label(message, skin, "text");
        textLabel.setAlignment(Align.left);
        textLabel.setWrap(true);
        scrollableTable.add(textLabel).width(Utils.WIDTH).padTop(Utils.HEIGHT / 12);
        scrollableTable.row();

        Label continueLabel = new Label("Continue", skin, "text");
        continueLabel.setAlignment(Align.center);
        continueLabel.setWrap(true);
        scrollableTable.add(continueLabel).width(Utils.WIDTH).padTop(Utils.HEIGHT / 12);
        scrollableTable.row();

        final Drawable buttonImage =
                new TextureRegionDrawable(new TextureRegion(new Texture("playbutton.png")));
        final ImageButton playButton = new ImageButton(buttonImage);

        scrollableTable.add(playButton);

        playButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gsm.set(new GameState(gsm, starfield));
                    }
                });
        scrollableTable.row();

        table.add(scrollPane).expand().fill();
    }

    @Override
    protected void handleInput() {}

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        starfield.render();

        stage.draw();
    }
}
