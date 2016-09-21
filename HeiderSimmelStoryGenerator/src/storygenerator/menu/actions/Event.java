package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.linalg2D.Point;
import storygenerator.menu.BehaviourEditorFrame;
import storygenerator.menu.DiskWorldStory;

import java.io.Serializable;

/**
 * Abstract class which specifies a sort of behaviour.
 * To add an event, just create a class extending this one.
 * Caution: Also inform the {@link storygenerator.menu.Controller} about the new class by adding it in the contructor.
 *
 * @author Svenja
 */
public abstract class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    protected double environmentSize = DiskWorldStory.SIZE_OF_ENVIRONMENT;
    protected final double DEFAULT_SPEED = 0.01;
    protected double speed = DEFAULT_SPEED * 100.0;
    protected transient PathFinder pathFinder;


    public Event() {
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Calculates the new position and angle of a disk according to the desired behaviour.
     *
     * @param disk
     * @param env
     * @return a double array with the absolute x position (in environment coordinates - between 0 and DiskWorldStory.SIZE_OF_ENVIRONMENT,
     * the absolute y position (in environment coordinates),
     * and the angle of the disk (between -pi and +pi) in the new time step.
     * NOTE: better set the angle to atan2 of x and y coordinate.
     * Movement in other than head direction does not work yet.
     * A value other than atan2 only leads to an undistorted movemement, if the disk does not move at all, just turns.
     */
    public abstract double[] getTimeStepValues(Disk disk, Environment env);

    /**
     * Method to customize a {@link BehaviourEditorFrame} according to the event's fields.
     * The frame shall be edited such, that all relevant fields of the event are editable in the frame.
     * This can be done by calling the add... methods of the frame:
     * {@link BehaviourEditorFrame#addText(String)}, just for text,
     * {@link BehaviourEditorFrame#addValueChooser(double, double, double, String, String)}, for doubles with min/max value,
     * {@link BehaviourEditorFrame#addValueChooser(double, double, String, String)}, for doubles with only min value,
     * {@link BehaviourEditorFrame#addBooleanChooser(double, String, String)}, for booleans (in double form ->
     * make a double field and a boolean field; call this method; the double field will be set to 1 or 0,
     * change your boolean accordingly in the setter of the double.),
     * {@link BehaviourEditorFrame#addReferenceDiskChooser(String, String)}, for reference Disks.
     * {@link BehaviourEditorFrame#addGUILineChooser(Double[][], String, String)}, if the user shall be able to draw a line
     * (multiple 2d points, can be null in the beginning)  e.g. for a trajectory,
     * {@Link BehaviourEditorFrame#addCoordinateChooser}, if the user shall be able to choose a coordinate. The corresponding location
     * is visualized in the environment screen.
     * <p>
     * !!! Be careful: for all add-methods except for addText, you have to pass the (original) name of the field!
     * !!! Also be sure to provide setters for the fields you want to be editable.
     *
     * @param frame Menuframe to edit
     */
    public abstract void editMenuFrame(BehaviourEditorFrame frame);

    /**
     * Method to reset fields before the start of a simulation. Is called by the controller.
     * Overwrite this method if you have fields to reset or computing to be done before the start of a simulation.
     */
    public void initialise() {
        pathFinder = null;
    }

    protected double[] goToPoint(Disk disk, Point point) {
        return goToPoint(disk, point.x, point.y);
    }

    protected double[] goToPoint(Disk disk, double targetx, double targety) {
        final double MARGIN = 0.005;
        double[] values = new double[3];
        double newx;
        double newy;

        double thisx = disk.getX();
        double thisy = disk.getY();

        double distance = Math.sqrt(Math.pow(targetx - thisx, 2) + Math.pow(targety - thisy, 2)); //pythagoras: compute distance
        double relSpeed = speed / 100;
        double necessaryTimeSteps = distance / relSpeed;

        double x = thisx + (targetx - thisx) / necessaryTimeSteps;
        newx = Math.abs(thisx - targetx) > MARGIN ? x : thisx;
        double y = thisy + (targety - thisy) / necessaryTimeSteps;
        newy = Math.abs(thisy - targety) > MARGIN ? y : thisy;

        double newangle = getNewAngle(disk, targetx, targety);

        values[0] = newx;
        values[1] = newy;
        values[2] = newangle;
        return values;
    }

    private double getNewAngle(Disk disk, double targetx, double targety) {
        double thisx = disk.getX();
        double thisy = disk.getY();
        double newangle;
        final double MARGIN = 0.005;

        if (Math.abs(targetx - thisx) < MARGIN && Math.abs(targety - thisy) < MARGIN) {
            newangle = disk.getAngle();
        } else {
            newangle = Math.atan2((targety - thisy), (targetx - thisx));
        }
        return newangle;
    }

    protected double[] rotateHelper(Disk disk, Environment env, double radius, Point CenterOfRotation) {
        double relRadius = radius * 0.01;
        Point me = new Point(disk.getX(), disk.getY());
        double radiusOfRotation = relRadius * environmentSize;

        double margin = 0.01 * environmentSize;

        if ((me.distance(CenterOfRotation) - radiusOfRotation) > margin) { //outside -> go in direction of the center of circle (intelligently)
            //						System.out.println("outside");
            if (pathFinder == null) {
                Double[] coord = {Double.valueOf(CenterOfRotation.x) / environmentSize * 100, Double.valueOf(CenterOfRotation.y) / environmentSize * 100};
                if (this instanceof RotationAroundDisk) {
                    pathFinder = new PathFinder(coord, speed, ((RotationAroundDisk) this).getReferenceDisk());
                } else pathFinder = new PathFinder(coord, speed, null);
            }
            return pathFinder.getTimeStepValues(disk, env);
            //return this.goToPoint(disk, CenterOfRotation);
        }

        if (me.distance(CenterOfRotation) - radiusOfRotation < (-1) * margin) { //inside -> go in direction of the outside
            //			System.out.println("inside");

            if (me.x == CenterOfRotation.x && me.y == CenterOfRotation.y) { // if in the middle -> go righthand to the outside
                return this.goToPoint(disk, CenterOfRotation.x + 1.0, CenterOfRotation.y);
            } else { // inside the circle -> go on a straight line to the outside
                double vectorX = me.x - CenterOfRotation.x;
                double vectorY = me.y - CenterOfRotation.y;
                Point pointOut = new Point(me.x + vectorX, me.y + vectorY);
                return this.goToPoint(disk, pointOut);
            }
        }

        //		 perfect: super.getDisk() has the right distance to the center of rotation.
        //		System.out.println("rotate");
        double relSpeed = speed / 100;
        double speedOfRotation = .5 * relSpeed / radiusOfRotation;
        double t = (Math.atan2((me.y - CenterOfRotation.y), (me.x - CenterOfRotation.x))) + speedOfRotation;
        double x = (CenterOfRotation.x + radiusOfRotation * Math.cos(t));
        double y = (CenterOfRotation.y + radiusOfRotation * Math.sin(t));
        double[] values = new double[3];
        values[0] = x;
        values[1] = y;

        double oppositeSideLength = (me.y - CenterOfRotation.y);
        double adjacentSideLength = (me.x - CenterOfRotation.x);

        double alpha = Math.atan2(oppositeSideLength, adjacentSideLength);

        if (Math.PI / 2 < alpha && alpha < Math.PI) {
            values[2] = -3 * Math.PI / 2 + alpha;
        } else {
            values[2] = alpha + Math.toRadians(90);
        }
        double dx = x - disk.getX();
        double dy = y - disk.getY();
        values[2] = Math.atan2(dy, dx);

        return values;
    }

    protected double[] stop(Disk disk) {
        double[] values = new double[3];
        values[0] = disk.getX();
        values[1] = disk.getY();
        values[2] = disk.getAngle();
        return values;
    }
}
