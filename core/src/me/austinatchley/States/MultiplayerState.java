package me.austinatchley.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.austinatchley.GameStateManager;
import me.austinatchley.Objects.Missile;
import me.austinatchley.Objects.PlayerMissile;
import me.austinatchley.Objects.Rocket;
import me.austinatchley.Objects.SpaceObject;
import me.austinatchley.Tools.SpawnPair;
import me.austinatchley.Tools.Utils;

public class MultiplayerState extends InterfaceState {
    private Socket socket;
    private World world;

    Rocket player;
    HashMap<String, Rocket> players;

    long playersLastUpdated;
    TreeMap<Long, HashMap<Rocket, Transform>> playersTransformUpdates;

    // Set of SpaceObjects waiting to be spawned
    // Each SpawnPair entry contains a SpaceObject and a spawn location,
    private HashSet<SpawnPair> spawnGroup;

    // Set of SpaceObjects in the scene
    private HashSet<SpaceObject> activeGroup;

    // Set of SpaceObjects waiting to be deleted
    private HashSet<SpaceObject> deleteGroup;

    // Set of ships firing
    private HashSet<Rocket> firingGroup;

    private Texture shipTexture;

    // Used to prevent the player from moving before the ship's transform buffer is filled
    private long spawnTimer;

    // Set to true when the player has finished their spawnTimer cooldown
    private boolean controllable;

    private float prevAccelZ;

    // Used for input handling
    private enum InputAction {
        INVALID, LEFT, RIGHT, UP, DOWN, FIRE
    }

    public MultiplayerState(final GameStateManager gsm) {
        super(gsm);

        Box2D.init();
        world = new World(new Vector2(0, -100f), true);

        players = new HashMap<String, Rocket>();

        playersLastUpdated = System.currentTimeMillis();
        playersTransformUpdates = new TreeMap<Long, HashMap<Rocket, Transform>>();

        spawnGroup = new HashSet<SpawnPair>();
        activeGroup = new HashSet<SpaceObject>();
        deleteGroup = new HashSet<SpaceObject>();

        firingGroup = new HashSet<Rocket>();

        shipTexture = new Texture("spaceCraft4.png");

        spawnTimer = 0l;
        controllable = false;

        prevAccelZ = 0f;

        socket = Utils.connectSocket("https://asteroid-agency.herokuapp.com");
//        socket = Utils.connectSocket("http://localhost:3000");
        configSocketEvents();
    }

    @Override
    protected void handleInput() {
        handleInput(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        batch.end();

        if (controllable) {
            handleInput();
        } else if (System.currentTimeMillis() - spawnTimer > 1000) {
            controllable = true;
        }

        batch.begin();
        for (SpaceObject obj : activeGroup) {
            obj.render(batch);

            if (obj instanceof Missile) {
                if (((Missile)obj).isOutOfBounds()) {
                    deleteGroup.add(obj);
                }
            }
        }
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        spawn();
        delete();

        batch.begin();
    }

    @Override
    public void dispose() {
        super.dispose();
        player.dispose();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        // The TreeSet is sorted, so we grab the first entry (most recent snapshot)
        Map.Entry<Long, HashMap<Rocket, Transform>> mostRecentSnapshot = playersTransformUpdates.firstEntry();

        if (mostRecentSnapshot != null) {
            for (Map.Entry<Rocket, Transform> entry : mostRecentSnapshot.getValue().entrySet()) {
                Rocket rocket = entry.getKey();

                Vector2 pos = entry.getValue().getPosition();
                float rotation = entry.getValue().getRotation();

                // Interpolate the transform to smooth out any jerkiness from the server
                rocket.setTransformLerp(pos, rotation);
            }

            playersTransformUpdates.clear();
            playersLastUpdated = System.currentTimeMillis();
        }

        for (Rocket player : firingGroup) {
            if (player.canShoot()) {
                PlayerMissile missile = player.shootMissile(null);
                activeGroup.add(missile);

                firingGroup.remove(player);
            }
        }

//        firingGroup.clear();
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
            InputAction dir;

            // all input should be accumulated to a Set and sent together
            if (Utils.IS_DESKTOP) {
                dir = handleKeyboard();
            } else {
                dir = handleTouch();
            }

            if (dir != InputAction.INVALID) {
                sendInput(dir, deltaTime);
            }
        }
    }

