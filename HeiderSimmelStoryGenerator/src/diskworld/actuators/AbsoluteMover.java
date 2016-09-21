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

import diskworld.Disk;
import diskworld.Environment;

/**
 * Actuator that moves a disk complex in absolute environment coordinates. The orientation of the disk complex is irrelevant. 
 * The actuator array holds the following values:
 * - component[0] = displacement along x coordinate
 * - component[1] = displacement along y coordinate
 * 
 * Actuator values are multiplied with a constant value "maxMoveDistancePerCoordinate".
 * Energy consumption is proportional to the moved distance per coordinate.
 * 
 * @author Jan
 */
public class AbsoluteMover extends ActuatorWithVisualisation {

	private static final long serialVersionUID = 1L;

	public static final String ACTUATOR_NAME = "AbsoluteMover";

	// indices of activity values:
	public static final int X = 0;
	public static final int Y = 1;

	private final double maxMoveDistancePerCoordinate;
	private final double moveEnergyConsumptionFactor;

	@Override
	public int getDim() {
		return 2;
	}

	/**
	 * Actuator that moves in absolute coordinates irrespective of its orientation
	 * 
	 * @param maxMoveDistancePerCoordinate
	 * 		maximal moved distance in one time unit 
	 * @param moveEnergyConsumptionFactor
	 * 		factor determining how much energy is consumed per mass unit
	 */
	public AbsoluteMover(double maxMoveDistancePerCoordinate, double moveEnergyConsumptionFactor) {
		super(ACTUATOR_NAME);
		this.maxMoveDistancePerCoordinate = maxMoveDistancePerCoordinate;
		this.moveEnergyConsumptionFactor = moveEnergyConsumptionFactor;
	}
	
	@Override
	public double evaluateEffect(Disk disk, Environment environment, double[] activity, double partial_dt, double total_dt, boolean firstSlice) {
		if (firstSlice) {
			// set velocity and angular speed in first time slice
			double vx =  activity[0] * maxMoveDistancePerCoordinate / total_dt;
			double vy =  activity[1] * maxMoveDistancePerCoordinate / total_dt;
			disk.getDiskComplex().setVelocity(vx, vy);
			// stop rotation
			disk.getDiskComplex().setAngularSpeed(0);
			return (Math.abs(activity[0])+Math.abs(activity[1])) * moveEnergyConsumptionFactor * disk.getDiskComplex().getMass();
		} else {
			// in other time slices: do nothing
			return 0.0;
		}
	}

	@Override
	protected ActuatorVisualisationVariant[] getVisualisationVariants() {
		return new ActuatorVisualisationVariant[] {
				ACTIVITY_AS_TEXT,
				NO_VISUALISATION,
		};
	}

	@Override
	public boolean isAlwaysNonNegative(int index) {
		return false;
	}

	@Override
	public boolean isBoolean(int index) {
		return false;
	}

	@Override
	public double getActivityFromRealWorldData(double realWorldValue, int index) {
		return realWorldValue / maxMoveDistancePerCoordinate;
	}

	@Override
	public String getRealWorldMeaning(int index) {
		return "movedDistance_" + (index == 0 ? "x" : "y");
	}
}
