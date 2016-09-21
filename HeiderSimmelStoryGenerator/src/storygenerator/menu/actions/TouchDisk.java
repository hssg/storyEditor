package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 21.01.2016.
 */
public class TouchDisk extends EventTransitionTrigger2Disks {
    private static final long serialVersionUID = 1L;
    private static final double EPS = 0.05;

    public TouchDisk() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        double thisX = disk.getX();
        double thisY = disk.getY();
        double otherX = this.getReferenceDisk().getX();
        double otherY = this.getReferenceDisk().getY();

        double distance = Math.sqrt(Math.pow(otherX - thisX, 2)
                + Math.pow(otherY - thisY, 2)); //pythagoras: compute distance

        if (distance < (EPS + disk.getRadius() + this.getReferenceDisk().getRadius())) {
            //			System.out.println("collision! Other disk touched");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered when the other disk is touched.");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk that is touched");
    }


}
