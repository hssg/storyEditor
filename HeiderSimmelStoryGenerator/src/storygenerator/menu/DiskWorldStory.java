package storygenerator.menu;

import diskworld.*;
import diskworld.actuators.Mover;
import diskworld.actuators.Teleporter;
import diskworld.environment.AgentMapping;
import diskworld.environment.FloorCellType;
import diskworld.environment.Wall;
import diskworld.interfaces.CollidableObject;
import diskworld.interfaces.CollisionEventHandler;
import diskworld.interfaces.Sensor;
import diskworld.linalg2D.Line;
import diskworld.linalg2D.Point;
import diskworld.sensors.WallSensor;
import diskworld.visualization.PolygonDiskSymbol;
import diskworld.visualization.VisualizationOption;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

/**
 * @author Svenja
 */
public class DiskWorldStory {
    private Vector<MenuDisk> menuDisks;
    private LinkedList<Line> wallList;
    private String storyName;
    private boolean showVisualizationOfDiskDirection;
    private boolean randomized;
    private Environment env;
    private HashMap<String, DiskComplex> diskComplexes = new HashMap<>(10);
    private HashMap<String, AgentMapping> mappings = new HashMap<>(10);
    private HashMap<String, Disk> disks = new HashMap<>(10);
    public static final int SIZE_OF_ENVIRONMENT = 10;
    public static final Color DEFAULT_DISK_COLOR = Color.ORANGE;
    public static final int MOVER_MAX_VALUE = 10;
    private Random random;

    private HashMap<String, Boolean> collision = new HashMap<>(10);
    private boolean usingMover = true;


    public DiskWorldStory(Vector<MenuDisk> menuDisks, LinkedList<Line> wallList, String storyName, boolean showVisualizationOfDiskDirection, boolean randomized, long l) {
        this.menuDisks = menuDisks;
        this.wallList = wallList;
        this.storyName = storyName;
        this.showVisualizationOfDiskDirection = showVisualizationOfDiskDirection;
        this.randomized = randomized;
        this.random = new Random(l);
    }

    public Environment getEnvironment() {
        int sizeOfEnvironment = SIZE_OF_ENVIRONMENT;
        env = new Environment(sizeOfEnvironment, sizeOfEnvironment, 1, getWalls(sizeOfEnvironment), 0.1, 0.1, true);
        env.getFloor().fill(FloorCellType.EMPTY);

        //Disk symbols
        PolygonDiskSymbol diskSymbolTrianlge = PolygonDiskSymbol.getSharpTriangleSymbol(0.7D);
        Teleporter teleporter = new Teleporter(env, false, 0.5, 0.0, 0.0, 1.0);
        Mover mover = new Mover(-MOVER_MAX_VALUE, MOVER_MAX_VALUE, MOVER_MAX_VALUE, 0, 0.0);
        //   ImpulsDrive impulsDrive = new ImpulsDrive(1, 0.3);
        teleporter.setDiskSymbol(diskSymbolTrianlge);
        DiskType agentDrive;
        final int NUM_AGENTS = menuDisks.size();
        Disk[] diskArray = new Disk[NUM_AGENTS];
        int diskCounter1 = 0;
        for (MenuDisk menuDisk : menuDisks) {
            Sensor[] sensors = {new WallSensor(env, 0, 0.7 * Math.PI, 0, menuDisk.radius * sizeOfEnvironment + 0.5, "cone Sensor")};
            if (usingMover) agentDrive = new DiskType(DiskMaterial.METAL.withColor(menuDisk.col), mover);
            else agentDrive = new DiskType(DiskMaterial.METAL.withColor(menuDisk.col), teleporter);
            Disk disk;
            if (!randomized)
                disk = env.newRootDisk(menuDisk.x * sizeOfEnvironment, menuDisk.y * sizeOfEnvironment, menuDisk.radius * sizeOfEnvironment, agentDrive);
            else
                disk = getRandomizedDisk(env, agentDrive);
            disk.addEventHandler(new CollisionEventHandler() {
                @Override
                public void collision(CollidableObject collidableObject, Point collisionPoint, double exchangedImpulse) {
                    collision.put(menuDisk.diskName, true);
                }
            });
            diskArray[diskCounter1] = disk;
            diskComplexes.put(menuDisk.diskName, disk.getDiskComplex());
            disks.put(menuDisk.diskName, disk);
            diskCounter1++;
        }
        return env;
    }

    private Disk getRandomizedDisk(Environment env, DiskType type) {
        //TODO should take care of walls and other disks
        final double minRadius = 0.1;
        final double maxRadius = 0.6;
        double radius = minRadius + (maxRadius - minRadius) * random.nextDouble();
        final double minPos = 3 * radius;
        final double maxPos = SIZE_OF_ENVIRONMENT - 3 * radius;
        double x = minPos + (maxPos - minPos) * random.nextDouble();
        double y = minPos + (maxPos - minPos) * random.nextDouble();
        Disk disk = env.newRootDisk(x, y, radius, type);
        return disk;
    }

    private LinkedList<Wall> getWalls(double screenSize) {
        LinkedList<Wall> walls = new LinkedList<Wall>();
        for (Line line : wallList) {
            Line lineOnScreen = new Line(line.getX1() * screenSize, line.getY1() * screenSize,
                    line.getX2() * screenSize, line.getY2() * screenSize);
            walls.add(new Wall(lineOnScreen, 0.01 * screenSize));
        }

        //add bounding walls
        int SIZE = DiskWorldStory.SIZE_OF_ENVIRONMENT;
        walls.add(new Wall(new Line(new Point(0, 0), new Point(SIZE, 0)), 0.01));
        walls.add(new Wall(new Line(new Point(SIZE, 0), new Point(SIZE, SIZE)), 0.01));
        walls.add(new Wall(new Line(new Point(SIZE, SIZE), new Point(0, SIZE)), 0.01));
        walls.add(new Wall(new Line(new Point(0, SIZE), new Point(0, 0)), 0.01));

        return walls;
    }

    public HashMap<String, AgentMapping> getAgentMappings() {
        for (String s : diskComplexes.keySet()) {
            DiskComplex diskComplex = diskComplexes.get(s);
            AgentMapping mapping = new AgentMapping(diskComplex);
            mappings.put(s, mapping);
        }

        return mappings;
    }

    public boolean adaptVisualisationSettings(VisualizationSettings settings) {
        //here we can change the default settings for e.g. grid visualization, actuator,...
        settings.getColorScheme().wallColor = Color.white;
        settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);

        VisualizationOption opt;
        if (usingMover)
            opt = settings.getOptions().getOption(VisualizationOptions.GROUP_ACTUATORS, Mover.ACTUATOR_NAME);
        else opt = settings.getOptions().getOption(VisualizationOptions.GROUP_ACTUATORS, Teleporter.ACTUATOR_NAME);
        if (opt != null) {
            opt.setEnabled(showVisualizationOfDiskDirection);
            return true;
        }
        return false;
    }

    public HashMap<String, Disk> getDisks() {
        return disks;
    }

    public HashMap<String, Boolean> getCollision() {
        return collision;
    }

    public void setCollision(HashMap<String, Boolean> collision) {
        this.collision = collision;
    }

    public boolean isUsingMover() {
        return usingMover;
    }

    public void setUsingMover(boolean usingMover) {
        this.usingMover = usingMover;
    }
}
