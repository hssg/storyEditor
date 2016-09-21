package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;
import storygenerator.menu.DiskWorldStory;

/**
 * Created by Svenja on 11.05.2016.
 */
public class MirroredMovement extends Event2Disks {
    private static final long serialVersionUID = 1L;
    private transient int counter;
    private transient double newX;
    private transient double newY;

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        if (counter++ % 20 == 0) {
            double x = getReferenceDisk().getX();
            newX = x + 2 * ((double) DiskWorldStory.SIZE_OF_ENVIRONMENT / 2.0 - x);
            double y = getReferenceDisk().getY();
            newY = y + 2 * (DiskWorldStory.SIZE_OF_ENVIRONMENT / 2.0 - y);
        }
        return goToPoint(disk, newX, newY);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Mirrors movement of the other disk (mirrored on center point)");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk to mirror");
    }

    @Override
    public void initialise() {
        counter = 0;
    }
}
