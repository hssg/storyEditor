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
import diskworld.DiskComplex;
import diskworld.DiskMaterial;
import diskworld.DiskType;
import diskworld.Environment;
import diskworld.ObjectConstructor;
import diskworld.demos.DemoLauncher;
import diskworld.demos.PhysicsDemo;
import diskworld.environment.FloorCellType;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class DifferentZLevels extends PhysicsDemo {

	@Override
	public String getTitle() {
		return "z-Levels";
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 20;
		int sizey = 20;
		Environment env = new Environment(sizex, sizey);
		DiskMaterial dm = new DiskMaterial(0.8, 1, 0.0, 0.5, Color.RED);

		// provide disk types 
		DiskType diskType1 = new DiskType(dm.withColor(Color.GREEN));
		DiskType diskType2 = new DiskType(dm.withColor(Color.RED));

		FloorCellType fct = new FloorCellType(0.2, Color.BLUE);
		env.getFloor().fill(fct);

		//construct DiskComplexes with Disks at two different zLevels
		ObjectConstructor oc1 = env.createObjectConstructor();
		oc1.setRoot(0.5, diskType1);
		oc1.addItem(0, 0, 0, 0.5, diskType2);

		ObjectConstructor oc2 = env.createObjectConstructor();
		oc2.setRoot(0.4, diskType2);

		ObjectConstructor oc3 = env.createObjectConstructor();
		oc3.setRoot(0.4, diskType1);

		double imp = 4;
		int numDisks = 5;
		for (int i = 0; i < 1; i++) {
			double off = 1;
			DiskComplex dc = oc1.createDiskComplex(env.getMaxX() / 2 + off, env.getMaxY() / 2 + off, 0, 1);
			if (dc != null) {
				Disk d1 = dc.getDisks().get(0);
				Disk d2 = dc.getDisks().get(1);
				d1.setZLevel(2);
				d2.setZLevel(1);
				d1.applyImpulse(0.1, 0.1);
				d2.applyImpulse(0.1, 0.1);
			}
		}
		for (int i = 0; i < numDisks; i++) {
			DiskComplex dc = oc2.createDiskComplex(env.getMaxX() * (i + 1) / (numDisks + 1), 1, 0, 1);
			if (dc != null) {
				Disk d1 = dc.getDisks().get(0);
				d1.setZLevel(1);
				d1.applyImpulse(0, imp);
			}
		}
		for (int i = 0; i < numDisks; i++) {
			DiskComplex dc = oc3.createDiskComplex(1, env.getMaxY() * (i + 1) / (numDisks + 1), 0, 1);
			if (dc != null) {
				Disk d1 = dc.getDisks().get(0);
				d1.setZLevel(2);
				d1.applyImpulse(imp, 0);
			}
		}

		return env;
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		//settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SHADOW).setEnabled(false);
		return true;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new DifferentZLevels());
	}

}