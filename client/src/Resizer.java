import javax.swing.*;
import java.awt.*;

public class Resizer extends Thread {
    Resizer() {
    }

    @Override
    public void run() {
        while(true) {
            InstaMessage.Nickname.setBounds(InstaMessage.instaMessage.getWidth() / 2 - 100, 90, 200, 20);
            InstaMessage.Nickname.setHorizontalAlignment(JTextField.CENTER);

            InstaMessage.loading.setBounds(InstaMessage.instaMessage.getWidth() / 2 - 50, 220, 100, 100);

            InstaMessage.Title.setBounds(InstaMessage.instaMessage.getWidth() / 2 - InstaMessage.instaMessage.getWidth() / 4, 0, InstaMessage.instaMessage.getWidth() / 2, 60);
            InstaMessage.Title.setFont(InstaMessage.getFont(InstaMessage.instaMessage.getWidth() / 48));
            InstaMessage.Title.setHorizontalAlignment(JLabel.CENTER);
            InstaMessage.Title.setVerticalAlignment(JLabel.CENTER);

            InstaMessage.Incorrect.setBounds(InstaMessage.instaMessage.getWidth() / 2 - 100, 150, 200, 20);
            InstaMessage.Incorrect.setHorizontalAlignment(JTextField.CENTER);
            InstaMessage.Incorrect.setForeground(new Color(255, 0, 0));

            InstaMessage.Password.setBounds(InstaMessage.instaMessage.getWidth() / 2 - 100, 120, 200, 20);
            InstaMessage.Password.setHorizontalAlignment(JTextField.CENTER);

            InstaMessage.IP.setBounds(InstaMessage.instaMessage.getWidth() / 2 - 100, 60, 200, 20);
            InstaMessage.IP.setHorizontalAlignment(JTextField.CENTER);

            InstaMessage.close.setBounds(InstaMessage.instaMessage.getWidth() - 65, 0, 50, 50);
        }
    }
}
