package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 21.01.2016.
 */
public class BecameInvisible extends BecameVisible {
    private static final long serialVersionUID = 1L;

    public BecameInvisible() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        return !super.hasOccurred(disk, currentTime, momentOfLastTrigger, env);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered if the disk cannot see the other disk in its view angle.");
        frame.addValueChooser(30, 360, super.getAngleOfVisibility(), "angle of invisibility", "angleOfVisibility");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk that becomes invisible");
    }
}
