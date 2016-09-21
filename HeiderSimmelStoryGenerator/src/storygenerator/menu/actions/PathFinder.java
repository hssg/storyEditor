package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.DiskComplex;
import diskworld.Environment;
import diskworld.environment.Wall;
import diskworld.grid.Cell;
import diskworld.grid.GridBasedCollisionDetector;
import diskworld.interfaces.CollisionDetector;
import org.math.plot.Plot3DPanel;
import org.math.plot.PlotPanel;
import storygenerator.menu.BehaviourEditorFrame;
import storygenerator.menu.DiskWorldStory;

import javax.swing.*;
import java.util.*;
import java.util.List;

/**
 * Created by Svenja on 25.05.2016.
 */
public class PathFinder extends MovementToPoint {
    private static final long serialVersionUID = 1L;
    private static final double BLOCKED = Double.NEGATIVE_INFINITY;
    private transient boolean computed;
    private transient double[][] potField;
    private transient boolean plot;
    private transient Disk diskToExclude = null; //only needed in e.g. Intelligent chasing so that the chased disk is not blocked.
    private transient int counter;

    public PathFinder() {
    }

    public PathFinder(Double[] coord, double speed, Disk diskToExclude) {
        this.setCoord(coord);
        setSpeed(speed);
        this.diskToExclude = diskToExclude;
        initialise();
    }

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        if (++counter % 10 == 0) {
            computed = false; //recompute every 20 time steps
        }
        if (!computed) {
            computePotentialField(disk, env);
            computed = true;
            if (plot) plotPotentialField();
        }

        int gridPosX = (int) convertEnvToPotFieldCoordinate(disk.getX());
        int gridPosY = (int) convertEnvToPotFieldCoordinate(disk.getY());

        //find the 'best' grid cell among the 8 neighboring cells
        double[] largestNeighbor = {gridPosX, gridPosY, potField[gridPosX][gridPosY]};
        int gridSizeX = potField.length;
        int gridSizeY = potField[0].length;
        for (int i = Math.max(0, gridPosX - 1); i <= Math.min(gridSizeX - 1, gridPosX + 1); i++) {
            for (int j = Math.max(0, gridPosY - 1); j <= Math.min(gridSizeY - 1, gridPosY + 1); j++) {
                if (potField[i][j] >= largestNeighbor[2]) {
                    largestNeighbor[0] = i;
                    largestNeighbor[1] = j;
                    largestNeighbor[2] = potField[i][j];
                }
            }
        }
        //compute vector pointing from the current grid cell to the best neighboring gridcell
        double[] v = {largestNeighbor[0] - gridPosX, largestNeighbor[1] - gridPosY};

        //compute new coordinates
        final double MAX_SPEED = 0.5;
        double newX = disk.getX() + v[0] * speed / 100 * MAX_SPEED;
        double newY = disk.getY() + v[1] * speed / 100 * MAX_SPEED;
        return new double[]{newX, newY, Math.atan2(newY - disk.getY(), newX - disk.getX())};
    }

    private void plotPotentialField() {
        Plot3DPanel panel = new Plot3DPanel();
        double[] xArr = new double[potField.length];
        double[] yArr = new double[potField.length];
        for (int i = 0; i < potField.length; i++) {
            xArr[i] = i;
            yArr[i] = i;
        }
        panel.addGridPlot("potential field", xArr, yArr, potField);
        panel.setLegendOrientation(PlotPanel.SOUTH);
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setAlwaysOnTop(false);
        frame.setSize(100, 100);
        frame.setVisible(true);
    }

    private void computePotentialField(Disk disk, Environment env) {
        int gridSizeX = env.getDiskGrid().getNumColumns();
        int gridSizeY = env.getDiskGrid().getNumRows();
        potField = new double[gridSizeX][];
        for (int i = 0; i < gridSizeX; i++) {
            potField[i] = new double[gridSizeY];
        }
        computeBlockedAreas(env, disk);

        int absTargX = (int) (getCoord()[0] / 100 * gridSizeX);
        int absTargY = (int) (getCoord()[1] / 100 * gridSizeY);
        LinkedList<int[]> queue = new LinkedList<>(); //queue to efficiently fill the potential field, holding the grid coordinates
        queue.add(new int[]{absTargX, absTargY}); //start with the goal
        final double START = 100;
        potField[absTargX][absTargY] = START;
        final double DECAY = 0.95;
        while (!queue.isEmpty()) {
            int[] current = queue.pop();
            double v = potField[current[0]][current[1]];
            double newValue = v * DECAY;
            for (int i = Math.max(0, current[0] - 1); i <= Math.min(gridSizeX - 1, current[0] + 1); i++) {
                for (int j = Math.max(0, current[1] - 1); j <= Math.min(gridSizeY - 1, current[1] + 1); j++) {  //eight surrounding neighbors
                    if (potField[i][j] >= 0 && potField[i][j] < newValue) {
                        potField[i][j] = newValue;
                        queue.add(new int[]{i, j});
                    }
                }
            }
        }
    }

    private void computeBlockedAreas(Environment env, Disk disk) {
        CollisionDetector detector = env.getCollisionDetector();
        Collection<List<Cell>> wallCells;
        int gridSize = potField.length;
        int safetyMargin = (int) (convertEnvToPotFieldCoordinate(disk.getRadius()) * 1.5);
        //mark wall cells and nearby cells as blocked
        if (detector instanceof GridBasedCollisionDetector) {
            wallCells = ((GridBasedCollisionDetector) detector).getWallCells();
            for (List<Cell> cells : wallCells) {
                for (Cell cell : cells) {
                    int[] index = cell.getIndex();
                    int x = index[0];
                    int y = index[1];
                    potField[index[0]][index[1]] = BLOCKED;
                    for (int i = Math.max(0, x - safetyMargin); i <= Math.min(gridSize - 1, x + safetyMargin); i++) {
                        for (int j = Math.max(0, y - safetyMargin); j <= Math.min(gridSize - 1, y + safetyMargin); j++) {
                            potField[i][j] = BLOCKED;
                        }
                    }
                }
            }
        }
        //look for locations of other disk's (assumes they're static...) and mark them as blocked
        Set<DiskComplex> diskComplexes = env.getDiskComplexes();
        for (DiskComplex diskComplex : diskComplexes) {
            List<Disk> disks = diskComplex.getDisks();
            for (Disk disk1 : disks) {
                if ((disk1 != disk && diskToExclude == null) || (disk1 != disk && diskToExclude != null && diskToExclude != disk1)) {
                    LinkedList<Cell> cells = env.getDiskGrid().getCellsIntersectingWithDisk(disk1);
                    for (Cell cell : cells) {
                        int[] index = cell.getIndex();
                        potField[index[0]][index[1]] = BLOCKED;
                    }
                }
            }
        }
    }

    private double convertEnvToPotFieldCoordinate(double d) {
        return d / (double) DiskWorldStory.SIZE_OF_ENVIRONMENT * (double) potField.length;
    }

    private double convertPotFieldToEnvCoordinate(double d) {
        return d / (double) potField.length * (double) DiskWorldStory.SIZE_OF_ENVIRONMENT;
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk intelligently finds its way to the target.");
        frame.addCoordinateChooser(getCoord(), "coord");
        frame.addValueChooser(0, 20, speed, "speed", "speed");
    }

    @Override
    public void initialise() {
        computed = false;
        counter = 0;
    }

    public void setDiskToExclude(Disk diskToExclude) {
        this.diskToExclude = diskToExclude;
    }

    @Override
    public void setCoord(Double[] coord) {
        super.setCoord(coord);
        this.computed = false;
    }
}
