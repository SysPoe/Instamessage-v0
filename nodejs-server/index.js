let chat = "";
let password = "";

const readline = require('readline').createInterface({
    input: process.stdin,
    output: process.stdout
});

readline.question('Enter password ->  ', password => {
    this.password = password;
    readline.close();
});

const WebSocket = require('ws');
const server = new WebSocket.Server({
    port: 63439
});

let users = [];
server.on('connection', function(socket) {
    socket.on('message', function(msg) {
        message = JSON.parse(msg);
        if(message.type === "validate") {
             if(message.password === password) {
                 if(validUsername(message.username)) {
                     let user = new User(socket, message.username)
                     users.push(user);
                     console.log("New client: " + JSON.stringify(user));
                     socket.send(JSON.stringify({type: "token", content: getToken()}));
                 } else socket.send(JSON.stringify({ type: 'error', content: 'Invalid Username' }));
             } else socket.send(JSON.stringify({ type: 'error', content: 'Incorrect Password' }));
        } else if(message.type === "error") console.log("Error from client \""+ JSON.parse(getUserFromSocket(socket)) + "\": "+message.content);
        else socket.send(JSON.stringify({type: "error", content: "Invalid Request!"}))
    });
    socket.on('close', function() {
        let user =  getUserFromSocket(socket);
        users = users.filter(u => u !== user);
    });
    socket.on('error', function (error) {
        console.log(error);
    });
});
function validUsername(username) {
    for(let i = 0; i < users.length; i++) {
        if(users[i].username === username) return false;
    }
    return true;
}
function getUserFromSocket(socket) {
    let user;
    for(let i = 0; i < users.length; i++) {
        if(users[i].socket === socket) user = users[i];
    }
    return user;
}
function getUserFromUsername(username) {
    let user;
    for(let i = 0; i < users.length; i++) {
        if(users[i].username === username ) user = users[i];
    }
    return user;
}