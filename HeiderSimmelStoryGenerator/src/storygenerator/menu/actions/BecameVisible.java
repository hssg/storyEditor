package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.environment.Wall;
import diskworld.linalg2D.Line;
import storygenerator.menu.BehaviourEditorFrame;

import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Svenja on 21.01.2016.
 */
public class BecameVisible extends EventTransitionTrigger2Disks {
    private static final long serialVersionUID = 1L;

    private double angleOfVisibility = 90;


    public BecameVisible() {
        super();
    }

    @Override
    public boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env) {
        Collection<Wall> walls = env.getCollisionDetector().getWalls();
        double angleToRefDisk = getAngleOfThisDiskAndReferenceDisk(disk);
        boolean isInFieldOfVisibility = (Math.abs(disk.getAngle() - angleToRefDisk) <= 0.5 * Math.toRadians(this.angleOfVisibility));

        if (!isInFieldOfVisibility) {
            return false;
        } else {
            if (getDistance(disk) < environmentSize * 0.3) {
                return false;
            }
            //check for walls
            if (null == walls || walls.size() == 0) {
                return true;
            } else {
                Line2D.Double lineFromThisDiskToOtherDisk = new Line2D.Double(disk.getX(), disk.getY(), this.getReferenceDisk().getX(), this.getReferenceDisk().getY());
                for (Wall wall : walls) {
                    Line2D.Double l2 = new Line2D.Double(wall.getX1() * environmentSize, wall.getY1() * environmentSize, wall.getX2() * environmentSize, wall.getY2() * environmentSize);
                    if (l2.intersectsLine(lineFromThisDiskToOtherDisk)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    private double getDistance(Disk disk) {
        return Math.sqrt(Math.pow(disk.getX() - getReferenceDisk().getX(), 2) + Math.pow(disk.getY() - getReferenceDisk().getY(), 2));
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Transition is triggered if the disk can see the other disk in its view angle, not too far away.");
        frame.addValueChooser(30, 360, angleOfVisibility, "angle of visibility", "angleOfVisibility");
        frame.addReferenceDiskChooser(getReferenceDiskName(), "disk that becomes visible");
    }


    public double getAngleOfVisibility() {
        return angleOfVisibility;
    }

    public void setAngleOfVisibility(double angleOfVisibility) {
        this.angleOfVisibility = angleOfVisibility;
    }
}
