package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 10.02.2016.
 */
public class Stopping extends Event {
    private static final long serialVersionUID = 1L;

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        return super.stop(disk);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk stays at its place.");
        frame.addText("Please press ok.");
    }

}
