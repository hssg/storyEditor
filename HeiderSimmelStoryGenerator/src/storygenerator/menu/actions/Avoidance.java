package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 10.02.2016.
 */
public class Avoidance extends Event2Disks {
    private static final long serialVersionUID = 1L;

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        return super.avoid(disk, env);
    }


    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk sidesteps when other disk is too close.");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "avoided disk");

    }

}
