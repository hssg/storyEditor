package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 27.06.2016.
 */
public class HasStopped extends EventTransitionTrigger {
    private transient double[] oldPos = new double[2];
    private static final long serialVersionUID = 1L;
    private static final double eps = 0.00001;
    private transient int counter;

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered when the disk has stopped, e.g. when it reached its goal.");
        frame.addText("Please press ok");
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        double x = disk.getX();
        double y = disk.getY();
        boolean b = Math.abs(x - oldPos[0]) < eps && Math.abs(y - oldPos[1]) < eps;
        if (++counter % 5 == 0) {
            oldPos[0] = x;
            oldPos[1] = y;
        }
        return b;
    }

    @Override
    public void initialise() {
        super.initialise();
        oldPos = new double[2];
        counter = 0;
    }
}