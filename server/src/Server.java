import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread{
    public static Server s;
    ServerSocket socket1;
    String password;
    static float serverVersion = 1.0f;
    static int max;
    static ArrayList<Float> UUIDs = new ArrayList<>();
    static AtomicInteger users = new AtomicInteger(0);
    static String chat;
    static ArrayList<String> userNames = new ArrayList<>();

    public Server() {
    }

    @Override
    public void run() {
        try {
            socket1 = new ServerSocket(1456);

            System.out.println("Enter password:");
            Scanner s = new Scanner(System.in);
            password = s.nextLine();
            System.out.println("Password set to: "+password+"\n");

            System.out.println("Set max users (Type 0 for no limit).");
            s = new Scanner(System.in);
            try {max = Integer.parseInt(s.nextLine()) - 1;}
            catch (NumberFormatException e) {
                System.out.println("Please enter a number next time. :(");
                System.exit(2);
            }
            if(max == -1) System.out.println("No user limit.");
            else System.out.println("Max users set to: "+(max+1)+"\n");

            System.out.println("Server started.");

            while(true) {
                if(users.get() < max+1 || max == -1) {
                    // Accept client request and start new thread to handle it.
                    Socket clientRequest = socket1.accept();
                    new ClientHandler(clientRequest).start();
                    users.incrementAndGet();
                    System.out.println("New client accepted: "+clientRequest.getInetAddress().getHostAddress());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        s = new Server();
        System.out.println("Starting server...");
        s.start();

    }
}
