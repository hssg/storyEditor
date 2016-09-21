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

public class GridView extends PhysicsDemo {

	@Override
	public String getTitle() {
		return "Intro";
	}

	public static boolean showGrid = true;
	public static boolean showCoordinates = true;

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 160;
		int sizey = 90;
		Environment env = new Environment(sizex, sizey);
		Floor floor = env.getFloor();
		FloorCellType fct = new FloorCellType(0, Color.GREEN);
		floor.fill(fct);
		DiskMaterial dm = new DiskMaterial(1, 1, 1, 0.5, Color.RED);
		DiskType dt = new DiskType(dm);
		double r1 = 5;
		double r2 = 3;
		double impulsey1 = 2000;

		Disk d = env.newRootDisk(140, 20, r1, dt);
		Disk d2 = d.attachDisk(0, r1, dt);
		Disk d3 = d2.attachDisk(Math.toRadians(-120), r1, dt);
		d.applyImpulse(100 + impulsey1, 3000 - impulsey1);
		d2.applyImpulse(-90.0, impulsey1);

		env.newRootDisk(50, 45, 40, dt);
		env.newRootDisk(110, 80, r2, dt).getDiskComplex().setAngularSpeed(2);

		Disk d4 = env.newRootDisk(110, 30, 1, dt);
		for (int i = 0; i < 10; i++)
			d4 = d4.attachDisk(Math.toRadians(20), 1, dt);
		return env;
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COORDINATES).setEnabled(showCoordinates);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(showGrid);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setChosenOption(1);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COLLISIONS).setEnabled(false);
		return true;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new GridView());
	}

}