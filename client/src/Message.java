public class Message {
    String type, content;
    double token;

    Message(String type, String content, double token) {
        this.type = type;
        this.content = content;
        this.token = token;
    }
}