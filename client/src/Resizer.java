import javax.swing.*;
import java.awt.*;

public class Resizer extends Thread {
    Resizer() {
    }

    @Override
    public void run() {
        while(true) {
            int width = InstaMessage.instaMessage.getWidth();
            int height = InstaMessage.instaMessage.getHeight();
            InstaMessage.Nickname.setBounds(width / 2 - 100, 90, 200, 20);
            InstaMessage.Nickname.setHorizontalAlignment(JTextField.CENTER);

            InstaMessage.loading.setBounds(width / 2 - 50, 220, 100, 100);

            InstaMessage.Title.setBounds(width / 2 - width / 4, 0, width / 2, 60);
            InstaMessage.Title.setFont(InstaMessage.getFont(21));
            InstaMessage.Title.setHorizontalAlignment(JLabel.CENTER);
            InstaMessage.Title.setVerticalAlignment(JLabel.CENTER);

            InstaMessage.Incorrect.setBounds(width / 2 - 100, 150, 200, 20);
            InstaMessage.Incorrect.setHorizontalAlignment(JTextField.CENTER);
            InstaMessage.Incorrect.setForeground(new Color(255, 0, 0));

            InstaMessage.Password.setBounds(width / 2 - 100, 120, 200, 20);
            InstaMessage.Password.setHorizontalAlignment(JTextField.CENTER);

            InstaMessage.IP.setBounds(width / 2 - 100, 60, 200, 20);
            InstaMessage.IP.setHorizontalAlignment(JTextField.CENTER);

            InstaMessage.close.setBounds(width - 65, 0, 50, 50);

            InstaMessage.chat.setBounds(width/4, 100, width/2, height/2);
        }
    }
}
