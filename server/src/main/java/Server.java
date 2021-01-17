import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        try {
            ArrayList<Token> tokens = new ArrayList<Token>();
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
                    webSocket.send("Hello!");
                    System.out.println("Recieved: " + s);
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
}