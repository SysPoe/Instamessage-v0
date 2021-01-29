let ip;
let username;
let password;

let websocket;
let uuid = 0;
let previousChat = "";

function connect() {
    ip = document.getElementById("ip").value;
    username = document.getElementById("username").value;
    password = document.getElementById("password").value;

    websocket = new WebSocket("ws://"+ip+":63439");
    websocket.onopen = onOpen;
    websocket.onmessage = onMessage;
    websocket.onerror = onError;

    console.log("Connecting to: ws://"+ip+":63439");

    document.getElementById("web-send-message").addEventListener("keyup", function(e) {
        if(e.key === "Enter") {
            e.preventDefault();
            sendMessage();
        }
    });

    document.getElementById("loading").style.display="block";
    
}
function onOpen() {
    const tosendJSON = {type: "validate", password: password, username: username};
    const tosend = JSON.stringify(tosendJSON);
    console.log("Connected to server... authenticating");
    document.getElementById("loading").style.display="none";
    websocket.send(tosend);
}
function onMessage(e) {
    const message = JSON.parse(e.data);
    console.log(message);
    if (message.type === "token") {
        uuid = message.content;
        console.log("verified, token is: "+uuid);
        document.getElementById("web-error").innerHTML = "";
        document.getElementById("inputs").style.display = "none";
        document.getElementById("web-chat-screen").style.display = "block";
    } else if (message.type === "error") {
        console.log("Error: "+message.content);
        document.getElementById("web-error").innerHTML = message.content;
        document.getElementById("inputs").style.display = "block";
        document.getElementById("web-chat-screen").style.display = "none";
        if(message.content === "Invalid username!") {
            document.getElementById("inputs").style.display = "block";
            document.getElementById("web-chat-screen").style.display = "none";
        }
    } else if (message.type === "client") {
        console.log("Message from server: " + message.type + " " + message.content);
    } else if (message.type === "chat") {
        document.getElementById("web-chat").innerHTML = message.content.replace(/\n/g, "<br />");
        if (message.content.replace(/\n/g, "<br />") !== previousChat) {
            document.getElementById("web-chat").scrollTop = document.getElementById("web-chat").scrollHeight;
        }
        previousChat = message.content.replace(/\n/g, "<br />");
    } else if (message.type === "message") {
        document.getElementById("web-chat").innerHTML = document.getElementById("web-chat").innerHTML + message.message.replace(/\n/g, "<br />") + "<br />";
    }
    else {
        console.log("Recieved from server: "+e.data);
    }
}
function onError() {
    document.getElementById("web-error").innerHTML = "Unable to connect to host: server not found.";
    document.getElementById("loading").style.display="none";
}
function sendMessage() {
    websocket.send(JSON.stringify({type: "chat", content: document.getElementById("web-send-message").value, token: uuid}));
    document.getElementById("web-send-message").value = "";
}