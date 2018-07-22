var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];

server.listen(8080, () => {
    console.log("Server is running...");
});

io.on('connection', (socket) => {
    console.log('Player Connected');

    socket.emit('socketID', { id: socket.id });
    socket.emit('getPlayers', players);

    socket.broadcast.emit('newPlayer', { id: socket.id });

    players.push(new player(socket.id, 0, 0));

    // Update player positions every 1/60 seconds
    var updates = setInterval(function(){
        socket.emit('updatePlayers', players); 
    }, 16);

    // Event Handlers

    socket.on('disconnect', () => {
        console.log('Player Disconnected');
        
        socket.broadcast.emit('playerDisconnected', { id: socket.id });

        for (var i = 0; i < players.length; i++) {
            if (players[i].id == socket.id) {
                players.splice(i, 1);
            }
        }

        clearInterval(updates);
    });

    socket.on('input', (data, dt) => {
        console.log('Input Received from Player ' + socket.id);
        console.log(players);

        for (var i = 0; i < players.length; i++) {
            if (typeof players[i] === "undefined") {
                console.log('undefined player');
                continue;
            }

            if (players[i].id == socket.id) {
                move(players[i], data.direction, data.dt);  
            }

        }              
    });
});

function move(player, direction, dt) {
    var dx = 200;
    var dy = 200;

    var xMult = 0;
    var yMult = 0;

    if (direction == InputEnum.INVALID) {
        console.log("invalid");
        return;
    } else if (direction == InputEnum.LEFT) {
        xMult = -1;
    } else if (direction == InputEnum.RIGHT) {
        xMult = 1;
    } else if (direction == InputEnum.UP) {
        yMult = 1;
    } else if (direction == InputEnum.DOWN) {
        yMult = -1;
    } else if (direction == InputEnum.FIRE) {
        fire(player);
    } else {
        console.log("Direction " + direction + " not recognized");
    }

    console.log("xMult: " + xMult + ", yMult: " + yMult);

    player.x += dx * xMult * dt;
    player.y += dy * yMult * dt;
}

function fire(player) {
    return;
}

// Player

function player(id, x, y) {
    this.id = id;
    this.x = x;
    this.y = y;
}

// Utils

const InputEnum = Object.freeze({"INVALID":0, "LEFT":1, "RIGHT":2, "UP":3, "DOWN":4, "FIRE":5})