import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.time.Instant;

public class Client extends Thread {
    String ip, password, nickname;
    double token;

    Client(String ip, String password, String nickname) {
        this.ip=ip;
        this.password = password;
        this.nickname = nickname;
        token = 0.0;
    }

    @Override
    public void run() {
        try{
            WebSocketClient webSocketClient = new WebSocketClient(new URI("ws://"+ip+":63439/")) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    send(new GsonBuilder().create().toJson(new Message("validate", password, token)));
                    System.out.println("Connecting to server: ws://\"+ip+\":63439/");
                }

                @Override
                public void onMessage(String s) {
                    try {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        if(s.equals("")) send(gsonBuilder.create().toJson(new Message("error", "invalid message", token)));
                        else {
                            Message message = gsonBuilder.create().fromJson(s, Message.class);
                            switch(message.type) {
                                case "error" -> {
                                    InstaMessage.loading.setVisible(false);
                                    if(message.content.equals("invalid password")) {
                                        InstaMessage.Incorrect.setVisible(true);
                                    } else {
                                        System.out.println("Error: " + message.content);
                                    }
                                    close(1);
                                }
                                case "token" -> {
                                    token = Double.parseDouble(message.content);
                                    send(gsonBuilder.create().toJson(new Message("username", nickname, token)));
                                }
                                case "chat" -> {
                                    System.out.println("chat: "+message.content);
                                }

                                default -> {
                                    System.out.println("Invalid message: "+s);
                                    send(gsonBuilder.create().toJson(new Message("error", "invalid message", token)));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("Closed connection: "+i);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            };
            webSocketClient.connect();

        } catch (URISyntaxException uriSyntaxException) {
            System.out.println(uriSyntaxException.getMessage());
        }
    }
}
