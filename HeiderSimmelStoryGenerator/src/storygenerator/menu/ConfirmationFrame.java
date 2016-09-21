package storygenerator.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ConfirmationFrame extends SelectionFrame {

    private JButton buttonNO = new JButton("Cancel");

    protected ConfirmationFrame(String title, String message) {
        frame.setTitle(title);
        frame.setSize(600, 100);
        JPanel panel = new JPanel();

        JLabel jlabel1 = new JLabel(message);
        panel.add(jlabel1);

        frame.getRootPane().setDefaultButton(buttonOK);

        panel.add(buttonOK);
        panel.add(buttonNO);

        buttonNO.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == buttonNO) {
                    isChosen = true;
                    frame.setVisible(false);
                }
            }
        });

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}
