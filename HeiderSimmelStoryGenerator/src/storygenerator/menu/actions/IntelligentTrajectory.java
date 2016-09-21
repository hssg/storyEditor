package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;

/**
 * Created by Svenja on 04.07.2016.
 */
public class IntelligentTrajectory extends Trajectory {
    private transient PathFinder pathFinder;
    private transient double[] newPos = new double[2];

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        //TODO produces a somehow wiggled trajectory around painted trajectory
        double[] values = pathFinder.getTimeStepValues(disk, env);
        newPos[0] = values[0];
        newPos[1] = values[1];
        if (doesNotMove(disk)) {
            if (isContinueTrajectory() && getNumNextPointOnTrajectory() + 1 == getTrajectory().length) {
                setNumNextPointOnTrajectory(-1);
            }
            int p = getNumNextPointOnTrajectory();
            if (p + 1 < getTrajectory().length) {
                setNumNextPointOnTrajectory(p + 1);
                this.pathFinder.setCoord(new Double[]{getTrajectory()[p + 1][0] * 100, getTrajectory()[p + 1][1] * 100});
                values = pathFinder.getTimeStepValues(disk, env);
            } else return this.stop(disk);
        }
        return values;
    }

    private boolean doesNotMove(Disk disk) {
        double eps = 0.00001;
        return Math.abs(newPos[0] - disk.getX()) < eps && Math.abs(newPos[1] - disk.getY()) < eps;
    }

    @Override
    public void initialise() {
        super.initialise();
        newPos = new double[2];
        pathFinder = new PathFinder();
        if (getTrajectory() != null)
            pathFinder.setCoord(new Double[]{getTrajectory()[0][0] * 100, getTrajectory()[0][1] * 100});
        pathFinder.setSpeed(speed);
        pathFinder.initialise();
    }
}
