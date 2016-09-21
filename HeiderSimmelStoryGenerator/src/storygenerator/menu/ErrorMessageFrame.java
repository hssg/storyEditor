package storygenerator.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ErrorMessageFrame extends SelectionFrame {

    protected ErrorMessageFrame(String title, String errorMessage) {
        frame.setTitle(title);
        frame.setSize(600, 100);
        JPanel panel = new JPanel();

        JLabel jlabel1 = new JLabel(errorMessage);
        panel.add(jlabel1);

        frame.getRootPane().setDefaultButton(buttonOK);

        panel.add(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == buttonOK) {
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
