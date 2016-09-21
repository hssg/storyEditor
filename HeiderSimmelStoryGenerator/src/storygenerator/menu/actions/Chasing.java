package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 10.02.2016.
 */
public class Chasing extends Event2Disks {
    private static final long serialVersionUID = 1L;
    private double distance = 10;

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        double[] values = new double[3];
        double newx;
        double newy;

        double thisx = disk.getX();
        double thisy = disk.getY();
        double thisRadius = disk.getRadius();

        double targetx = super.getReferenceDisk().getX();
        double targety = super.getReferenceDisk().getY();
        double otherRadius = super.getReferenceDisk().getRadius();

        final double MAXIMUM_CHASING_DISTANCE_IN_PERCENT = 0.5;
        double relDistance = distance * 0.01;
        double safetyDistance = MAXIMUM_CHASING_DISTANCE_IN_PERCENT * this.environmentSize * relDistance;

        double distance = Math.sqrt(Math.pow(targetx - thisx, 2) + Math.pow(targety - thisy, 2)); //pythagoras: compute distance

        double relSpeed = speed / 100;
        double necessaryTimeSteps = distance / relSpeed;
        double newangle;

        if (distance > thisRadius + otherRadius + safetyDistance) {
            newx = thisx + (targetx - thisx) / necessaryTimeSteps;
            newy = thisy + (targety - thisy) / necessaryTimeSteps;
            newangle = Math.atan2((targety - thisy), (targetx - thisx));
        } else if (distance < (thisRadius + otherRadius) * 2) {
            //sidestep when other disk comes too close...
            return avoid(disk, env);
        } else {
            newx = thisx;
            newy = thisy;
            newangle = disk.getAngle();
        }

        values[0] = newx;
        values[1] = newy;
        values[2] = newangle;

        return values;
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Chasing another disk.");
        frame.addValueChooser(0, 100, distance, "distance to the chased disk", "distance");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk to chase");
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
