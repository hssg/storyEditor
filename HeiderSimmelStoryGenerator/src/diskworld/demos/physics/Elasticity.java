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

public class Elasticity extends PhysicsDemo {

	@Override
	public String getTitle() {
		return "Collisions";
	}

	@Override
	public Environment getEnvironment() {
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

		double r1 = 0.2;
		double r2 = 0.2;
		double off_factor = 0.04;
		double impx1 = 0.1;
		double impx2 = -impx1 * r2 * r2 / (r1 * r1);

		double elasticity1 = 0.05;
		double elasticity2 = 0.9;

		DiskMaterial dm1 = new DiskMaterial(1, elasticity1, 0, 0, Color.WHITE);
		DiskMaterial dm2 = new DiskMaterial(1, elasticity2, 0, 0, Color.WHITE);
		DiskType dt1 = new DiskType(dm1.withColor(Color.BLUE));
		DiskType dt2 = new DiskType(dm1.withColor(Color.GREEN));
		DiskType dt3 = new DiskType(dm2.withColor(Color.BLUE));
		DiskType dt4 = new DiskType(dm2.withColor(Color.GREEN));

		for (int i = 0; i < num; i++) {
			double off = i * off_factor;
			env.newRootDisk(i * sizef + 1, 2.5 - off, r1, dt1).applyImpulse(impx1, 0.0);
			env.newRootDisk((i + 1) * sizef - 1, 2.5 + off, r1, dt2).applyImpulse(impx2, 0.0);

			env.newRootDisk(i * sizef + 1, 7.5 - off, r1, dt3).applyImpulse(impx1, 0.0);
			env.newRootDisk((i + 1) * sizef - 1, 7.5 + off, r1, dt4).applyImpulse(impx2, 0.0);
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
		DemoLauncher.runDemo(new Elasticity());
	}

}