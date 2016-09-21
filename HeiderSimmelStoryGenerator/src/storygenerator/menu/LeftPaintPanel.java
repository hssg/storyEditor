package storygenerator.menu;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import diskworld.linalg2D.Line;
import diskworld.linalg2D.Point;
import diskworld.extension.Utils;

public class LeftPaintPanel extends JPanel implements MouseListener,
        MouseMotionListener, KeyListener {

    private final Vector<MenuDisk> disks;

    public enum DrawItem {
        DISK, WALL, TRAJECTORY;


    }

    private DrawItem currentDrawItem = DrawItem.WALL;
    private LinkedList<Line> wallList = new LinkedList<Line>();

    private LinkedList<Point> pointList = new LinkedList<Point>();

    private Double[] coord;
    private A_MenuMain menu;
    private int startX = -1;
    private int startY = -1;

    private int tempX, tempY;
    private PopupMenu popupMenu;
    private boolean doPaintWhileMouseIsMoving = true;

    private Color currentColor = Color.ORANGE;
    private DiskAdder assignMenu = null;
    private static final Color WALL_COLOR = Color.WHITE;

    private static final Color TRAJECTORY_COLOR = Color.ORANGE;

    public LeftPaintPanel(A_MenuMain menu, Vector<MenuDisk> disks,
                          LinkedList<Line> wallList) {
        this.disks = disks;
        this.menu = menu;
        this.wallList = wallList;
        this.currentColor = DiskWorldStory.DEFAULT_DISK_COLOR;

        this.setBackground(Color.BLACK);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this); // ????
        this.setFocusable(true);
        this.requestFocusInWindow();

        this.setToolTipText("Click & drag cursor to create Disk or a wall.");

        final int CROSSHAIR_CURSOR = 1;
        Cursor cursor = new Cursor(CROSSHAIR_CURSOR);
        this.setCursor(cursor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (MenuDisk disk : disks) {
            int newXPos = (int) (disk.x * this.getWidth());
            int newYPos = this.getHeight() - (int) (disk.y * this.getHeight());
            int newRadius = transformRadiusToScreen(disk.radius);
            drawCircle((Graphics2D) g, newXPos, newYPos, disk.col, newRadius);
        }

        for (Line wall : wallList) {
            Line lineOnScreen = transformLineToScreen(wall);
            paintLine(g, lineOnScreen, WALL_COLOR);
        }

        if (coord != null) {
            drawCircle((Graphics2D) g, (int) (coord[0].intValue() / 100.0 * this.getWidth()), (int) (this.getHeight() - coord[1].intValue() / 100.0 * this.getHeight()), TRAJECTORY_COLOR, 5);
        }
        if (currentDrawItem == DrawItem.TRAJECTORY) {
            if (pointList.size() == 1) {
                drawCircle((Graphics2D) g, (int) (pointList.getFirst().x), (int) (pointList.getFirst().y), TRAJECTORY_COLOR, 4);
            } else {
                for (int i = 1; i < pointList.size(); i++) {
                    Line line = new Line(pointList.get(i - 1), pointList.get(i));
                    paintLine(g, line, TRAJECTORY_COLOR);
                }
            }
        }


        if (doPaintWhileMouseIsMoving) {
            if (startX != -1 && startY != -1) {
                switch (currentDrawItem) {
                    case DISK:
                        drawCircle((Graphics2D) g, startX, startY, currentColor, getCurrentRadiusOnScreen());
                        if (assignMenu != null)
                            assignMenu.setCircleData(transformXPositionToPercent(startX), transformYPositionToPercent(startY), getCurrentRadiusInPercent());
                        break;
                    case WALL:
                        paintCurrentWall(g);
                        break;
                    case TRAJECTORY:
                        break;
                }
            }
        }
    }

    protected Double[][] getTrajectory() {
        Double[][] tr = new Double[pointList.size()][2];
        for (int i = 0; i < pointList.size(); i++) {
            Point point = transformPointToPercent(pointList.get(i));
            tr[i][0] = point.x;
            tr[i][1] = point.y;
        }
        return tr;
    }

    protected void setCurrentDrawItemToTrajectory() {
        this.currentDrawItem = DrawItem.TRAJECTORY;
        updatePaintingAfterSettingAnItem();
    }

    protected void setCurrentDrawItemToTrajectory(Double[][] trajectory) {
        this.pointList = new LinkedList<Point>();
        for (int i = 0; i < trajectory.length; i++) {
            pointList.add(getPointOnScreen(new Point(trajectory[i][0], trajectory[i][1])));
        }
        this.currentDrawItem = DrawItem.TRAJECTORY;
        updatePaintingAfterSettingAnItem();
    }

    protected void setCurrentDrawItemToDefault() {
        this.currentDrawItem = DrawItem.WALL;
        startX = -1;
        startY = -1;
        this.pointList = new LinkedList<Point>();
        updatePaintingAfterSettingAnItem();
    }

    protected void setCurrentDrawItemToDisk() {
        this.currentDrawItem = DrawItem.DISK;
        startX = -1;
        startY = -1;
        this.pointList = new LinkedList<Point>();
        updatePaintingAfterSettingAnItem();
    }

    protected boolean isTrajectoryChosen() {
        return pointList.size() >= 1;
    }

    private void updatePaintingAfterSettingAnItem() {
        doPaintWhileMouseIsMoving = false;
        repaint();
    }

    public Double[] getCoord() {
        return coord;
    }

    public void setCoord(Double[] coord) {
        this.coord = coord;
    }

    private Line getCurrentLine() {
        return new Line(startX, startY, tempX, tempY);
    }

    private void paintCurrentWall(Graphics g) {
        Line currentWall = getCurrentLine();
        paintLine(g, currentWall, WALL_COLOR);
    }

    private void paintLine(Graphics g, Line line, Color col) {
        g.setColor(col);
        g.drawLine((int) line.getX1(), (int) line.getY1(),
                (int) line.getX2(), (int) line.getY2());
    }

    private static void drawCircle(Graphics2D g, int x, int y, Color color,
                                   int radius) {
        g.setColor(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    /***********************************************************************************
     * *********************	Transformations screen <-> data		********************
     ********************************************************************************/


    private int getCurrentRadiusOnScreen() {
        return distance(startX, startY, tempX, tempY);
    }

    private double getCurrentRadiusInPercent() {
        return transformRadiusToPercent(distance(startX, startY, tempX, tempY));
    }

    private Point getPointOnScreen(Point p) {
        double x = (int) (p.x * this.getWidth());
        double y = this.getHeight() - (int) (p.y * this.getHeight());
        return new Point(x, y);
    }

    private Line transformLineToScreen(Line line) {
        double x1 = line.getX1() * this.getWidth();
        double y1 = this.getHeight() - line.getY1() * this.getHeight();
        double x2 = line.getX2() * this.getWidth();
        double y2 = this.getHeight() - line.getY2() * this.getHeight();
        return new Line(x1, y1, x2, y2);
    }

    private int transformRadiusToScreen(double radiusInPercent) {
        int newRadius = (int) (radiusInPercent * (0.5 * (this.getWidth() + this.getHeight())));
        return newRadius;
    }

    private double transformRadiusToPercent(double radiusOnScreen) {
        double newRadius = (radiusOnScreen / (0.5 * (this.getWidth() + this.getHeight())));
        newRadius = Utils.round(newRadius, 2);
        return newRadius;
    }

    private Point transformPointToPercent(Point p) {
        double newX = transformXPositionToPercent(p.x);
        double newY = transformYPositionToPercent(p.y);
        return new Point(newX, newY);
    }

    private double transformXPositionToPercent(double xPosition) {
        //double newPosition =( xPosition / (0.5*(this.getWidth()+this.getHeight())));
        double newPosition = (xPosition / this.getWidth());
        newPosition = Utils.round(newPosition, 2);
        return newPosition;
    }

    private double transformYPositionToPercent(double yPosition) {
        //double panelsize = 0.5*(this.getWidth()+this.getHeight());
        //double newPosition = 1.0 - ( yPosition / panelsize);
        double newPosition = 1.0 - (yPosition / this.getHeight());
        newPosition = Utils.round(newPosition, 2);
        return newPosition;
    }

    private static int distance(int x1, int y1, int x2, int y2) {
        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);
        return (int) p1.distance(p2);
    }

    /***********************************************************************************
     * *************************	Mouse  & Key events		****************************
     ********************************************************************************/


    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1) {

            if (currentDrawItem == DrawItem.TRAJECTORY) {
                pointList.add(new Point(e.getX(), e.getY()));
            }

            doPaintWhileMouseIsMoving = true;
            repaint();
        } else if (e.getButton() == 2) {

            if (currentDrawItem != DrawItem.TRAJECTORY) {
                popupMenu = new storygenerator.menu.PopupMenu(this, e);

                popupMenu.colorItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == popupMenu.colorItem) {
                            currentColor = JColorChooser.showDialog(null, "Select Color", currentColor);
                        }

                    }
                });

                popupMenu.wallItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == popupMenu.wallItem) {
                            currentDrawItem = DrawItem.WALL;
                        }
                    }
                });

                popupMenu.diskItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == popupMenu.diskItem) {
                            currentDrawItem = DrawItem.DISK;
                        }
                    }
                });
            }

        } else if (e.getButton() == 3) {
            if (currentDrawItem == DrawItem.TRAJECTORY) {
                if (pointList.size() >= 1) {
                    pointList.removeLast();
                }
            }
            doPaintWhileMouseIsMoving = false;
            repaint();
        }

    }

    public void mouseEntered(MouseEvent e) {
        this.requestFocusInWindow();
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        if (currentDrawItem == DrawItem.WALL) {
            double newx1 = transformXPositionToPercent(startX);
            double newy1 = transformYPositionToPercent(startY);
            double newx2 = transformXPositionToPercent(tempX);
            double newy2 = transformYPositionToPercent(tempY);
            wallList.add(new Line(newx1, newy1, newx2, newy2));
            repaint();
        }
    }

    public void mouseDragged(MouseEvent e) {

        if (SwingUtilities.isLeftMouseButton(e)) {
            doPaintWhileMouseIsMoving = true;
            tempX = e.getX();
            tempY = e.getY();
            this.repaint();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            doPaintWhileMouseIsMoving = false;
            this.repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent paramKeyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (null != this.getMousePosition()) { //mouse is on the panel

            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                Toolkit.getDefaultToolkit().beep();

                switch (currentDrawItem) {
                    case DISK:
                        if (getCurrentRadiusInPercent() >= 0.01) {

/*						menu.addDiskAndNotifyMenu(assignMenu);
                        updatePaintingAfterSettingAnItem();
						assignMenu = null;
						currentDrawItem = DrawItem.WALL;
						startX = -1;
						startY = -1;*/
/*						String diskName = "DiskName"+Integer.toString(listAss.size()+1);
                        String title = "Choose disk name";
						String labeltext = "Please enter the desired disk name.";
						final TextSelectionFrame textMenu = new TextSelectionFrame(diskName, title, labeltext);

						textMenu.buttonOK.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								if(e.getSource()==textMenu.buttonOK) {

									DiskAdder d = new DiskAdder();
									d.diskName = textMenu.tf1.getText();
									if(d.isUsedDiskName(listAss)) {
										A_MenuMain.chooseDifferentName();
									} else {
										d.col = currentColor;
										d.x = transformXPositionToPercent(startX);
										d.y = transformYPositionToPercent(startY);
										d.radius = getCurrentRadiusInPercent();
										menu.addDiskAndNotifyMenu(d);
										textMenu.frame.setVisible(false);
										textMenu.frame.dispose();
										updatePaintingAfterSettingAnItem();
									}
								}
							}

						});*/
                        }
                        break;

                    case WALL:
/*					double newx1 = transformXPositionToPercent(startX);
                    double newy1 = transformYPositionToPercent(startY);
					double newx2 = transformXPositionToPercent(tempX);
					double newy2 = transformYPositionToPercent(tempY);
					wallList.add(new Line(newx1, newy1, newx2, newy2));*/

                        break;


                    case TRAJECTORY:
                        break;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void setAssignMenuDisk(DiskAdder d) {
        assignMenu = d;
    }

    public void setCurrentDrawItemToWall() {
        currentDrawItem = DrawItem.WALL;
        startX = -1;
        startY = -1;
    }

    public void deleteTrajectory() {
        this.pointList = new LinkedList<>();
    }

    public void clear() {
        this.coord = null;
        this.pointList = new LinkedList<>();
        this.setCurrentDrawItemToDefault();
        this.repaint();
    }
}


class PopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;

    protected JMenuItem diskItem, wallItem, colorItem;


    public PopupMenu(JPanel parentComp, MouseEvent event) {

        JMenu menu = new JMenu("Set different item");
        diskItem = new JMenuItem("Disk");
        menu.add(diskItem);
        wallItem = new JMenuItem("Wall");
        menu.add(wallItem);
        add(menu);

        addSeparator();

        colorItem = new JMenuItem("Change color");
        add(colorItem);

        show(event.getComponent(), event.getX(), event.getY());
    }
} 
