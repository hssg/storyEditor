package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.linalg2D.Point;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 10.02.2016.
 */
public class Rotation extends Event {
    private static final long serialVersionUID = 1L;
    private double radius = 10;
    private Double[] coord = new Double[]{10.0, 10.0};

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        //TODO might happen that rotation centre is too close to edges
        double relX = coord[0] * 0.01;
        double relY = coord[1] * 0.01;
        Point M = new Point(relX * environmentSize, relY * environmentSize);
        return this.rotateHelper(disk, env, radius, M);
    }


    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk rotates around a point.");
        frame.addValueChooser(0, 100, radius, "radius of the rotation", "radius");
        frame.addCoordinateChooser(coord, "coord");
        frame.addValueChooser(0, 20, speed, "speed of the rotation", "speed");
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setCoord(Double[] coord) {
        this.coord = coord;
    }
}
