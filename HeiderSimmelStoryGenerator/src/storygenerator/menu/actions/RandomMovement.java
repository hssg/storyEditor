package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.linalg2D.Point;
import storygenerator.menu.BehaviourEditorFrame;

import java.util.Random;

/**
 * Created by Svenja on 10.02.2016.
 */
public class RandomMovement extends Event {
    private static final long serialVersionUID = 1L;
    private double x = -1;
    private double y = -1;
    private Random random = new Random(34980923L);

    @Override
    public double[] getTimeStepValues(Disk disk, Environment env) {
        double[] values = this.pathFinder.getTimeStepValues(disk, env);
        x = values[0];
        y = values[1];
        if (doesNotMove(disk)) {
            computeRandomTarget(disk);
            pathFinder.setCoord(new Double[]{x / environmentSize * 100, y / environmentSize * 100});
        }
        return values;

    }

    private boolean doesNotMove(Disk disk) {
        double eps = 0.00001;
        return Math.abs(x - disk.getX()) < eps && Math.abs(y - disk.getY()) < eps;
    }

    private void computeRandomTarget(Disk disk) {
        double add = 0.0;
        double radius = disk.getRadius();
        double x, y;
        do {
            x = random.nextDouble() * environmentSize;
            y = random.nextDouble() * environmentSize;
        }
        while (x < radius + add || y < radius + add || x + add > environmentSize - radius || y + add > environmentSize - radius);

        this.x = x;
        this.y = y;
    }


    @Override
    public void editMenuFrame(BehaviourEditorFrame frame) {
        frame.addText("Disk randomly moves around the environment.");
        frame.addValueChooser(0, 5, speed, "speed of the random movement", "speed");
    }

    @Override
    public void initialise() {
        x = random.nextDouble() * environmentSize;
        y = random.nextDouble() * environmentSize;
        pathFinder = new PathFinder(new Double[]{x / environmentSize * 100, y / environmentSize * 100}, speed, null);
    }
}
