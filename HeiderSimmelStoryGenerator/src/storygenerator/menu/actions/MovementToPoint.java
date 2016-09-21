package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;

/**
 * Created by Svenja on 06.05.2016.
 */
public class MovementToPoint extends Event {
    private static final long serialVersionUID = 1L;
    private Double[] coord = new Double[]{50.0, 50.0};

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        return goToPoint(disk, coord[0] * environmentSize * 0.01, coord[1] * environmentSize * 0.01);
    }

    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk goes in straight line to the desired point.");
        frame.addCoordinateChooser(coord, "coord");
        frame.addValueChooser(0, 20, speed, "speed", "speed");
    }

    public void setCoord(Double[] coord) {
        this.coord = coord;
    }

    public Double[] getCoord() {
        return coord;
    }
}
