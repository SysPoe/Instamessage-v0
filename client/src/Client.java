import com.google.gson.GsonBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class Client extends Thread {
    String ip, password, nickname;
    static double token;
    static WebSocketClient webSocketClient;

    Client(String ip, String password, String nickname) {
        this.ip=ip;
        this.password = password;
        this.nickname = nickname;
        token = 0.0;
    }

    @Override
    public void run() {
        try{
            webSocketClient = new WebSocketClient(new URI("ws://"+ip+":63439/")) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    send(new GsonBuilder().create().toJson(new Message("validate", password, token)));
                    System.out.println("Connecting to server: ws://"+ip+":63439/");
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
                                    InstaMessage.declined();
                                    InstaMessage.loading.setVisible(false);
                                    InstaMessage.Incorrect.setVisible(true);
                                    InstaMessage.Incorrect.setText(message.content);
                                    System.out.println("Error: " + message.content);
                                    close();
                                }
                                case "token" -> {
                                    token = Double.parseDouble(message.content);
                                    send(gsonBuilder.create().toJson(new Message("username", nickname, token)));
                                }
                                case "chat" -> {
                                    InstaMessage.accepted();
                                    InstaMessage.chat.setText(InstaMessage.formatForJLabel(message.content));
                                }
                                case "client" -> {
                                    System.out.println("\"Server\":" + message.type + " "+ message.content);
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
                    InstaMessage.loading.setVisible(false);
                    InstaMessage.Incorrect.setVisible(true);
                    InstaMessage.Incorrect.setText(e.getMessage());
                }
            };
            webSocketClient.connect();

        } catch (URISyntaxException uriSyntaxException) {
            System.out.println(uriSyntaxException.getMessage());
        }
    }
}
