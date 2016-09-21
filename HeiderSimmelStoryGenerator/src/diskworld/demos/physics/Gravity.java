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
package diskworld.demos.physics;

import java.awt.Color;

import diskworld.Disk;
import diskworld.DiskMaterial;
import diskworld.DiskType;
import diskworld.Environment;
import diskworld.ObjectConstructor;
import diskworld.demos.DemoLauncher;
import diskworld.demos.PhysicsDemo;
import diskworld.environment.ConstantGravityModel;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class Gravity extends PhysicsDemo {

	public static boolean GRAVITY_ON = true;

	@Override
	public String getTitle() {
		return "Gravity";
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 10;
		int sizey = 10;
		Environment env = new Environment(sizex, sizey);
		double gravityConstant = 1.0;
		env.getPhysicsParameters().setGravityModel(new ConstantGravityModel(GRAVITY_ON ? gravityConstant : 0.0));

		DiskMaterial dm = new DiskMaterial(1, 0.1, 0.5, 0.5, Color.RED);
		DiskType dt = new DiskType(dm);

		double r = 0.1;
		ObjectConstructor oc = env.createObjectConstructor();
		oc.setRoot(r, dt);
		int num = 10;
		for (int i = 1; i < num; i++) {
			oc.addItem(i - 1, (((i / 2) % 2) * 2 - 1) * Math.PI / 2, 0, r, dt);
		}
		oc.createObject(5, 3.4, 1.2, 1);

		DiskMaterial dm2 = new DiskMaterial(1, 0.5, 0.5, 0.5, Color.BLUE);
		DiskType dt2 = new DiskType(dm2);
		Disk d1 = env.newRootDisk(1, 1, 0.2, dt2);
		d1.applyImpulse(0.2, 0.45);

		env.newRootDisk(2, 9, 0.1, dt2);
		env.newRootDisk(3, 9, 0.2, dt2);
		return env;
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COLLISIONS).setEnabled(false);
		return true;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new Gravity());
	}

}