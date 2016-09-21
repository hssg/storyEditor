package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 22.01.2016.
 */
public class PeriodOfTime extends EventTransitionTrigger {
    private static final long serialVersionUID = 1L;
    double time = 10;

    public PeriodOfTime() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        double targetTimePeriod = time;  //here: targetTime means targetTimePeriod!
        //		System.out.println("lastEvent: "+this.preciseMomentOfLastEvent+" curr: "+currentTime+" target: "+targetTimePeriod);
        double diffCurrentToLast = Math.abs(currentTime - momentOfLastTrigger);
        return (Math.abs(diffCurrentToLast - targetTimePeriod) < 0.05);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered after a certain period of time after the disk's last transition.");
        frame.addValueChooser(0, this.time, "period of time", "time");
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
