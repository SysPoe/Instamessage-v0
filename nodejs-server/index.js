let chat = [];
let bannedIPs = [];
let adminIPs = [];
let password = "";

const WebSocket = require('ws');
const HTTP = require('http');
const HTTPServer = HTTP.createServer();
const inquirer = require('inquirer')
const fs = require("fs");


HTTPServer.listen(63438);

const WSServer = new WebSocket.Server({
    httpServer: HTTPServer,
    port: 63439
});

let users = [];
WSServer.on('connection', function(socket) {
    if(bannedIPs.find(value => value.localeCompare(socket._socket.remoteAddress))) {
        socket.close();
    }
    socket.on('message', function(msg) {
        let message = JSON.parse(msg);
        console.log(message);
        if(message.type === "validate") {
            if(message.password.localeCompare(password)) {
                if(validUsername(message.username)) {
                    let token = getUniqueID();
                    let admin = false;
                    for(let i = 0; i < adminIPs.length; i++) if (adminIPs[i].localeCompare(socket._socket.remoteAddress)) admin = true;
                    let user;
                    if(admin === true) user = { "socket": socket, "username": message.username, "ipAddress": socket._socket.remoteAddress, "token": token, "permissionLevel": 2 };
                    else user = { "socket": socket, "username": message.username, "ipAddress": socket._socket.remoteAddress, "token": token, "permissionLevel": 0 };
                    users.push(user);
                    console.log({ username: message.username, ipAddress: socket._socket.remoteAddress, token: token, permissionLevel: 0 })
                    console.log("New client: " + user.ipAddress + " " + user.username);
                    socket.send(JSON.stringify({type: "token", content: token}));
                } else socket.send(JSON.stringify({ type: 'error', content: 'Invalid Username' }));
            } else socket.send(JSON.stringify({ type: 'error', content: 'Incorrect Password', entered: message.password }));
        } else if(message.type === "chat") {
            if(message.content.startsWith("/")) {
                command(getUserFromSocket(socket), message.content);
            } else {
                sendMessage(getUserFromSocket(socket), message.content);
            }
        }
        else if(message.type === "error") console.log("Error from client "+ JSON.stringify(getUserFromSocket(socket).ipAddress) + ": "+message.content);
        else socket.send(JSON.stringify({type: "error", content: "Invalid Request!"}));
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
        if(users[i].username.localeCompare(username)) return false;
    } return true;
}
function getUserFromSocket(socket) {
    let user;
    for(let i = 0; i < users.length; i++) {
        if(users[i].socket === socket) user = users[i];
    } return user;
}
function getUserFromUsername(username) {
    let user;
    for(let i = 0; i < users.length; i++) {
        if(users[i].username === username ) user = users[i];
    } return user;
}
function getUserFromToken(token) {
    let user = null;
    for(let i = 0; i < users.length; i++) {
        if(users[i].token === token) user = users[i];
    } return user;
}
function getUserByIP(ip) {
    let user = null;
    for(let i = 0; i < users.length; i++) {
        if(users[i].ipAddress === ip) user = users[i];
    } return user;
}
function getUsersByPermissionLevel(level, aboveCounts) {
    let userList = [];
    for(let i = 0; i < users.length; i++) {
        if(users[i].permissionLevel === level) userList.push(users[i]);
        else if(users[i].permissionLevel >= level && aboveCounts) userList.push(users[i]);
    } return userList;
}
function sendMessage(user, message) {
    if(user.permissionLevel === 0) {
        chat.push("0 "+user.username+": "+message);
        for(let i = 0; i < users.length; i++) users[i].socket.send(JSON.stringify({type: "message", message: "[GUEST] "+user.username+": "+message}));
    } else if(user.permissionLevel === 1 ) {
        chat.push("1 "+user.username+": "+message);
        let usersWithPerms = getUsersByPermissionLevel(1, true);
        for(let i = 0; i < usersWithPerms.length; i++) {
            usersWithPerms[i].socket.send(JSON.stringify({type: "message", message: "[MEMBER] "+user.username+": "+message}));
        }
    } else if(user.permissionLevel === 2 ) {
        chat.push("2 "+user.username+": "+message);
        let usersWithPerms = getUsersByPermissionLevel(2, false);
        for(let i = 0; i < usersWithPerms.length; i++) {
            usersWithPerms[i].socket.send(JSON.stringify({type: "message", message: "[ADMIN] "+user.username+": "+message}));
        }
    }
}
function command(user, message) {
    let commandsTeir2 = `
    Commands: 
    /getip {username}
    Gets the IP of the specified user
    \n
    /member {username}
    Sets the specified user to a member status
    \n
    /guest {username}
    Sets the specified user to a guest status
    \n
    /banIP {ip}
    Bans the specified IP
    \n
    /unbanIP {ip}
    Unbans the specified IP
    \n
    /kickIP
    Kicks the specified IP
    \n
    /all
    Sends a message to all the tiers
    `;
    if(user.permissionLevel === 2) {
        try {
            if (message.startsWith("/getip")) {
                let u = getUserFromUsername(message.replace("/getip ", ""));
                user.socket.send(JSON.stringify({
                    type: "message",
                    message: u.username + "'s IP Address is: " + u.ipAddress
                }));
            } else if (message.startsWith("/member")) {
                let u = getUserFromUsername(message.replace("/member ", ""));
                u.permissionLevel = 1;
                user.socket.send("Set " + u.username + "'s permission level to member.");
            } else if (message.startsWith("/guest")) {
                let u = getUserFromUsername(message.replace("/guest ", ""));
                u.permissionLevel = 0;
                user.socket.send("Set " + u.username + "'s permission level to guest.");
            } else if (message.startsWith("/banIP")) {
                bannedIPs.push(message.replace("/banIP ", ""));
                user.socket.send("Banned ip: " + message.replace("/guest ", ""));
                getUserByIP(message.replace("/banIP ", "")).socket.send(JSON.stringify({
                    type: "error",
                    content: "Banned by Admin"
                }));
                getUserByIP(message.replace("/banIP ", "")).socket.close();
            } else if (message.startsWith("/unbanIP")) {
                bannedIPs = bannedIPs.filter(value => value !== message.replace("/unbanIP ", ""));
                user.socket.send("Unbanned ip: " + message.replace("/unbanIP ", ""));
            } else if (message.startsWith("/kickIP")) {
                getUserByIP(message.replace("/banIP ", "")).socket.send(JSON.stringify({
                    type: "error",
                    content: "Kicked by Admin"
                }));
                getUserByIP(message.replace("/banIP ", "")).socket.close();
            } else if (message.startsWith("/all")) for (let i = 0; i < users.length; i++) users[i].socket.send(JSON.stringify({
                type: "message",
                message: "[ALL]" + user.username + ": " + message.replace("/all ", "")
            }));
            else if (message.startsWith("/help")) {
                if (user.permissionLevel === 0) user.socket.send(JSON.stringify({
                    type: "message",
                    message: "Commands: \n/all"
                }));
                else if (user.permissionLevel === 1) user.socket.send(JSON.stringify({
                    type: "message",
                    message: "Commands: \n/all"
                }));
                else if (user.permissionLevel === 2) user.socket.send(JSON.stringify({
                    type: "message",
                    message: commandsTeir2
                }));
            } else {
                sendMessage(user, message);
            }
        } catch (e) {
            console.log(e);
        }
    } else {
        if(message.startsWith("/all")) for(let i = 0; i < users.length; i++) users[i].socket.send(JSON.stringify({type: "message", message: "[ALL]" + user.username+": "+message.replace("/all ","")}));
        else if(message.startsWith("/help")) {
            if(user.permissionLevel === 0) user.socket.send(JSON.stringify({type: "message", message: "Commands: \n/all"}));
            else if(user.permissionLevel === 1) user.socket.send(JSON.stringify({type: "message", message: "Commands: \n/all"}));
            else if(user.permissionLevel === 2) user.socket.send(JSON.stringify({type: "message", message: commandsTeir2}));
        } else {
            sendMessage(user, message);
        }
    }
}
const getUniqueID = () => {
    const s4 = () => Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    return s4() + s4() + '-' + s4() + s4() + '-' + s4() + s4() + '-' + s4() + s4();
};

const website = HTTP.createServer(function (req, res) {
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.write(fs.readFileSync("website/index.html"));
    res.end();
}).listen(80);

let psQuestions = [
    {
        type: 'input',
        name: 'data',
        message: 'Type the password'
    }
]
inquirer.prompt(psQuestions).then(datas => {
    password = datas['data'];
    console.log("Password: "+password);
    prompt();
});
let questions = [
    {
        type: 'input',
        name: 'data',
        message: "Type a username to make that user an admin -> \n"
    }
];
function prompt() {
    inquirer.prompt(questions).then(datas => {
        try {
            let data = datas['data'];
            adminIPs.push(getUserFromUsername(data.toString()).socket._socket.remoteAddress);
            getUserFromUsername(data.toString()).permissionLevel = 2;
            console.log("Made " + getUserFromUsername(data.toString()).ipAddress + " an admin");
        } catch (e) {
            console.log(e);
        }
        prompt();
    });
}