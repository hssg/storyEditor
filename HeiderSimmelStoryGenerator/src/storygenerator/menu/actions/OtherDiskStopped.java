package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 21.01.2016.
 */
public class OtherDiskStopped extends EventTransitionTrigger2Disks {
    private static final long serialVersionUID = 1L;
    private double[] valueHistory = new double[2];
    private int counterOfCallsHelper = 0;

    public OtherDiskStopped() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        counterOfCallsHelper++;

        if (counterOfCallsHelper % 10 == 0) {

            double[] oldValues = new double[2];
            oldValues[0] = valueHistory[0];
            oldValues[1] = valueHistory[1];

            double[] currentValues = new double[2];

            Disk referenceDisk = this.getReferenceDisk();

            if (null == referenceDisk) {
                return false;
            }

            currentValues[0] = referenceDisk.getX();
            currentValues[1] = referenceDisk.getY();

            //to safe the new values
            valueHistory[0] = referenceDisk.getX();
            valueHistory[1] = referenceDisk.getY();

            final double EPS = 0.00001;
            return almostEqual(currentValues[0], oldValues[0], EPS) && almostEqual(currentValues[1], oldValues[1], EPS);
        } else {
            return false;
        }
    }

    private static boolean almostEqual(double a, double b, double eps) {
        return Math.abs(a - b) < eps;
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered when the other disk stops.");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk whose stopping shall be monitored");
    }


}
