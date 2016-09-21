package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 21.01.2016.
 */
public class Always extends EventTransitionTrigger {
    private static final long serialVersionUID = 1L;

    public Always() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        return false;
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Always never triggers an event transition; the disk never changes its event.");
    }

}
