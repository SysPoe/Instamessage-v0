import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.*;
import java.net.*;

public class Client extends Thread {
    String ip;
    String password;
    Client(String ip, String password) {
        this.ip=ip;
        this.password = password;
    }

    @Override
    public void run() {
        try{
            WebSocketClient webSocketClient = new WebSocketClient(new URI("ws://"+ip+":")) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    send();
                }

                @Override
                public void onMessage(String s) {

                }

                @Override
                public void onClose(int i, String s, boolean b) {

                }

                @Override
                public void onError(Exception e) {

                }
            }

        } catch(IOException ioe) {
            System.out.println("Failed to connect.");
            InstaMessage.Incorrect.setVisible(true);
            InstaMessage.Incorrect.setText("Incorrect password or IP address.");
            InstaMessage.loading.setVisible(false);
        }
    }
}
