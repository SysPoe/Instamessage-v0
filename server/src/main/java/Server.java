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
    static ArrayList<WebSocket> sockets = new ArrayList<>();
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
                    sockets.remove(webSocket);
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
                                    if (message.content.equals(password)) {
                                        User user;
                                        double token = createToken();
                                        user = new User("placeholder", token);
                                        users.add(user);
                                        webSocket.send(gsonBuilder.create().toJson(new Message("token", token + "", 0)));
                                        System.out.println("New user: " + gsonBuilder.create().toJson(user));
                                        sockets.add(webSocket);
                                    } else {
                                        webSocket.send(gsonBuilder.create().toJson(new Error("Invalid Password")));
                                    }
                                }
                                case "chat" -> {
                                    User user = getUser(message.token);
                                    if(user.isAdmin()) {
                                        if(message.content.startsWith("/")) {
                                            if(message.content.startsWith("/kickall")) {
                                                for (WebSocket web : Server.sockets) {
                                                    web.send(new GsonBuilder().create().toJson(new Error("Kicked by admin")));
                                                }
                                                sockets.removeAll(sockets);
                                                users.removeAll(users);
                                            }
                                            else if(message.content.startsWith("/shutdown")) {
                                                for (WebSocket web : Server.sockets) {
                                                    web.send(new GsonBuilder().create().toJson(new Error("Server shutdown")));
                                                }
                                                System.exit(0);
                                            }
                                            else if(message.content.startsWith("/password")) {
                                                password = message.content.replaceFirst("/password ", "");
                                            }
                                        }
                                    } else {
                                        user.sendMessage(message.content);
                                        for (WebSocket web : Server.sockets) {
                                            web.send(new GsonBuilder().create().toJson(new Message("chat", Server.chatSession, 0)));
                                        }
                                    }
                                }
                                case "username" -> {
                                    if(validUsername(message.content)) {
                                        getUser(message.token).setUsername(message.content);
                                        webSocket.send(new GsonBuilder().create().toJson(new Message("chat", Server.chatSession, 0)));
                                    }
                                    else webSocket.send(gsonBuilder.create().toJson(new Error("Invalid username!")));
                                }
                                case "error" -> System.out.println("Error from client \""+getUser(message.token).getUsername() + "\": "+message.content);

                                default -> webSocket.send(gsonBuilder.create().toJson(new Error("invalid request")));
                            }
                        }
                    } catch(JsonSyntaxException e) {
                        System.out.println("Received unexpected message from client: "+s);
                    } catch (NullPointerException e) {
                        // Do nothing
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
            System.out.print("Type a username to make that user an admin.\n");
            Scanner scanner = new Scanner(System.in);
            while(true) {
                try {
                  if(scanner.hasNextLine()) {
                      String s = scanner.nextLine();
                      getUser(s).setAdmin(getUser(s).isAdmin() == false);
                      // Used "getUser(s).isAdmin() == false" instead of "!getUser(s).isAdmin()" because it has issues for some reason.
                      System.out.println(s + ".isAdmin is now "+getUser(s).isAdmin());
                  }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
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
    static User getUser(String username) {
        User user = null;
        for(User u : users) if (u.getUsername().equals(username)) user = u;
        return user;
    }
    static boolean validUsername(String s) {
        boolean valid = true;
        for(User u : users) {
            if (u.getUsername().equals(s)) {
                valid = false;
                break;
            }
        }
        if(s.contains("[SERVER]")) return false;
        else if(s.contains("[ADMIN]")) return false;
        return valid;
    }
}