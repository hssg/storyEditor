package storygenerator.menu;

import java.awt.Cursor;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    protected JMenuItem menuNewStory = new JMenuItem(" New story ");
    protected JMenuItem menuLoadStory = new JMenuItem(" Load a story ");
    protected JMenuItem menuSaveStory = new JMenuItem(" Save this story ");
    protected JMenuItem menuEditStory = new JMenuItem(" Edit Story ");
    protected JMenuItem menuHelp = new JMenuItem(" Help ");

    private boolean isRunning = false;

    MenuBar() {
        JMenu menu = new JMenu("Menu");
        menu.add(menuNewStory);
        menu.add(menuLoadStory);
        menu.add(menuSaveStory);
        menu.add(menuEditStory);
        menu.add(menuHelp);
        this.add(menu);
        final int HAND_CURSOR = 12;
        Cursor cursor = new Cursor(HAND_CURSOR);
        this.setCursor(cursor);
    }
}
