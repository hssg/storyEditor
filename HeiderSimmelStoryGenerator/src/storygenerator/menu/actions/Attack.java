package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.linalg2D.Point;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 11.05.2016.
 */
public class Attack extends Event2Disks {
    private static final long serialVersionUID = 1L;
    private transient boolean pickMode;
    private transient double[] oldPos;
    private transient double[] goal;
    private transient int counter;
    private transient boolean stopMode;

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        Point me = new Point(disk.getX(), disk.getY());
        Point enemy = new Point(getReferenceDisk().getX(), getReferenceDisk().getY());
        if (stopMode) {
            if (++counter > 50) stopMode = false;
            return new double[]{disk.getX(), disk.getY(), disk.getAngle()};
        }
        if (pickMode) {
            if (++counter > 100) {
                counter = 0;
                pickMode = false;
                stopMode = true;
            }
            if (counter < 50) {
                //forward picking movement if enemy is not too close
                if (me.distance(enemy) < (disk.getRadius() + getReferenceDisk().getRadius()) * 2) {
                    return avoid(disk, env);
                }
                return goToPoint(disk, goal[0], goal[1]);
            } else {
                //backward movement
                return goToPoint(disk, oldPos[0], oldPos[1]);
            }
        }
        if (me.distance(enemy) < environmentSize * 0.2) {
            //start pick mode if enemy is close
            pickMode = true;
            oldPos = new double[]{me.x, me.y};
            goal = new double[]{enemy.x, enemy.y};
            counter = 0;
            return goToPoint(disk, goal[0], goal[1]);
        }
        //else do nothing
        return new double[]{disk.getX(), disk.getY(), disk.getAngle()};
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Makes picking movement to other disk if it comes too close.");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk that shall be attacked");
    }

    @Override
    public void initialise() {
        stopMode = false;
        pickMode = false;
        counter = 0;
    }
}
