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
package diskworld.demos.agents;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import diskworld.Disk;
import diskworld.DiskComplex;
import diskworld.DiskMaterial;
import diskworld.DiskType;
import diskworld.Environment;
import diskworld.actuators.ImpulsDrive;
import diskworld.demos.AgentDemo;
import diskworld.demos.DemoLauncher;
import diskworld.environment.AgentMapping;
import diskworld.environment.DefaultPhysicsParameters;
import diskworld.environment.FloorCellType;
import diskworld.environment.SlidingFrictionModel;
import diskworld.interfaces.Actuator;
import diskworld.interfaces.AgentController;
import diskworld.interfaces.Sensor;
import diskworld.sensors.ClosestDiskSensor;

public class BraitenbergVehicle extends AgentDemo {

	private static final int NUM_AGENTS = 1;
	private static final double IMPULSE_DRIVE_STRENGTH = 20;
	private static final FloorCellType GLUE = new FloorCellType(0.999, Color.GREEN.darker().darker());

	private DiskComplex agent;

	private ClosestDiskSensor sensor;

	@Override
	public String getTitle() {
		return "Dynamic Braitenberg Vehicle";
	}

	@Override
	public long getMiliSecondsPerTimeStep() {
		return 40;
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 30;
		int sizey = 30;
		Environment env = new Environment(sizex, sizey);
		env.setPhysicsParameters(new DefaultPhysicsParameters(new SlidingFrictionModel(env.getFloor(), env.getFloorGrid(), 0.1)));
		env.getFloor().fill(GLUE);

		// provide disk types (including sensors and actuators)
		DiskType ball = new DiskType(DiskMaterial.RUBBER);
		Set<DiskMaterial> invisibleMaterials = new HashSet<DiskMaterial>();
		invisibleMaterials.add(DiskMaterial.METAL);
		sensor = ClosestDiskSensor.getPositionAngleSensor(env, Math.toRadians(200), 15, invisibleMaterials);

		DiskType agentSensor = new DiskType(DiskMaterial.METAL, new Sensor[] { sensor });
		Actuator impulseDrive = new ImpulsDrive(IMPULSE_DRIVE_STRENGTH, 0.0);
		DiskType agentDrive = new DiskType(DiskMaterial.METAL, impulseDrive);

		// change the material properties: disable friction for rubber, set high friction for metal
		DiskMaterial.RUBBER.setFrictionCoefficient(2.999);
		DiskMaterial.METAL.setFrictionCoefficient(2.999);

		// construct ball (one disk made of rubber)
		double x1 = 10;
		double y1 = 15;
		double r1 = 1;
		Disk d1 = env.newRootDisk(x1, y1, r1, ball);
		// give it a kick
		d1.applyImpulse(0, 0);

		// construct agent (a sensor and two motors on the sides)
		double x2 = 20;
		double y2 = 20;
		double r2 = 1;
		Disk d2 = env.newRootDisk(x2, y2, r2, Math.toRadians(271), agentSensor);
		d2.attachDisk(Math.toRadians(90), 0.5, Math.toRadians(90), agentDrive);
		d2.attachDisk(Math.toRadians(-90), 0.5, Math.toRadians(-90), agentDrive);

		// store the agent diskComplex for later use
		agent = d2.getDiskComplex();

		return env;
	}

	@Override
	public AgentMapping[] getAgentMappings() {
		// create the default mapping of sensors/actuators to a double array
		AgentMapping mapping = new AgentMapping(agent);
		return new AgentMapping[] { mapping };
	}

	@Override
	public AgentController[] getControllers() {
		// the controller of the agent
		AgentController controller = new AgentController() {
			@Override
			public void doTimeStep(double[] sensorValues, double[] actuatorValues) {
				// sensorValues has dim 2 (disk seen, angle to closest disk in view)
				boolean seen = sensorValues[0] > 0.5;
				double dir = sensor.getRealWorldInterpretation(sensorValues, 1);
				double sv = sensorValues[1];
				double asv = 1.0 - Math.abs(sv);
				double mix1 = 0.8;
				double mix2 = 0.8;
				if (seen) {
					actuatorValues[0] = -sv * mix1 + asv * mix2;
					actuatorValues[1] = sv * mix1 + asv * mix2;
				} else {
					actuatorValues[0] = 0;
					actuatorValues[1] = 0;
				}
			}
		};
		AgentController[] cont = new AgentController[NUM_AGENTS];
		for (int i = 0; i < cont.length; i++) {
			cont[i] = controller;
		}
		return cont;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new BraitenbergVehicle());
	}

}
