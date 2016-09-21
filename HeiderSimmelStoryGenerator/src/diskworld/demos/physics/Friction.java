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
import diskworld.environment.Floor;
import diskworld.environment.FloorCellType;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class Friction extends PhysicsDemo {

	@Override
	public String getTitle() {
		return "Friction";
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 10;
		int sizey = 10;
		Environment env = new Environment(sizex, sizey);
		Floor floor = env.getFloor();
		FloorCellType fct1 = new FloorCellType(4, Color.BLUE);
		FloorCellType fct2 = new FloorCellType(2, Color.GREEN);
		for (int i = 0; i < floor.getMaxX(); i++) {
			for (int j = 0; j < 6; j++) {
				floor.setType(i, j, i * 2 < floor.getMaxX() ? fct1 : fct2);
			}
		}

		DiskMaterial dm = new DiskMaterial(1, 1, 0.5, 0.5, Color.RED);
		DiskType dt = new DiskType(dm);
		double r1 = 0.1;
		double r2 = 0.2;
		double impulsey1 = -0.1;
		double impulsey2 = impulsey1 * (r2 * r2) / (r1 * r1);

		Disk d = env.newRootDisk(1 - r1, 9, r1, dt);
		d.attachDisk(0, r1, dt).applyImpulse(0.0, impulsey1);
		d.applyImpulse(0.0, impulsey1);

		d = env.newRootDisk(3 - r2, 9, r2, dt);
		d.attachDisk(0, r2, dt).applyImpulse(0.0, impulsey2);
		d.applyImpulse(0.0, impulsey2);

		d = env.newRootDisk(5 - r2, 9, r2, dt);
		d.attachDisk(0, r2, dt).applyImpulse(0.0, impulsey2);
		d.applyImpulse(0.0, impulsey2);

		d = env.newRootDisk(7 - r2, 9, r2, dt);
		d.attachDisk(0, r2, dt).applyImpulse(0.0, impulsey2);
		d.applyImpulse(0.0, impulsey2);

		d = env.newRootDisk(9 - r1, 9, r1, dt);
		d.attachDisk(0, r1, dt).applyImpulse(0.0, impulsey1);
		d.applyImpulse(0.0, impulsey1);
		return env;
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		return true;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new Friction());
	}

}