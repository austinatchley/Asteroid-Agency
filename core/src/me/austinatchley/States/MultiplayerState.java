package me.austinatchley.States;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.austinatchley.GameStateManager;

import io.socket.client.Socket;
import io.socket.client.IO;
import me.austinatchley.Tools.Utils;

public class MultiplayerState extends InterfaceState {
    private Socket socket;

    public MultiplayerState(final GameStateManager gsm) {
        super(gsm);

        Utils.connectSocket(socket, "http://localhost:8080");
    }

    @Override
    protected void handleInput() {}

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }
}
