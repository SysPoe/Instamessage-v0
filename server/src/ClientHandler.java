import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.io.*;
import java.net.*;

/*
Legend:

1: Verify client
2: Register username
3: Check client and server version
 */

public class ClientHandler extends Thread{
    Socket client;
    String clientIP, clientName;
    ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            clientIP = client.getInetAddress().getHostAddress();
            boolean verified = false;
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());
            while (true) {
                String clientMessage = reader1.readLine();
                System.out.println("Data from client: " + clientIP + ". \"" + clientMessage + "\"");
                JsonObject clientMessageJson = Json.parse(clientMessage).asObject();


                // Check to see if client has disconnected
                if (clientMessage == null) {
                    client.close();
                    Server.users.decrementAndGet();
                    Server.userNames.remove(clientName);
                    System.out.println("Connection with client " + clientIP + " closed.");
                    return;
                }
                switch (clientMessageJson.get("requestType").asString()) {
                    case "verify" -> {
                        if (clientMessageJson.get("content").asString().equals(Server.s.password)) {
                            verified = true;
                            out.println("accept");
                            System.out.println("Verified client: " + clientIP + "");
                        } else {
                            out.println("denied");
                            System.out.println("Failed to verify client: " + clientIP + "");
                        }
                    }
                    case "registerUsername" -> {
                        if (verified) {
                            for (String user : Server.userNames) {
                                if (clientMessage.replaceFirst("2", "").equals(user)) {
                                    out.println("denied");
                                    client.close();
                                    return;
                                }
                            }
                            clientName = clientMessageJson.get("content").asString();
                            Server.userNames.add(clientName);
                            out.println("accept");
                        } else out.println("denied");
                    }
                    case "checkVersion" -> {
                        float ClientVersion = clientMessageJson.get("content").asFloat();
                        if (ClientVersion == Server.serverVersion) out.println("valid");
                        else if (ClientVersion > Server.serverVersion) out.println("outdated server");
                        else if (ClientVersion < Server.serverVersion) out.println("outdated client");
                    }
                    default -> {
                        System.out.println("Unexpected value at client request type: " + clientMessageJson.get("requestType").asString());
                        out.println(Json.object().add("type", "error").add("content", "invalid request type"));
                    }
                }
                out.flush();
            }

        } catch (EOFException | SocketException e) {
            try {
                client.close();
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
            Server.users.decrementAndGet();
            Server.userNames.remove(clientName);
            System.out.println("Connection with client " + clientIP + " closed.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
