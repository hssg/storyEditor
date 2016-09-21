package diskworld.actuators;

import java.util.HashMap;
import java.util.Map;

import diskworld.Disk;
import diskworld.visualization.AbstractDiskSymbol;

public abstract class ActuatorWithSuccessSignalAndVisualization extends ActuatorWithVisualisation implements ActuatorWithSuccessSignal {

	private Map<Disk, Double> success;

	public ActuatorWithSuccessSignalAndVisualization(String variantName, AbstractDiskSymbol symbol) {
		super(variantName, symbol);
		success = new HashMap<Disk, Double>();
	}

	public double getSuccessValue(Disk disk) {
		Double res = success.get(disk);
		return (res == null) ? 0.0 : res;
	}

	public void setSuccessValue(Disk disk, double successValue) {
		success.put(disk, successValue);
	}

}
