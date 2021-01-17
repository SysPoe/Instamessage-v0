import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Server {
    static String chatSession = "";
    static ArrayList<User> users = new ArrayList<User>();
    public static void main(String[] args) {
        try {
            WebSocketServer server = new WebSocketServer() {
                @Override
                public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                    webSocket.send("Hello!");
                    System.out.println("New client");
                }

                @Override
                public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                    System.out.println("Client disconnected.");
                }

                @Override
                public void onMessage(WebSocket webSocket, String s) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    if(s == "") webSocket.send(gsonBuilder.create().toJson(new Error("invalid request")));
                    else {
                        Message message = gsonBuilder.create().fromJson(s, Message.class);
                        switch (message.type) {
                            case "validate":
                                String password = "pass";
                                if (message.content == password) {
                                    webSocket.send(createToken() + "");
                                } else {
                                    webSocket.send(gsonBuilder.create().toJson(new Error("invalid password")));
                                }
                                System.out.println(message.content);
                                break;

                            default: {
                                webSocket.send(gsonBuilder.create().toJson(new Error("invalid request")));
                            }
                        }
                    }
                }

                @Override
                public void onError(WebSocket webSocket, Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onStart() {
                    System.out.println("Server started.");
                }
            };
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static double createToken() {
        boolean valid = false;
        double token = 0.0;
        while(!valid) {
            valid = true;
            token = Math.random();
            for(User user : users) {
                if(user.matchToken(token)) valid = false;
            }
        }
        return token;
    }
}