public class User {
    private String username;
    private double token;
    User(String username, double token) {
        this.username = username;
        this.token = token;
    }

    public void sendMessage(String message) {
        Server.chatSession = Server.chatSession + "\n" + username + ": " + message;
        System.out.println(username + ": " + message);
    }

    public boolean matchToken(double token) {
        if(token == this.token) return true;
        else return false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
