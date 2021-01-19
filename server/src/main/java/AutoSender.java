import com.google.gson.GsonBuilder;
import org.java_websocket.WebSocket;

public class AutoSender extends Thread{
    AutoSender() {
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (WebSocket webSocket : Server.sockets) {
                    webSocket.send(new GsonBuilder().create().toJson(new Message("chat", Server.chatSession, 0)));
                }
                AutoSender.sleep(200);
            } catch (Exception e) {
                System.out.println("[AutoSender] "+e.getMessage());
            }
        }
    }
}
