package diskworld.actuators;

import diskworld.Disk;
import diskworld.interfaces.Actuator;

public interface ActuatorWithSuccessSignal extends Actuator {

	public double getSuccessValue(Disk disk);

	public void setSuccessValue(Disk disk, double successValue);

}
