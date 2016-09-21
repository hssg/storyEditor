package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 21.01.2016.
 */
public class SameYPosition extends EventTransitionTrigger2Disks {
    private static final long serialVersionUID = 1L;

    private static final double EPS = 0.05;

    public SameYPosition() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        double referenceHeight = this.getReferenceDisk().getY();
        double thisHeight = disk.getY();
        return (Math.abs(referenceHeight - thisHeight) < EPS);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered when both disks have the same y position.");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk with the same y position");
    }


}
