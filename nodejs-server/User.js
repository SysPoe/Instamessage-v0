class User {
    socket;
    username;
    ipAddress;

    constructor(socket, username, ipAddress) {
        this.socket = socket;
        this.username = username;
        this.ipAddress = ipAddress;
    }

    sendMessage(chat, message) {
        chat = chat + "\n" + message;
        return chat;
    }
}