package testTools;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class test {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Swing App.App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button = new JButton("Click me!");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button clicked!");
            }
        });

        frame.getContentPane().add(button);
        frame.pack();
        frame.setVisible(true);
    }
}
