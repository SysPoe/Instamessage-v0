import com.google.gson.JsonSyntaxException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.GsonBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class Server {
    static String chatSession = "";
    static ArrayList<User> users = new ArrayList<>();
    public static void main(String[] args) {
        try {
            WebSocketServer server = new WebSocketServer(new InetSocketAddress(63439)) {
                @Override
                public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                    webSocket.send(new GsonBuilder().create().toJson(new Message("client", "accepted")));
                    System.out.println("New client");
                }

                @Override
                public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                    System.out.println("Client disconnected.");
                }

                @Override
                public void onMessage(WebSocket webSocket, String s) {
                    try {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        if (s.equals("")) webSocket.send(gsonBuilder.create().toJson(new Error("invalid request")));
                        else {
                            Message message = gsonBuilder.create().fromJson(s, Message.class);
                            switch (message.type) {
                                case "validate" -> {
                                    String password = "pass";
                                    if (message.content.equals(password)) {
                                        webSocket.send(gsonBuilder.create().toJson(new Message("token", createToken() + "")));
                                    } else {
                                        webSocket.send(gsonBuilder.create().toJson(new Error("invalid password")));
                                    }
                                    System.out.println(message.content);
                                    break;
                                }

                                default -> { webSocket.send(gsonBuilder.create().toJson(new Error("invalid request"))); }
                            }
                        }
                    } catch(JsonSyntaxException e) {
                        System.out.println("Received unexpected message from client: "+s);
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
        } catch(JsonSyntaxException e) {
            System.out.println("Received unexpected message from client");
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