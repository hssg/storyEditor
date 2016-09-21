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
import diskworld.environment.Wall;
import diskworld.linalg2D.Line;
import diskworld.linalg2D.Point;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class Rotation extends PhysicsDemo {

	@Override
	public String getTitle() {
		return "Rotation";
	}

	@Override
	public Environment getEnvironment() {
		double timeFactor = 2.0;

		// create environment
		int num = 4;
		int sizef = 5;

		int sizex = num * sizef;
		int sizey = 2 * sizef;
		Environment env = new Environment(sizex, sizey);

		double walloff = Environment.DEFAULT_WALL_THICKNESS * 0.5;
		for (int i = 0; i < num; i++) {
			env.canAddWall(new Wall(new Line(new Point((double) i * sizex / num, walloff), new Point((double) i * sizex / num, sizey - walloff)), 0.1));
		}
		env.canAddWall(new Wall(new Line(new Point(walloff, sizey / 2), new Point(sizex - walloff, sizey / 2)), 0.1));

		double r = 0.25;
		double off_factor = 0.06;
		double impx = -0.1 * timeFactor;
		double rotImp = impx;
		double a = 1.839 - 0.003;
		double b = 0.045;
		double startx1[] = new double[] { a - 0.01, a + b - 0.05, a + 2 * b - 0.04, a + 3 * b };
		double startx2[] = new double[] { a - 0.01, a + b - 0.05, a + 2 * b - 0.05, a + 3 * b };
		double elasticity = 0.5;

		DiskMaterial dm = new DiskMaterial(1, elasticity, 0, 0, Color.WHITE);
		DiskType dt1 = new DiskType(dm.withColor(Color.BLUE));
		DiskType dt2 = new DiskType(dm.withColor(Color.GREEN));

		for (int i = 0; i < num; i++) {
			Disk d1, d2;
			double off = i * off_factor;
			d1 = env.newRootDisk(i * sizef + startx1[i], 7.5 - off, r, dt1);
			d1.attachDisk(0, r, dt1);//.applyImpulse(impx1b, 0.0);
			//d1.applyImpulse(impx1, 0.0);
			d2 = env.newRootDisk((i + 1) * sizef - 1, 7.5 + off, r, dt2);
			d2.attachDisk(0, r, dt2).applyImpulse(impx, 0.0);
			d2.applyImpulse(impx, 0.0);

			d1 = env.newRootDisk(i * sizef + startx2[i], 2.5 - off, r, dt1);
			d1.attachDisk(0, r, dt1).applyImpulse(0.0, -rotImp);
			d1.applyImpulse(0.0, rotImp);
			d2 = env.newRootDisk((i + 1) * sizef - 1, 2.5 + off, r, dt2);
			d2.attachDisk(0, r, dt2).applyImpulse(impx, 0.0);
			d2.applyImpulse(impx, 0.0);
		}
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
		DemoLauncher.runDemo(new Rotation());
	}

}