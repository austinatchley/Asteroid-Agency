package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.socket.emitter.Emitter;
import me.austinatchley.GameStateManager;

import io.socket.client.Socket;
import io.socket.client.IO;

import me.austinatchley.Objects.Rocket;
import me.austinatchley.Objects.SpaceObject;

import me.austinatchley.Tools.Utils;
import me.austinatchley.Tools.SpawnPair;

public class MultiplayerState extends InterfaceState {
    private Socket socket;
    private World world;

    Rocket player;
    HashMap<String, Rocket> players;

    // Set of SpaceObjects waiting to be spawned
    // Each SpawnPair entry contains a SpaceObject and a spawn location,
    Set<SpawnPair> spawnGroup;

    // Set of SpaceObjects in the scene
    Set<SpaceObject> activeGroup;

    // Set of SpaceObjects waiting to be deleted
    Set<SpaceObject> deleteGroup;

    Texture shipTex;

    public MultiplayerState(final GameStateManager gsm) {
        super(gsm);

        Box2D.init();
        world = new World(new Vector2(0, -100f), true);

        players = new HashMap<String, Rocket>();
        spawnGroup = new HashSet<SpawnPair>();
        activeGroup = new HashSet<SpaceObject>();
        deleteGroup = new HashSet<SpaceObject>();

        shipTex = new Texture("spaceCraft4.png");

        socket = Utils.connectSocket("http://localhost:8080");
        configSocketEvents();
    }

    @Override
    protected void handleInput() {
        handleInput(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        handleInput();

        batch.begin();
        for (SpaceObject obj : activeGroup) {
            obj.render(batch);
        }
        batch.end();

        stage.draw();

        spawn();
        delete();
    }

    @Override
    public void dispose() {
        super.dispose();
        player.dispose();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    private void spawn() {
        for (SpawnPair pair : spawnGroup) {
            pair.getObject().init();
            pair.getObject().setTransform(pair.getLocation(), 0.f);
            activeGroup.add(pair.getObject());
        }

        spawnGroup.clear();
    }

    private void delete() {
        for (SpaceObject obj : deleteGroup) {
            obj.dispose();
            activeGroup.remove(obj);
        }

        deleteGroup.clear();
    }

    private void handleInput(float deltaTime) {
        if (player != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                Vector2 newTransform = player.getPosition();
                newTransform.x -= 200.f * deltaTime;

                player.setTransform(newTransform, player.getAngle());
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                Vector2 newTransform = player.getPosition();
                newTransform.x += 200.f * deltaTime;

                player.setTransform(newTransform, player.getAngle());
            }
        }
    }

    private void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");
                player = new Rocket(world, shipTex);
                spawnGroup.add(new SpawnPair(player, new Vector2()));
            }
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "My ID: " + id);
                } catch(Exception e) {
                    Gdx.app.log("SocketIO", "Error getting ID");
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "New player connected: " + id );

                    Rocket newRocket = new Rocket(world, shipTex);
                    players.put(id, newRocket);
                    spawnGroup.add(new SpawnPair(newRocket, new Vector2()));
                } catch(Exception e) {
                    Gdx.app.log("SocketIO", "Error getting new player ID");
                    Gdx.app.log("SocketIO", e.getMessage());
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "Player disconnected: " + id );

                    Rocket leavingPlayer = players.remove(id);
                    deleteGroup.add(leavingPlayer);
                } catch(Exception e) {
                    Gdx.app.log("SocketIO", "Error getting new player ID");
                    Gdx.app.log("SocketIO", e.getMessage());
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray data = (JSONArray) args[0];
                try {
                    for (int i = 0; i < data.length(); i++) {
                        Rocket newRocket = new Rocket(world, shipTex);
                        Vector2 position = new Vector2();
                        position.x = ((Double) data.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) data.getJSONObject(i).getDouble("y")).floatValue();

                        String id = data.getJSONObject(i).getString("id");
                        Gdx.app.log("SocketIO", "Found player " + id);
                        
                        players.put(id, newRocket);
                        spawnGroup.add(new SpawnPair(newRocket, position));
                    }
                } catch(Exception e) {
                    Gdx.app.log("SocketIO", "Error getting new player ID");
                    Gdx.app.log("SocketIO", e.getMessage());
                }
            }
        });
    }
}
