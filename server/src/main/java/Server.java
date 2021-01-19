import com.google.gson.JsonSyntaxException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.GsonBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    static String password = "";
    static String chatSession = "";
    static ArrayList<User> users = new ArrayList<>();
    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter password: ");
            password = scan.nextLine();
            WebSocketServer server = new WebSocketServer(new InetSocketAddress(63439)) {
                @Override
                public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                    webSocket.send(new GsonBuilder().create().toJson(new Message("client", "accepted", 0)));
                    System.out.println("New client");
                }

                @Override
                public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                    System.out.println("Client disconnected.");
                }

                @Override
                public void onMessage(WebSocket webSocket, String s) {
                    try {
                        System.out.println(s);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        if (s.equals("")) webSocket.send(gsonBuilder.create().toJson(new Error("invalid request")));
                        else {
                            Message message = gsonBuilder.create().fromJson(s, Message.class);
                            switch (message.type) {
                                case "validate" -> {
                                    if (message.content.equals(password)) {
                                        User user;
                                        double token = createToken();
                                        user = new User("placeholder", token);
                                        users.add(user);
                                        webSocket.send(gsonBuilder.create().toJson(new Message("token", token + "", 0)));
                                        System.out.println("New user: " + gsonBuilder.create().toJson(user));
                                    } else {
                                        webSocket.send(gsonBuilder.create().toJson(new Error("invalid password")));
                                    }
                                }
                                case "chat" -> {
                                    getUser(message.token).sendMessage(message.content);
                                }

                                default -> webSocket.send(gsonBuilder.create().toJson(new Error("invalid request")));
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
    static User getUser(double token) {
        User user = null;
        for(User u : users) if (u.matchToken(token)) user = u;
        return user;
    }
}