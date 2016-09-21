package storygenerator.menu;

import java.awt.Color;
import java.io.Serializable;

import diskworld.Disk;

public class MenuDisk implements Serializable {
    private static final long serialVersionUID = 1L;

    public String diskName;
    public double x;
    public double y;
    public double radius;
    public Color col;
    public double impulseX;
    public double impulseY;

    public MenuDisk() {
        this.diskName = "Disk1";
        this.x = 0.0;
        this.y = 0.0;
        this.radius = 0.0;
        this.col = DiskWorldStory.DEFAULT_DISK_COLOR;
        this.impulseX = 0.0;
        this.impulseY = 0.0;
    }

    public MenuDisk(String diskName, double x, double y, double radius, Color col, double impulseX, double impulseY) {
        this.diskName = diskName;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.col = col;
        this.impulseX = impulseX;
        this.impulseY = impulseY;
    }

    public MenuDisk(String diskName, double x, double y, double radius, Color col) {
        this.diskName = diskName;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.col = col;
        this.impulseX = 0.0;
        this.impulseY = 0.0;
    }

    public MenuDisk(Disk d) {
        if (null != d.getName())
            this.diskName = d.getName();
        else
            this.diskName = "noNameDisk";
        this.x = d.getX();
        this.y = d.getY();
        this.radius = d.getRadius();
        this.col = d.getDiskType().getMaterial().getDisplayColor();
        this.impulseX = d.getDiskComplex().getMomentumx();
        this.impulseY = d.getDiskComplex().getMomentumy();
    }


}
