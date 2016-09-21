/*******************************************************************************
 *     DiskWorld - a simple 2D physics simulation environment, 
 *     Copyright (C) 2014  Jan Kneissler
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program in the file "License.txt".  
 *     If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package diskworld.actuators;

import java.util.HashSet;
import java.util.Set;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.grid.GridWithDiskMap;
import diskworld.interfaces.CollidableObject;
import diskworld.interfaces.CollisionEventHandler;
import diskworld.linalg2D.Point;
import diskworld.shape.CircularShapeTemplates;
import diskworld.shape.Shape;
import diskworld.shape.ShapeTemplate;
import diskworld.visualization.AbstractDiskSymbol;
import diskworld.visualization.CircleDiskSymbol;

/**
 * Actuator that can consume disks that it overlaps/collides with.
 * 
 * @author Jan
 */
public class Consumer extends ActuatorWithSuccessSignalAndVisualization {

	public static final String ACTUATOR_NAME = "Mouth";

	private static final AbstractDiskSymbol DISK_SYMBOL = new CircleDiskSymbol(0.3);

	public interface ConsumptionHandler {
		public boolean consumes(Disk disk);
	}

	public enum ConsumptionEventType {
		COLLISION, RANGE, COLLISION_OR_RANGE
	}

	private double activationEnergy;
	private ConsumptionEventType eventType;
	private ConsumptionHandler consumptionHandler;
	private Set<Disk> attachedTo;
	private ShapeTemplate shapeTemplate;

	private GridWithDiskMap diskHash;

	@Override
	public int getDim() {
		return 1;
	}

	/**
	 * Actuator that is able to consume disks when active
	 * 
	 * @param environment
	 *            the environment object in which the sensor lives
	 * @param consumptionRangeRelative
	 *            range of consumption relative to disk radius
	 * @param type
	 *            determines if collisions or overlaps or both can trigger consumption
	 * @param activationEnergy
	 *            energy per time unit consumed when actuator is active
	 * @param consumptionHandler
	 *            handler called to perform consumption
	 */
	public Consumer(Environment environment, double consumptionRangeRelative, ConsumptionEventType type, double activationEnergy, ConsumptionHandler consumptionHandler) {
		super(ACTUATOR_NAME, DISK_SYMBOL);
		this.eventType = type;
		this.activationEnergy = activationEnergy;
		this.consumptionHandler = consumptionHandler;
		this.attachedTo = new HashSet<Disk>();
		this.shapeTemplate = CircularShapeTemplates.getRelativeCircleTemplate(consumptionRangeRelative);
		this.diskHash = environment.getDiskGrid();
	}

	/**
	 * Actuator that consumes disks when in overlapping
	 * 
	 * @param consumptionRangeRelative
	 *            range of consumption relative to disk radius
	 * @param activationEnergy
	 *            energy per time unit consumed when actuator is active
	 * @param consumptionHandler
	 *            handler called to perform consumption
	 */
	public Consumer createOverlapConsumer(Environment environment, double activationEnergy, ConsumptionHandler consumptionHandler) {
		return new Consumer(environment, 1.0, ConsumptionEventType.RANGE, activationEnergy, consumptionHandler);
	}

	/**
	 * Actuator that consumes disks when in overlapping
	 * 
	 * @param consumptionRangeRelative
	 *            range of consumption relative to disk radius
	 * @param activationEnergy
	 *            energy per time unit consumed when actuator is active
	 * @param consumptionHandler
	 *            handler called to perform consumption
	 */
	public Consumer createCollsisionConsumer(Environment environment, double activationEnergy, ConsumptionHandler consumptionHandler) {
		return new Consumer(environment, 0.0, ConsumptionEventType.COLLISION, activationEnergy, consumptionHandler);
	}

	@Override
	public double evaluateEffect(Disk disk, Environment environment, double[] activity, double partial_dt, double total_dt, boolean firstSlice) {
		if (activity[0] > 0.5) {
			if (!eventType.equals(ConsumptionEventType.COLLISION)) {
				checkDisksInRange(disk);
			}
			if (!eventType.equals(ConsumptionEventType.RANGE)) {
				if (!attachedTo.contains(disk)) {
					disk.addEventHandler(createEventHandler(disk));
				}
			}
			return activationEnergy;
		} else {
			return 0.0;
		}
	}

	private CollisionEventHandler createEventHandler(final Disk disk) {
		return new CollisionEventHandler() {
			@Override
			public void collision(CollidableObject collidableObject, Point collisionPoint, double exchangedImpulse) {
				if (collidableObject instanceof Disk) {
					checkDiskConsumption((Disk) collidableObject);
				}
			}
		};
	}

	private void checkDisksInRange(Disk disk) {
		Shape shape = shapeTemplate.getShape(disk.getX(), disk.getY(), disk.getRadius(), disk.getAngle());
		for (Disk d : diskHash.getDisksIntersectingWithShape(shape)) {
			checkDiskConsumption(d);
		}
	}

	private void checkDiskConsumption(Disk disk) {
		if (consumptionHandler.consumes(disk)) {
			setSuccessValue(disk, 1.0);
		}
	}

	@Override
	protected ActuatorVisualisationVariant[] getVisualisationVariants() {
		return new ActuatorVisualisationVariant[] {
				getDiskSymbolVisualization(),
				ACTIVITY_AS_TEXT,
				NO_VISUALISATION,
		};
	}

	@Override
	public boolean isAlwaysNonNegative(int index) {
		return true;
	}

	@Override
	public boolean isBoolean(int index) {
		return true;
	}

	@Override
	public double getActivityFromRealWorldData(double realWorldValue, int index) {
		return realWorldValue;
	}

	@Override
	public String getRealWorldMeaning(int index) {
		return "consumption activation flag";
	}
}
