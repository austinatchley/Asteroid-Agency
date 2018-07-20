var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

server.listen(8080, function() {
    console.log("Server is running...");
});

io.on('connection', function(socket) {
    console.log('Player Connected');

    socket.on('disconnect', function() {
        console.log('Player Disconnected');
    });
});