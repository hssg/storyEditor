package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 15.06.2016.
 */
public class IsNotClose extends IsClose {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        return !super.hasOccurred(disk, currentTime, momentOfLastTrigger, env);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered when the other disk is far, e.g. further away than the proximity value.");
        frame.addValueChooser(0, 100, getProximity(), "proximity", "proximity");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk which closeness shall be monitored");
    }
}
