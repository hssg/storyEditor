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
import diskworld.environment.Wall;
import diskworld.linalg2D.Line;
import diskworld.linalg2D.Point;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class Intro extends PhysicsDemo {

	@Override
	public String getTitle() {
		return "Intro";
	}

	public static boolean showFloor = true;
	public static boolean showWalls = true;
	public static boolean showDisks = true;
	public static boolean moveDisks = true;

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 160;
		int sizey = 90;
		Environment.DEFAULT_WALL_THICKNESS = 1;
		Environment env = new Environment(sizex, sizey);
		env.canAddWall(new Wall(new Line(new Point(60, 50), new Point(90, 20)), 4));
		env.canAddWall(new Wall(new Line(new Point(120, 50), new Point(90, 20)), 4));
		Floor floor = env.getFloor();
		FloorCellType fct1 = new FloorCellType(10, Color.BLUE);
		FloorCellType fct2 = new FloorCellType(0, Color.GREEN);
		for (int i = 0; i < floor.getMaxX(); i++) {
			for (int j = 0; j < floor.getMaxY(); j++) {
				floor.setType(i, j, i + j < 80 ? fct1 : fct2);
			}
		}
		DiskMaterial dm = new DiskMaterial(1, 1, 4, 0.5, Color.RED);
		DiskType dt = new DiskType(dm);
		double r1 = 5;
		double r2 = 3;

		Disk d = env.newRootDisk(60, 70, r1, dt);
		Disk d2 = d.attachDisk(0, r1, dt);
		Disk d3 = env.newRootDisk(110, 10 - 1, r2, dt);
		if (moveDisks) {
			d.applyImpulse(0, 1000);
			d2.applyImpulse(0.0, -1000);
			d3.applyImpulse(1510.0, 2000);
		}

		return env;
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COLLISIONS).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_FLOOR).setEnabled(showFloor);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_DISKS).setEnabled(showDisks);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_WALLS).setEnabled(showWalls);
		return true;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new Intro());
	}

}