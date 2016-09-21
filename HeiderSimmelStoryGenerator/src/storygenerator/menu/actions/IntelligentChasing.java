package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 20.06.2016.
 */
public class IntelligentChasing extends Event2Disks {
    private static final long serialVersionUID = 1L;
    private transient PathFinder pathFinder;
    private transient int counter = 0;
    private double dist = 10;

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        //update position of other disk every 10 steps
        if (++counter % 5 == 0) {
            pathFinder.setCoord(new Double[]{getReferenceDisk().getX() / environmentSize * 100, getReferenceDisk().getY() / environmentSize * 100});
        }
        if (distNotTooSmall(disk)) return pathFinder.getTimeStepValues(disk, env);
        //return new double[]{disk.getX(), disk.getY(), Math.atan2(disk.getY(), disk.getAngle())};

        //sidestep when other disk is too close (e.g. comes toward the disk) -> better solution??
        //TODO does not yet take speed into account
        return avoid(disk, env);
    }

    private boolean distNotTooSmall(Disk disk) {
        return Math.sqrt(Math.pow(disk.getX() - getReferenceDisk().getX(), 2) + Math.pow(disk.getY() - getReferenceDisk().getY(), 2)) > (dist / 100) * environmentSize;
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Intelligent Chase of another disk.");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk to be chased");
        frame.addValueChooser(0, 20, speed, "speed", "speed");
        frame.addValueChooser(0, 30, dist, "distance", "dist");
    }

    @Override
    public void initialise() {
        pathFinder = new PathFinder(new Double[]{getReferenceDisk().getX() / environmentSize * 100, getReferenceDisk().getY() / environmentSize * 100}, speed, getReferenceDisk());
        counter = 0;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }
}
