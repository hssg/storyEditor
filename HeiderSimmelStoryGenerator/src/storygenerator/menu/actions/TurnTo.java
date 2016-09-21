package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 10.02.2016.
 */
public class TurnTo extends Event2Disks {
    private static final long serialVersionUID = 1L;
    private double speed = 10;
    private final double TURN_IS_OVER_MARGIN = Math.toRadians(1);

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        double relSpeed = speed * 0.0001;
        double[] values = new double[4];
        double thisX = disk.getX();
        double thisY = disk.getY();
        double rx = super.getReferenceDisk().getX();
        double ry = super.getReferenceDisk().getY();

        double angle1 = convertTo2Pi(Math.atan2((ry - thisY), (rx - thisX)));

        double angle2 = disk.getOrientation();

        if (Math.abs(angle1 - angle2) < TURN_IS_OVER_MARGIN) {
            values[2] = disk.getAngle();
        } else {
            if (Math.abs(angle2 - angle1) <= Math.PI) {
                values[2] = convertToPMPi(angle2 + (angle2 > angle1 ? -relSpeed : relSpeed));
            } else {
                values[2] = convertToPMPi(angle2 + (angle2 > angle1 ? relSpeed : -relSpeed));
            }
        }
        values[0] = thisX;
        values[1] = thisY;
        return values;
    }

    private static double convertTo2Pi(double angle) {
        if (angle >= 0) return angle;
        else return 2 * Math.PI + angle;
    }

    private static double convertToPMPi(double angle) {
        if (angle <= Math.PI) {
            return angle;
        } else {
            return angle - 2 * Math.PI;
        }
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk stays at its place and always turns to the other disk.");
        frame.addValueChooser(10, 100, speed, "speed of the turn", "speed");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk to turn to");
    }

    @Override
    public void initialise() {

    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
