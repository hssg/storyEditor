package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 21.01.2016.
 */
public class IsClose extends EventTransitionTrigger2Disks {
    private static final long serialVersionUID = 1L;
    private double proximity = 10;

    public IsClose() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        // percentage of proximity : if less than, e.g. 5% of environenment, consider
        // it as being close to other disk.
        double relativeProximity = proximity * 0.01;
        double thisX = disk.getX();
        double thisY = disk.getY();
        double otherX = this.getReferenceDisk().getX();
        double otherY = this.getReferenceDisk().getY();

        double distanceBetweenCenterPoints = Math.sqrt(Math.pow(otherX - thisX, 2)
                + Math.pow(otherY - thisY, 2)); //pythagoras: compute distance
        double distanceBetweenDisks = distanceBetweenCenterPoints - disk.getRadius() - this.getReferenceDisk().getRadius();

        return (distanceBetweenDisks < environmentSize * relativeProximity);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered when the other disk is close.");
        frame.addValueChooser(0, 100, proximity, "proximity", "proximity");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk which closeness shall be monitored");
    }

    public double getProximity() {
        return proximity;
    }

    public void setProximity(double proximity) {
        this.proximity = proximity;
    }
}
