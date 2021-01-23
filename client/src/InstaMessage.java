import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class InstaMessage extends JFrame{
    static GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
    static GraphicsDevice device = graphics.getDefaultScreenDevice();
    static Border blackLine = BorderFactory.createLineBorder(Color.black);
    static JLabel Title = new JLabel("InstaMessage");
    static JLabel Incorrect = new JLabel("Incorrect password or IP address.");
    static JTextField IP = new JTextField("Enter IP address");
    static JTextField Nickname = new JTextField("Enter Nickname");
    static JTextField Password = new JTextField("Enter password");
    static JButton close = new JButton("X");
    static Icon loadingGif;
    static JLabel loading;
    static Client chatClient;
    static InstaMessage instaMessage;

    InstaMessage() {
        // device.setFullScreenWindow(this);
        setSize(1000,1000);
        setLayout(null);
        setTitle("InstaMessage");

        loadingGif = new ImageIcon(this.getClass().getResource("connect.gif"));

        close.setBounds(getWidth()-50,0,50,50);
        close.addActionListener(e -> {
            setVisible(false);
            System.exit(0);
        });
        add(close);


        IP.setBounds(getWidth()/2-100, 60, 200, 20);
        IP.setHorizontalAlignment(JTextField.CENTER);
        IP.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (IP.getText().equals("Enter IP address")) IP.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (IP.getText().equals("")) IP.setText("Enter IP address");
            }
        });
        add(IP);

        Nickname.setBounds(getWidth()/2-100, 90, 200, 20);
        Nickname.setHorizontalAlignment(JTextField.CENTER);
        Nickname.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Nickname.getText().equals("Enter Nickname")) Nickname.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Nickname.getText().equals("")) Nickname.setText("Enter Nickname");
            }
        });
        add(Nickname);


        Password.setBounds(getWidth()/2-100, 120, 200, 20);
        Password.setHorizontalAlignment(JTextField.CENTER);
        Password.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Password.getText().equals("Enter password")) Password.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Password.getText().equals("")) Password.setText("Enter password");
            }
        });
        Password.addActionListener(e -> {
            chatClient = new Client(IP.getText(), Password.getText());
            chatClient.start();
        });
        add(Password);

        Incorrect.setBounds(getWidth()/2-100, 150, 200, 20);
        Incorrect.setHorizontalAlignment(JTextField.CENTER);
        Incorrect.setForeground(new Color(255, 0 ,0));
        Incorrect.setVisible(false);
        add(Incorrect);

        Title.setBounds(getWidth()/2-getWidth()/4, 0, getWidth()/2, 60);
        Title.setFont(getFont(21));
        Title.setHorizontalAlignment(JLabel.CENTER);
        Title.setVerticalAlignment(JLabel.CENTER);
        add(Title);

        loading = new JLabel(loadingGif);
        loading.setBounds(getWidth()/2-50, 220, 100,100);
        loading.setVisible(false);
        add(loading);

        setVisible(true);
        setVisible(false);
        setVisible(true);
    }
    public static void main(String[] args) {
        instaMessage = new InstaMessage();
        new Resizer().start();
    }
    public static String formatForJLabel(String orig)
    {
        return "<html>" + orig.replaceAll("\n", "<br>");
    }
    public static Font getFont(int size)
    {
        return new Font("Sans-Serif", Font.PLAIN, size);
    }
}