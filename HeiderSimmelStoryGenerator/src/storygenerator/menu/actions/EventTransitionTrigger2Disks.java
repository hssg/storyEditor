package storygenerator.menu.actions;

import diskworld.Disk;

/**
 * For {@link EventTransitionTrigger} which need a reference disk.
 * //TODO handle cases where disk equals reference disk
 *
 * @author Svenja
 */
public abstract class EventTransitionTrigger2Disks extends EventTransitionTrigger {
    private static final long serialVersionUID = 1L;
    private transient Disk referenceDisk;
    private String referenceDiskName;

    public String getReferenceDiskName() {
        return referenceDiskName;
    }

    public void setReferenceDiskName(String referenceDiskName) {
        this.referenceDiskName = referenceDiskName;
    }

    public EventTransitionTrigger2Disks() {
        super();
    }

    public Disk getReferenceDisk() {
        return referenceDisk;
    }

    public void setReferenceDisk(Disk referenceDisk) {
        this.referenceDisk = referenceDisk;
    }

    protected double getAngleOfThisDiskAndReferenceDisk(Disk thisDisk) {
        double tx = thisDisk.getX();
        double ty = thisDisk.getY();
        double rx = this.getReferenceDisk().getX();
        double ry = this.getReferenceDisk().getY();
        double angle1 = Math.atan2((ry - ty), (rx - tx));
        return angle1;
    }
}
