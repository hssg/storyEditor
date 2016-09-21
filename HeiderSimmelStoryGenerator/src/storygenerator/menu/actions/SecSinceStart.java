package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 22.01.2016.
 */
public class SecSinceStart extends EventTransitionTrigger {
    private static final long serialVersionUID = 1L;
    double sec = 10;

    public SecSinceStart() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        return currentTime - sec > 0.0;
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered some seconds after the start of the simulation.");
        frame.addValueChooser(0, this.sec, "seconds since start", "sec");
    }

    public double getSec() {
        return sec;
    }

    public void setSec(double sec) {
        this.sec = sec;
    }
}
