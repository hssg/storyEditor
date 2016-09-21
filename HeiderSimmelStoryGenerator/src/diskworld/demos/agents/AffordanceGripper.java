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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import diskworld.Disk;
import diskworld.DiskMaterial;
import diskworld.DiskType;
import diskworld.Environment;
import diskworld.actions.DiskAction;
import diskworld.actions.Joint;
import diskworld.actions.JointActionType;
import diskworld.demos.AgentDemo;
import diskworld.demos.DemoLauncher;
import diskworld.environment.AgentMapping;
import diskworld.environment.FloorCellType;
import diskworld.interfaces.AgentController;
import diskworld.interfaces.Sensor;
import diskworld.sensors.ClosestDiskSensor;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class AffordanceGripper extends AgentDemo {

	private static final int NUM_SENSORS = 12;
	private static final double ANGLE_RANGE = Math.toRadians(60);

	DiskMaterial rootMaterial = DiskMaterial.METAL.withColor(Color.BLUE.darker());
	DiskMaterial limbMaterial = DiskMaterial.METAL.withColor(Color.GRAY);
	DiskMaterial jointMaterial = DiskMaterial.METAL.withColor(Color.RED.darker());

	private Joint joints[];

	@Override
	public String getTitle() {
		return "Gripper and Object with Affordance";
	}

	private Sensor[] createSensors(Environment env) {
		Sensor[] res = new Sensor[NUM_SENSORS];
		Set<DiskMaterial> invisibleMaterials = new HashSet<DiskMaterial>();
		invisibleMaterials.add(rootMaterial);
		invisibleMaterials.add(limbMaterial);
		invisibleMaterials.add(jointMaterial);
		for (int i = 0; i < NUM_SENSORS; i++) {
			double min = getAngle(i);
			double max = getAngle(i + 1);
			res[i] = new ClosestDiskSensor(env, (min + max) / 2, max - min, 1, 60, invisibleMaterials, false, true, false, null);
		}
		return res;
	}

	private double getAngle(int i) {
		double pos = i - NUM_SENSORS * 0.5;
		double abspos = Math.abs(pos) / NUM_SENSORS * 2.0;
		double angle = abspos * abspos * ANGLE_RANGE;
		//System.out.println(i + " --> " + abspos + "--> " + angle);
		return angle * Math.signum(pos);
	}

	public Joint[] createRobotHand(Environment env, double x, double y) {
		Sensor[] sensors = createSensors(env);
		DiskType rootType = new DiskType(rootMaterial, sensors);
		DiskType limbType = new DiskType(limbMaterial);
		DiskType jointType = new DiskType(jointMaterial);

		int numPerLimb = 5;
		int numLimbs = 3;
		int numPerFinger = 3;
		Joint joint[] = new Joint[numLimbs + 4];
		Disk d = joint[0] = env.newRootJoint(x, y, 5, 0, rootType, true);
		d = joint[1] = d.attachJoint(0, 1, 0, jointType);
		for (int i = 0; i < numLimbs; i++) {
			if (i > 0) {
				d = joint[i + 1] = d.attachJoint(0, 1, 0, jointType);
			}
			for (int j = 0; j < numPerLimb; j++) {
				d = d.attachDisk(0, 1, limbType);
			}
		}
		Joint fork = joint[numLimbs + 1] = d.attachJoint(0, 2, 0, jointType);

		d = fork.attachDisk(Math.toRadians(60), 1, limbType);
		d = joint[numLimbs + 2] = d.attachJoint(Math.toRadians(-30), 1, Math.toRadians(-15), jointType);
		for (int i = 0; i < numPerFinger; i++) {
			d = d.attachDisk(Math.toRadians(-15), 1, Math.toRadians(0), limbType);
		}

		d = fork.attachDisk(Math.toRadians(-60), 1, limbType);
		d = joint[numLimbs + 3] = d.attachJoint(Math.toRadians(30), 1, Math.toRadians(15), jointType);
		for (int i = 0; i < numPerFinger; i++) {
			d = d.attachDisk(Math.toRadians(15), 1, Math.toRadians(0), limbType);
		}
		return joint;
	}

	@Override
	public long getMiliSecondsPerTimeStep() {
		return 10;
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 150;
		int sizey = 150;
		Environment env = new Environment(sizex, sizey);
		env.getFloor().fill(FloorCellType.STONE_LIGHT);
		// construct arms
		joints = createRobotHand(env, sizex / 2, sizey / 2);

		DiskMaterial objectMaterial = DiskMaterial.METAL.withColor(Color.GREEN.darker());
		DiskType objectType = new DiskType(objectMaterial);
		env.newRootDisk(sizex / 2, sizey / 2 + 40, 1.5, objectType);
		Disk d = env.newRootDisk(sizex / 2 + 20, sizey / 2 + 30, 5, objectType);
		d.attachDisk(-1.8, 0.9, 0, objectType);
		return env;
	}

	@Override
	public AgentMapping[] getAgentMappings() {
		// set max angular speed of joint rotations
		double maxAngularChange = 1;
		// first joint is controlled by target angle
		AgentMapping res[] = new AgentMapping[1];
		// create possible ego-motion actions
		List<DiskAction> actions = new LinkedList<DiskAction>();
		actions.add(joints[0].createJointAction("joint#" + 0, maxAngularChange, JointActionType.ActionType.SPIN, JointActionType.ControlType.CHANGE));
		actions.add(joints[1].createJointAction("joint#" + 1, maxAngularChange, JointActionType.ActionType.SLIDE, JointActionType.ControlType.CHANGE));
		for (int i = 2; i < joints.length; i++) {
			actions.add(joints[i].createJointAction("joint#" + i, maxAngularChange, JointActionType.ActionType.SPIN, JointActionType.ControlType.CHANGE));
		}
		res[0] = new AgentMapping(actions);
		return res;
	}

	@Override
	public AgentController[] getControllers() {
		AgentController res[] = new AgentController[1];
		for (int i = 0; i < res.length; i++) {
			res[i] = new AgentController() {
				int time = 0;

				@Override
				public void doTimeStep(double[] sensorValues, double[] actuatorValues) {
					for (int i = 0; i < actuatorValues.length; i++) {
						actuatorValues[i] = 0;
					}
					time++;
					if (time < 91) {
						actuatorValues[0] = 1;
						actuatorValues[1] = -1;
					} else if (time < 1900 - 905) {
						actuatorValues[1] = -0.1;
						actuatorValues[2] = 0.2;
						actuatorValues[3] = 0.1;
						actuatorValues[4] = -0.06;
					} else if (time < 2100 - 905) {
						actuatorValues[5] = 0.2;
						actuatorValues[6] = -0.2;
						actuatorValues[1] = 0.05;
						actuatorValues[3] = -0.05;
					} else if (time < 2900 - 905 - 200) {
						actuatorValues[1] = 0.05;
						actuatorValues[3] = -0.05;
					} else if (time < 3350 - 905 - 200) {
						actuatorValues[5] = -0.1;
						actuatorValues[6] = 0.1;
					} else if (time < 3700 - 905 - 200) {
						actuatorValues[0] = 1;
					} else if (time < 3750 - 905 - 200) {
						actuatorValues[0] = -1;
						actuatorValues[5] = 0.1;
						actuatorValues[6] = -0.1;
					}
				}
			};
		}
		return res;
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COLLISIONS).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
		return true;
	}

	public static void main(String[] args) {
		DemoLauncher.DT_PER_STEP = 0.01;
		DemoLauncher.runDemo(new AffordanceGripper());
	}

}