import com.eclipsesource.json.Json;

import java.io.*;
import java.net.*;

public class Client extends Thread {
    String ip;
    String password;
    PrintWriter printOut;
    InetAddress host;
    Socket socket;
    BufferedReader reader;
    Client(String ip, String password) {
        this.ip=ip;
        this.password = password;
    }

    @Override
    public void run() {
        try{
            InstaMessage.loading.setVisible(true);
            InstaMessage.Incorrect.setText("Incorrect password or IP address.");
            host = InetAddress.getByName(ip);
            socket = new Socket( host, 1456);
            System.out.println("Connecting to remote server...");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            printOut = new PrintWriter(socket.getOutputStream());
            printOut.println(Json.object().add("requestType", "verify").add("content", password));
            printOut.flush();
            System.out.println("Sending value: \"" + Json.object().add("requestType", "verify").add("content", password) + "\" to server");
            String messageIn = reader.readLine();
            if(messageIn.equals("denied")) {
                InstaMessage.loading.setVisible(false);
                InstaMessage.Incorrect.setVisible(true);
                System.out.println("Access to remote server denied.");
                socket.close();
            }
            else if (messageIn.equals("accept")) {
                printOut.println(Json.object().add("requestType", "registerUsername").add("content" , InstaMessage.Nickname.getText()));
                System.out.println("Sending value: \"" + Json.object().add("requestType", "registerUsername").add("content" , InstaMessage.Nickname.getText()) + "\" to server");
                printOut.flush();
                System.out.println("Registering nickname...");
                messageIn = reader.readLine();

                if (messageIn.equals("accept")) {
                    InstaMessage.Title.setVisible(false);
                    InstaMessage.IP.setVisible(false);
                    InstaMessage.Password.setVisible(false);
                    InstaMessage.Incorrect.setVisible(false);
                    InstaMessage.loading.setVisible(false);
                    System.out.println("connected.");
                }
                else if (messageIn.equals("denied")) {
                    InstaMessage.Incorrect.setVisible(true);
                    InstaMessage.loading.setVisible(false);
                    InstaMessage.Incorrect.setText("That nickname is taken!");
                    socket.close();
                }
                else {
                    InstaMessage.loading.setVisible(false);
                    System.out.println("Server returned unexpected value: " + messageIn);
                    socket.close();
                }
            }
            else {
                InstaMessage.loading.setVisible(false);
                System.out.println("Server returned unexpected value: " + messageIn);
                socket.close();
            }
        } catch(IOException ioe) {
            System.out.println("Failed to connect.");
            InstaMessage.Incorrect.setVisible(true);
            InstaMessage.Incorrect.setText("Incorrect password or IP address.");
            InstaMessage.loading.setVisible(false);
        }
    }
}
