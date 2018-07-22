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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.socket.client.SocketIOException;
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

    // Used to track when we need to update data from the server
    float serverTimer;

    // Used for input handling
    private enum InputAction {
        INVALID, LEFT, RIGHT, UP, DOWN, FIRE
    }

    public MultiplayerState(final GameStateManager gsm) {
        super(gsm);

        Box2D.init();
        world = new World(new Vector2(0, -100f), true);

        players = new HashMap<String, Rocket>();
        spawnGroup = new HashSet<SpawnPair>();
        activeGroup = new HashSet<SpaceObject>();
        deleteGroup = new HashSet<SpaceObject>();

        shipTex = new Texture("spaceCraft4.png");

        serverTimer = 0f;

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
            InputAction dir = InputAction.INVALID;

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//                Vector2 newTransform = player.getPosition();
//                newTransform.x -= 200.f * deltaTime;

//                player.setTransform(newTransform, player.getAngle());
                dir = InputAction.LEFT;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//                Vector2 newTransform = player.getPosition();
//                newTransform.x += 200.f * deltaTime;

//                player.setTransform(newTransform, player.getAngle());
                dir = InputAction.RIGHT;
            }

            if (dir != InputAction.INVALID) {
                sendInput(dir, deltaTime);
            }
        }
    }

    private void sendInput(InputAction dir, float deltaTime) {
        // Send the corresponding input request to the server
        try {
            JSONObject dirObj;
            try {
                System.out.println("pressed direction " + dir.ordinal());
                dirObj = new JSONObject().put("direction", dir.ordinal())
                                          .put("dt", deltaTime);

            } catch (JSONException e) {
                dirObj = null;
                Gdx.app.error("JSONObject","Direction JSONObject creation failed", e);
            }
            socket.emit("input", dirObj);
        } catch (Exception e) {
            Gdx.app.error("SocketIO", "Failed to emit input event", e);
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
                    players.put(id, player);
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
                } catch(JSONException e) {
                    Gdx.app.error("SocketIO", "JSONException");
                    Gdx.app.error("SocketIO", "Exception:", e);
                } catch(Exception e) {
                    Gdx.app.error("Error", "generic", e);
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

                        Iterator<String> it = players.keySet().iterator();
                        while (it.hasNext()) {
                            Gdx.app.log("", "Players " + it.next());
                        }

//                        Gdx.app.log("","SpawnGroup " + spawnGroup);
                        Gdx.app.log("getPlayers", "id " + id + ", (x, y) = (" + position.x + ", " + position.y + ")");
                    }
                } catch(Exception e) {
                    Gdx.app.log("SocketIO", "Error getting new player ID");
                    Gdx.app.log("SocketIO", e.getMessage());
                }
            }
        }).on("updatePlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray data = (JSONArray) args[0];
                try {
                    for (int i = 0; i < data.length(); i++) {
                        String id = "";
                        float x = 0f;
                        float y = 0f;

                        try {
                            id = data.getJSONObject(i).getString("id");
                            x = ((Double) data.getJSONObject(i).getDouble("x")).floatValue();
                            y = ((Double) data.getJSONObject(i).getDouble("y")).floatValue();
                        } catch(Exception e) {
                            System.out.println("JSON error here");
                        }
//                        Gdx.app.log("JSON","id: " + id + ", x: " + x + ", y: " + y);


                        Rocket correctPlayer = players.get(id);
                        if (correctPlayer != null) {
                            correctPlayer.setTransform(x, y, 0f);
                         } else {
                            System.out.println("players.get(id) was null " + players);
                        }

                        /*Rocket newRocket = new Rocket(world, shipTex);
                        Vector2 position = new Vector2();
                        position.x = ((Double) data.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) data.getJSONObject(i).getDouble("y")).floatValue();

                        String id = data.getJSONObject(i).getString("id");
                        Gdx.app.log("SocketIO", "Found player " + id);

                        players.put(id, newRocket);
                        spawnGroup.add(new SpawnPair(newRocket, position));*/
                    }
                } catch(Exception e) {
                    Gdx.app.log("SocketIO", "Error in updatePlayers");
                    Gdx.app.log("SocketIO", e.getMessage());
                    System.out.println(e);
                }
            }
        });
    }
}
