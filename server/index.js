"use strict";

const express = require('express');
const socketIO = require('socket.io');
const path = require('path');

const PORT = process.env.PORT || 3000;
const INDEX = path.join(__dirname, 'index.html');

const server = express()
    .use((req, res) => res.sendFile(INDEX))
    .listen(PORT, () => console.log(`Listening on ${PORT}`));

const io = socketIO(server);
var players = [];


// Utils

const InputEnum = Object.freeze({
    INVALID: 0,
    LEFT: 1,
    RIGHT: 2,
    UP: 3,
    DOWN: 4,
    FIRE: 5
});

// Player

function player(id, x, y) {
    "use strict";

    this.id = id;
    this.x = x;
    this.y = y;
}

function fire(player) {
    "use strict";

    return player;
}

function handleInput(player, direction, dt) {
    "use strict";

    const dx = 200;
    const dy = 200;

    var xMult = 0;
    var yMult = 0;

    if (direction === InputEnum.INVALID) {
        console.log("invalid");
        return;
    }

    if (direction === InputEnum.LEFT) {
        xMult = -1;
    } else if (direction === InputEnum.RIGHT) {
        xMult = 1;
    } else if (direction === InputEnum.UP) {
        yMult = 1;
    } else if (direction === InputEnum.DOWN) {
        yMult = -1;
    } else if (direction === InputEnum.FIRE) {
        fire(player);
    } else {
        console.log("Direction " + direction + " not recognized");
    }

    console.log("xMult: " + xMult + ", yMult: " + yMult);

    player.x += dx * xMult * dt;
    player.y += dy * yMult * dt;
}

io.on('connection', function (socket) {
    "use strict";

    console.log('Player Connected');

    var lastUpdate = (new Date()).getTime();

    socket.emit('socketID', {id: socket.id});
    socket.emit('getPlayers', players);

    socket.broadcast.emit('newPlayer', {id: socket.id});

    players.push(new player(socket.id, 0, 0));

    // Update player positions every 1/60 seconds
    var updates = setInterval(function () {
        socket.emit('updatePlayers', players);
    }, 16);

    var checkTimeOut = setInterval(function () {
        var date = new Date();
        var time = date.getTime();

        console.log("Player last updated " + (time - lastUpdate) + " ago.");

        if ((new Date()).getTime() - lastUpdate > 60000) {
            var i;
            for (i = 0; i < players.length; i += 1) {
                if (players[i].id === socket.id) {
                    players.splice(i, 1);
                }
            }
            socket.disconnect();
        }
    }, 20000);

    // Event Handlers

    socket.on('disconnect', function () {
        console.log('Player Disconnected');

        socket.broadcast.emit('playerDisconnected', {id: socket.id});

        var i;
        for (i = 0; i < players.length; i += 1) {
            if (players[i].id === socket.id) {
                players.splice(i, 1);
            }
        }

        clearInterval(updates);
        clearInterval(checkTimeOut);
    });

    socket.on('input', function (data, dt) {
        console.log('Input Received from Player ' + socket.id);
        console.log(players);

        var i;
        for (i = 0; i < players.length; i += 1) {
            if (players[i] !== "undefined") {
                if (players[i].id === socket.id) {
                    handleInput(players[i], data.direction, data.dt);
                }
            }
        }

        lastUpdate = (new Date()).getTime();
        console.log("Updated at " + lastUpdate);
    });
});