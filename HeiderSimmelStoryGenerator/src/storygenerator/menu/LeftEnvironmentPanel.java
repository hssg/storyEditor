package storygenerator.menu;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import diskworld.visualization.EnvironmentPanel;
import diskworld.visualization.VisualizationSettings;

public class LeftEnvironmentPanel extends EnvironmentPanel implements MouseListener, MouseMotionListener {

    private int startX, startY;
    private int tempX, tempY;
    private Image img = null;

    public LeftEnvironmentPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaintMode();
        g.drawImage(img, 0, 0, this);
        g2.setColor(Color.RED);
        g2.drawLine(startX, startY, tempX, tempY);
    }

    public void zeichne() {
        if (img == null) {
            img = createImage(this.getWidth(), this.getHeight());
        }
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        this.paint(g2d);
        g2d.dispose();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        zeichne();
    }

    public void mouseDragged(MouseEvent e) {
        tempX = e.getX();
        tempY = e.getY();
        this.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }
}
