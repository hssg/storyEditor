package storygenerator.menu;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Svenja on 27.04.2016.
 */
public class InfoFrame extends SelectionFrame {
    public InfoFrame() {
        frame.setTitle("Help");
        frame.setPreferredSize(new Dimension(900, 300));
        final JPanel panel = new JPanel();
        String s = new String();
        s += "Create Stories on your own!\n\n";
        s += "You can add disks and walls to your simulation environment (left Panel), and create stories which combine " +
                "different behaviours of the disks.\n";
        s += "It is also possible to save and load your stories.\n\n";
        s += "Events:\t\tUse the editor in the middle to create events, i.e. to generate behaviour routines for the disks.\n";
        s += "Event Transition Trigger:\tUse the editor in the middle to create triggers which control the transitions between different events.\n";
        s += "States:\t\tUse the states table to the right to concatenate your events and triggers to behaviour chains.\n";
        JTextArea textArea = new JTextArea(s);
        textArea.setPreferredSize(new Dimension(850, 250));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);

        panel.add(textArea);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.pack();
        frame.setVisible(true);
    }

}
