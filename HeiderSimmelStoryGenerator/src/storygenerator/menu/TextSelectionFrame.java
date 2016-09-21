package storygenerator.menu;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 */
public class TextSelectionFrame extends SelectionFrame {

    private static final long serialVersionUID = 1L;
    protected String text;
    protected final JTextField tf1;

    /**
     * Invokes opening a frame which enables the user to enter a text
     *
     * @param myText    default text
     * @param title     title of the frame
     * @param labeltext description
     */
    public TextSelectionFrame(String myText, String title, String labeltext) {

        this.text = myText;

        frame.setTitle(title);
        frame.setSize(600, 100);
        JPanel panel = new JPanel();

        tf1 = new JTextField(myText, 15);

        tf1.setForeground(Color.BLUE);
        tf1.setBackground(Color.YELLOW);
        panel.add(tf1);

        JLabel jlabel1 = new JLabel(labeltext);
        panel.add(jlabel1);

        frame.getRootPane().setDefaultButton(buttonOK);

        panel.add(buttonOK);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}