    private InputAction handleKeyboard() {
        InputAction dir = InputAction.INVALID;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            dir = InputAction.LEFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            dir = InputAction.RIGHT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            dir = InputAction.UP;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            dir = InputAction.DOWN;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            dir = InputAction.FIRE;
        }

        return dir;
    }

    private InputAction handleTouch() {
        InputAction dir = InputAction.INVALID;

        float accelZ = Gdx.input.getAccelerometerZ();
        if (prevAccelZ == 0f) {
            prevAccelZ = accelZ;
        } else if (Math.abs(accelZ - prevAccelZ) > 12f) { // make this a constant
            dir = InputAction.FIRE;
            Gdx.input.vibrate(120);

            prevAccelZ = accelZ;
        }

        if (!Gdx.input.isTouched()) {
            return dir;
        }

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

        camera.unproject(touchPos);

        Vector2 targetPos =
                new Vector2(
                        touchPos.x - (player.getWidth() / 1.5f),
                        touchPos.y - player.getHeight() / 2);

        Vector2 currentPos = player.getPosition();
        if (currentPos == null) {
            Gdx.app.log("handleTouch", "Player's position was null");
            return dir;
        }

        Vector2 deltaPos = targetPos.sub(currentPos);

        if (deltaPos.x < -Utils.DEADZONE) {
            dir = InputAction.LEFT;
        }
        if (deltaPos.x > Utils.DEADZONE) {
            dir = InputAction.RIGHT;
        }

        if (deltaPos.y < -Utils.DEADZONE) {
            dir = InputAction.DOWN;
        }
        if (deltaPos.y > Utils.DEADZONE) {
            dir = InputAction.UP;
        }

        Gdx.app.log("handleTouch", "deltaPos: " + deltaPos.toString());

        return dir;
    }

    // Send the corresponding input request to the server
    private void sendInput(InputAction dir, float deltaTime) {
        try {
            JSONObject dirObj;
            try {
                System.out.println("pressed direction " + dir);
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

                player = new Rocket(world, shipTexture);
                spawnGroup.add(new SpawnPair(player, new Vector2()));

                spawnTimer = System.currentTimeMillis();
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

                    Rocket newRocket = new Rocket(world, shipTexture);
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
                        Rocket newRocket = new Rocket(world, shipTexture);
                        Vector2 position = new Vector2();
                        position.x = ((Double) data.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) data.getJSONObject(i).getDouble("y")).floatValue();

                        String id = data.getJSONObject(i).getString("id");
                        Gdx.app.log("SocketIO", "Found player " + id);

                        players.put(id, newRocket);
                        spawnGroup.add(new SpawnPair(newRocket, position));

                        Gdx.app.log("getPlayers", "id " + id + ", (x, y) = (" + position.x + ", " + position.y + ")");
                    }

                    Iterator<String> it = players.keySet().iterator();
                    while (it.hasNext()) {
                        Gdx.app.log("", "Player " + it.next());
                    }
                } catch(Exception e) {
                    Gdx.app.error("SocketIO", "Error getting new player ID", e);
                }
            }
        }).on("updatePlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray data = (JSONArray) args[0];
                try {
                    HashMap<Rocket, Transform> rocketTransformMap = new HashMap<Rocket, Transform>();
                    long timeStamp = System.currentTimeMillis() - playersLastUpdated;

                    for (int i = 0; i < data.length(); i++) {
                        String id = "";
                        float x = 0f;
                        float y = 0f;
                        boolean firing = false;

                        try {
                            id = data.getJSONObject(i).getString("id");
                            x = ((Double) data.getJSONObject(i).getDouble("x")).floatValue();
                            y = ((Double) data.getJSONObject(i).getDouble("y")).floatValue();
                            firing = data.getJSONObject(i).getBoolean("firing");
                        } catch(Exception e) {
                            System.out.println("JSON parsing error");
                        }

                        Rocket player = players.get(id);
                        if (player == null) {
                            Gdx.app.log("updatePlayers", "Received update for null players " + id + "\n" + players);
                            continue;
                        }

                        rocketTransformMap.put(player, new Transform(new Vector2(x, y), 0f));

                        if (firing) {
                            firingGroup.add(player);
                        }
                    }

                    playersTransformUpdates.put(timeStamp, rocketTransformMap);
                } catch(Exception e) {
                    Gdx.app.error("SocketIO", "Error in updatePlayers", e);
                }
            }
        });
    }
}
