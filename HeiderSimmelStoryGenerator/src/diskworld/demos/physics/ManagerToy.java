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
import diskworld.demos.DemoLauncher;
import diskworld.demos.PhysicsDemo;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class ManagerToy extends PhysicsDemo {

	public static boolean INTERFERENCE = true;

	@Override
	public String getTitle() {
		return "Manager Toy";
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 10;
		int sizey = 10;
		Environment env = new Environment(sizex, sizey);

		DiskType dt1 = new DiskType(DiskMaterial.RUBBER.withColor(Color.BLUE));
		Disk d1 = env.newRootDisk(1, 5, 0.5, dt1);
		d1.applyImpulse(1.6, 0.0);

		DiskType dt2 = new DiskType(DiskMaterial.RUBBER.withColor(Color.GREEN));
		env.newRootDisk(4, 5, 0.5, dt1);
		env.newRootDisk(5, 5, 0.5, dt1);
		env.newRootDisk(6, 5, 0.5, dt1);
		env.newRootDisk(7, 5, 0.5, dt1);

		if (INTERFERENCE) {
			Disk d2 = env.newRootDisk(5, 8, 0.05, dt2);
			d2.applyImpulse(0.0, -0.001);
		}
		return env;
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
		return true;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new ManagerToy());
	}

}