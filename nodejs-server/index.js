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
const HTTP = require('http');
const HTTPserver = HTTP.createServer();

HTTPserver.listen(63438);

const WSserver = new WebSocket.Server({
    httpServer: HTTPserver,
    port: 63439
});

let users = [];
WSserver.on('connection', function(socket) {
    socket.on('message', function(msg) {
        let message = JSON.parse(msg);
        console.log(message);
        if(message.type === "validate") {
             if(message.password === password) {
                 if(validUsername(message.username)) {
                     let token = getUniqueID();
                     let user = { socket: socket, username: message.username, ipAddress: socket.remoteAddress, token: token };
                     users.push(user);
                     console.log("New client: " + user.username);
                     socket.send(JSON.stringify({type: "token", content: token}));
                 } else socket.send(JSON.stringify({ type: 'error', content: 'Invalid Username' }));
             } else {
                 console.log("Client \""+message.username+"\" tried to authenticate with password: "+message.password);
                 socket.send(JSON.stringify({ type: 'error', content: 'Incorrect Password', entered: message.password }));
             }
        } else if(message.type === "error") console.log("Error from client \""+ JSON.parse(getUserFromSocket(socket)) + "\": "+message.content);
        else socket.send(JSON.stringify({type: "error", content: "Invalid Request!"}))
    });
    socket.on('close', function() {
        let user =  getUserFromSocket(socket);
        users = users.filter(u => u !== user);
        console.log("Client \""+user.username+"\" disconnected")
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
function getUserFromToken(token) {
    let user = null;
    for(let i = 0; i < users.length; i++) {
        if(users[i].token === token) user = users[i];
    }
    return user;
}
const getUniqueID = () => {
    const s4 = () => Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    return s4() + s4() + '-' + s4();
};