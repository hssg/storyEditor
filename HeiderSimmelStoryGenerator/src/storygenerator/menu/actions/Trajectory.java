package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 10.02.2016.
 */
public class Trajectory extends Event {
    private static final long serialVersionUID = 1L;
    private transient int numNextPointOnTrajectory = 0;
    private Double[][] trajectory;
    private double continueTrajectoryDouble;
    private boolean continueTrajectory;

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        if (trajectory != null) {
            double targetx = trajectory[trajectory.length - 1][0] * environmentSize;
            double targety = trajectory[trajectory.length - 1][1] * environmentSize;

            if (numNextPointOnTrajectory < trajectory.length) {
                targetx = trajectory[numNextPointOnTrajectory][0] * environmentSize;
                targety = trajectory[numNextPointOnTrajectory][1] * environmentSize;
            }
            double positionReachedMargin = 0.005 * this.environmentSize;

            if ((Math.abs(disk.getX() - targetx) < positionReachedMargin) && (Math.abs(disk.getY() - targety) < positionReachedMargin)) {
                if ((numNextPointOnTrajectory + 1) < trajectory.length) {
                    numNextPointOnTrajectory++;
                    targetx = trajectory[numNextPointOnTrajectory][0] * environmentSize;
                    targety = trajectory[numNextPointOnTrajectory][1] * environmentSize;
                    return this.goToPoint(disk, targetx, targety);
                } else { //end of trajectory is reached.
                    if (continueTrajectory && trajectory.length > 1) {
                        numNextPointOnTrajectory = 0;
                        targetx = trajectory[numNextPointOnTrajectory][0] * environmentSize;
                        targety = trajectory[numNextPointOnTrajectory][1] * environmentSize;
                        return this.goToPoint(disk, targetx, targety);
                    } else {
                        // numNextPointOnTrajectory = trajectory.length;
                        return this.stop(disk);
                    }
                }
            } else {
                if (numNextPointOnTrajectory == trajectory.length)
                    numNextPointOnTrajectory = 0;
                return this.goToPoint(disk, targetx, targety);
            }
        } else return null;

    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("You can draw a trajectory for the disk. Remember to press 'Ready with painting'.");
        frame.addGUILineChooser(trajectory, "trajectory", "trajectory");
        int def = continueTrajectory ? 1 : 0;
        frame.addBooleanChooser(def, "disk continues trajectory from starting point after its finished", "continueTrajectoryDouble");
        frame.addValueChooser(0, 20, speed, "speed", "speed");
    }

    @Override
    public void initialise() {
        numNextPointOnTrajectory = 0;
    }

    public boolean isContinueTrajectory() {
        return continueTrajectory;
    }

    public void setContinueTrajectoryDouble(double trajectory) {
        continueTrajectoryDouble = trajectory;
        this.continueTrajectory = trajectory == 1.0 ? true : false;
    }

    public void setTrajectory(Double[][] trajectory) {
        this.trajectory = trajectory;
    }

    public Double[][] getTrajectory() {
        return trajectory;
    }

    public int getNumNextPointOnTrajectory() {
        return numNextPointOnTrajectory;
    }

    public double getContinueTrajectoryDouble() {
        return continueTrajectoryDouble;
    }

    public void setNumNextPointOnTrajectory(int numNextPointOnTrajectory) {
        this.numNextPointOnTrajectory = numNextPointOnTrajectory;
    }
}
