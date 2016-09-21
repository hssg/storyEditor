package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

public class SameXPosition extends EventTransitionTrigger2Disks {
    private static final long serialVersionUID = 1L;

    private static final double EPS = 0.05;

    public SameXPosition() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        double referenceHeight = this.getReferenceDisk().getX();
        double thisHeight = disk.getX();
        return Math.abs(referenceHeight - thisHeight) < EPS;
    }


    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered when both disks have the same x position.");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk with the same x position");

    }


}
