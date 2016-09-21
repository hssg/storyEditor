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
package diskworld.demos;

import java.awt.Color;
import java.util.Random;

import diskworld.Disk;
import diskworld.DiskComplex;
import diskworld.DiskMaterial;
import diskworld.DiskType;
import diskworld.Environment;
import diskworld.ObjectConstructor;
import diskworld.actuators.Mover;
import diskworld.environment.AgentMapping;
import diskworld.environment.FloorCellType;
import diskworld.interfaces.AgentController;
import diskworld.interfaces.Sensor;
import diskworld.sensors.ClosestDiskSensor;
import diskworld.visualization.VisualizationOption;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class Emergenz3 extends AgentDemo {

	private static final double SENSOR_OPENING_ANGLE_1 = Math.toRadians(45);
	private static final double SENSOR_OPENING_ANGLE_2 = Math.toRadians(270);
	private static final double SENSOR_RANGE_1 = 20;
	private static final double SENSOR_RANGE_2 = 10;
	private static final int NUM_AGENTS_1 = 10;
	private static final int NUM_AGENTS_2 = 20;
	private static final int SIZE = 100;
	protected static final double DESIRED_DIST = 2;

	private DiskComplex[] agents;
	private Disk[] agentDisks;
	private ClosestDiskSensor sensor1, sensor2;
	private Mover mover;
	Random rand = new Random();

	@Override
	public String getTitle() {
		return "Multiple Disk Follower";
	}

	@Override
	public long getMiliSecondsPerTimeStep() {
		return 5;
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = SIZE;
		int sizey = SIZE;
		Environment env = new Environment(sizex, sizey);
		env.getFloor().fill(FloorCellType.GRASS);
		// provide disk types (including sensors and actuators)
		DiskType ball = new DiskType(DiskMaterial.RUBBER);
		sensor1 = new ClosestDiskSensor(env, 0, SENSOR_OPENING_ANGLE_1, 1.1, SENSOR_RANGE_1, null, true, true, false, null);
		sensor2 = new ClosestDiskSensor(env, 0, SENSOR_OPENING_ANGLE_2, 1.1, SENSOR_RANGE_2, null, true, true, false, null);
		//mover = new Mover(MAX_DISPLACEMENT_DISTANCE, 0.1, 0.1);
		mover = new Mover(true, 0.1, 0.1, 0.01, 0.0, 0.0);
		DiskType agentDiskType1 = new DiskType(DiskMaterial.METAL.withColor(Color.BLUE), mover, new Sensor[] { sensor1 });
		DiskType agentDiskType2 = new DiskType(DiskMaterial.METAL, mover, new Sensor[] { sensor2 });

		// change the material properties: disable friction for rubber, set high friction for metal
		DiskMaterial.RUBBER.setFrictionCoefficient(0.0);
		DiskMaterial.METAL.setFrictionCoefficient(0.4);

		// construct balls (disks made of rubber)
		double x1 = 8;
		double y1 = 14;
		double r1 = 1;
		//Disk d1 = env.newRootDisk(x1, y1, r1, ball);
		// give it a kick
		//d1.applyImpulse(+6, -3);

		double x2 = 12;
		double y2 = 15;
		double r2 = 0.5;
		//Disk d2 = env.newRootDisk(x2, y2, r2, ball);
		// give it a kick
		//d2.applyImpulse(+4, -3);

		ObjectConstructor oc1 = new ObjectConstructor(env);
		oc1.setRoot(1.0, agentDiskType1);
		ObjectConstructor oc2 = new ObjectConstructor(env);
		oc2.setRoot(1.0, agentDiskType2);
		// construct agent 
		agents = new DiskComplex[NUM_AGENTS_1 + NUM_AGENTS_2];
		for (int i = 0; i < NUM_AGENTS_1; i++) {
			do {
				double xa = (rand.nextDouble() * 0.4 + 0.3) * sizex;
				double ya = (rand.nextDouble() * 0.4 + 0.3) * sizey;
				double aa = rand.nextDouble() * 2 * Math.PI;
				agents[i] = oc1.createDiskComplex(xa, ya, aa, 1.0);
			} while (agents[i] == null);
		}
		for (int i = 0; i < NUM_AGENTS_2; i++) {
			do {
				double xa = (rand.nextDouble() * 0.4 + 0.3) * sizex;
				double ya = (rand.nextDouble() * 0.4 + 0.3) * sizey;
				double aa = rand.nextDouble() * 2 * Math.PI;
				agents[NUM_AGENTS_1 + i] = oc2.createDiskComplex(xa, ya, aa, 1.0);
			} while (agents[NUM_AGENTS_1 + i] == null);
		}
		return env;
	}

	@Override
	public AgentMapping[] getAgentMappings() {
		// create the default mapping of sensors/actuators to a double array
		AgentMapping[] mappings = new AgentMapping[agents.length];
		for (int i = 0; i < mappings.length; i++) {
			mappings[i] = new AgentMapping(agents[i]);
		}
		return mappings;
	}

	/**
	 * Predator controller
	 * 
	 * @return
	 */
	private AgentController getController1() {
		return new AgentController() {
			private double dir = rand.nextDouble() * Math.PI * 2;

			@Override
			public void doTimeStep(double[] sensorValues, double[] actuatorValues) {
				if (sensorValues[0] < 0.5) {
					// no disk seen, move randomly
					if (rand.nextDouble() < 0.1) {
						dir = rand.nextDouble() * Math.PI * 2;
					}
					actuatorValues[0] = 0.25;
					actuatorValues[1] = dir;
				} else {
					// disk seen, move
					actuatorValues[0] = 0.5;
					double angle = sensor1.getRealWorldInterpretation(sensorValues, 1);
					double distance = sensor1.getRealWorldInterpretation(sensorValues, 2);
					// and turn towards the seen disk
					actuatorValues[1] = mover.getActivityFromRealWorldData(angle, 1);
				}
			}
		};
	}

	private AgentController getController2() {
		final double da = Math.PI; // (rand.nextDouble() * 2 - 1) * ANGLE_DELTA;
		return new AgentController() {
			@Override
			public void doTimeStep(double[] sensorValues, double[] actuatorValues) {
				if (sensorValues[0] < 0.5) {
					// no disk seen, stop
					actuatorValues[0] = 0;
					actuatorValues[1] = 0;
				} else {
					// disk seen, move
					double angle = sensor2.getRealWorldInterpretation(sensorValues, 1);
					double distance = sensor2.getRealWorldInterpretation(sensorValues, 2);
					angle += da;
					// and turn towards the seen disk
					actuatorValues[0] = 1; //distance > DESIRED_DIST ? 1 : -1;
					actuatorValues[1] = mover.getActivityFromRealWorldData(angle, 1);
				}
			}
		};
	}

	@Override
	public AgentController[] getControllers() {
		AgentController res[] = new AgentController[agents.length];
		for (int i = 0; i < NUM_AGENTS_1; i++) {
			res[i] = getController1();
		}
		for (int i = 0; i < NUM_AGENTS_2; i++) {
			res[NUM_AGENTS_1 + i] = getController2();
		}
		return res;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new Emergenz3());
	}

	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COLLISIONS).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);

		VisualizationOption opt = settings.getOptions().getOption(VisualizationOptions.GROUP_ACTUATORS, Mover.ACTUATOR_NAME);
		if (opt != null) {
			opt.setChosenOption(2);
			opt.setEnabled(true);
		} else {
			return false;
		}
		opt = settings.getOptions().getOption(VisualizationOptions.GROUP_SENSORS, ClosestDiskSensor.SENSOR_NAME);
		if (opt != null) {
			//opt.setChosenOption(1);
			opt.setEnabled(false);
		} else {
			return false;
		}
		return true;

	}

}