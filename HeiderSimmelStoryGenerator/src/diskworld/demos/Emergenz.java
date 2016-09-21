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

public class Emergenz extends AgentDemo {

	private static final double SENSOR_OPENING_ANGLE = Math.toRadians(360);
	private static final double MAX_DISPLACEMENT_DISTANCE = 0.2;
	private static final int numAgents = 200;
	protected static final double angleDelta = Math.toRadians(90);

	private DiskComplex[] agents;
	private Disk[] agentDisks;
	private ClosestDiskSensor sensor;
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
		int sizex = 150 * 4 / 3;
		int sizey = 150;
		Environment env = new Environment(sizex, sizey);
		env.getFloor().fill(FloorCellType.GRASS);
		// provide disk types (including sensors and actuators)
		DiskType ball = new DiskType(DiskMaterial.RUBBER);
		sensor = ClosestDiskSensor.getPositionAngleSensor(env, SENSOR_OPENING_ANGLE, 30);
		mover = new Mover(MAX_DISPLACEMENT_DISTANCE, 0.1, 0.1);
		DiskType agentDiskType = new DiskType(DiskMaterial.METAL, mover, new Sensor[] { sensor });

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

		ObjectConstructor oc = new ObjectConstructor(env);
		oc.setRoot(1.0, agentDiskType);
		// construct agent 
		agents = new DiskComplex[numAgents];
		for (int i = 0; i < numAgents; i++) {
			do {
				double xa = (rand.nextDouble() * 0.4 + 0.3) * sizex;
				double ya = (rand.nextDouble() * 0.4 + 0.3) * sizey;
				double aa = rand.nextDouble() * 2 * Math.PI;
				agents[i] = oc.createDiskComplex(xa, ya, aa, 1.0);
			} while (agents[i] == null);
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

	private AgentController getController() {
		final double da = (rand.nextDouble() * 2 - 1) * angleDelta * 1.3;
		return new AgentController() {
			@Override
			public void doTimeStep(double[] sensorValues, double[] actuatorValues) {
				if (sensorValues[0] < 0.5) {
					// no disk seen, stop
					actuatorValues[0] = 0.0;
					actuatorValues[1] = 0.0;
				} else {
					// disk seen, move
					actuatorValues[0] = 0.5 + 0.2 * rand.nextGaussian();
					double angle = sensor.getRealWorldInterpretation(sensorValues, 1);
					angle += da;
					// and turn towards the seen disk
					actuatorValues[1] = mover.getActivityFromRealWorldData(angle, 1);
				}
			}
		};
	}

	@Override
	public AgentController[] getControllers() {
		AgentController res[] = new AgentController[agents.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = getController();
		}
		return res;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new Emergenz());
	}

	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COLLISIONS).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);

		VisualizationOption opt = settings.getOptions().getOption(VisualizationOptions.GROUP_SENSORS, ClosestDiskSensor.SENSOR_NAME);
		if (opt != null) {
			//opt.setChosenOption(1);
			opt.setEnabled(false);
			return true;
		}
		return false;
	}

}