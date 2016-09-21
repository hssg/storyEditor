package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.linalg2D.Point;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 10.02.2016.
 */
public class RotationAroundDisk extends Event2Disks {
    private static final long serialVersionUID = 1L;
    private double radius = 10;

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        Point M = new Point(super.getReferenceDisk().getX(), super.getReferenceDisk().getY());
        return this.rotateHelper(disk, env, radius, M);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk rotates around the other disk.");
        frame.addValueChooser(5, 30, radius, "radius of the rotation around the other disk", "radius");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk to rotate around");
        frame.addValueChooser(0, 20, speed, "speed of the rotation", "speed");
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
