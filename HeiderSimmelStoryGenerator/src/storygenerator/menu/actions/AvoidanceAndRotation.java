package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.linalg2D.Point;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 10.02.2016.
 */
public class AvoidanceAndRotation extends Event2Disks {
    private static final long serialVersionUID = 1L;
    private double radius = 10;
    private Double[] coord = new Double[]{10.0, 10.0};

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        Point Center = new Point(environmentSize / 2, environmentSize / 2);
        Point me = new Point(disk.getX(), disk.getY());
        Point enemy = new Point(getReferenceDisk().getX(), getReferenceDisk().getY());
        double radius = 5;

        if (me.distance(enemy) > environmentSize / 3) {
            double relX = coord[0] * 0.01;
            double relY = coord[1] * 0.01;
            Point M = new Point(relX * environmentSize, relY * environmentSize);
            return rotateHelper(disk, env, radius, M);
        } else {
            return avoid(disk, env);
        }
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk rotates around a point but flees when avoided disk is too close.");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "avoid and rotate");
        frame.addCoordinateChooser(coord, "coord");
        frame.addValueChooser(0, 100, radius, "radius of the rotation", "radius");
        frame.addValueChooser(0, 20, speed, "speed of the rotation", "speed");
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setCoord(Double[] coord) {
        this.coord = coord;
    }
}
