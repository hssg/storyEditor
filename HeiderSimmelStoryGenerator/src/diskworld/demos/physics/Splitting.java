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
import java.util.Random;

import diskworld.Disk;
import diskworld.DiskComplex;
import diskworld.DiskMaterial;
import diskworld.DiskType;
import diskworld.Environment;
import diskworld.demos.DemoLauncher;
import diskworld.demos.PhysicsDemo;
import diskworld.interfaces.CollidableObject;
import diskworld.interfaces.CollisionEventHandler;
import diskworld.linalg2D.Point;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class Splitting extends PhysicsDemo {

	public static long RAND_SEED = 2;

	@Override
	public String getTitle() {
		return "Splitting";
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 10;
		int sizey = 10;
		final Environment env = new Environment(sizex, sizey);

		DiskType dt1 = new DiskType(DiskMaterial.RUBBER.withColor(Color.BLUE));
		double r = 0.1;

		int len = 19;
		int width = 4;
		int num = 7;

		Disk disks1[] = new Disk[len];
		Disk disks2[][] = new Disk[num][width];
		Disk disks3[] = new Disk[len];
		disks1[0] = env.newRootDisk(3, 6, r, 0, dt1);
		DiskComplex dc = disks1[0].getDiskComplex();
		for (int i = 1; i < len; i++) {
			disks1[i] = disks1[i - 1].attachDisk(0, r, dt1);
		}
		for (int i = 0; i < num; i++) {
			int k = (i * (len - 1)) / (num - 1);
			disks2[i][0] = disks1[k];
			for (int j = 1; j < width; j++) {
				disks2[i][j] = disks2[i][j - 1].attachDisk(j == 1 ? Math.PI / 2 : 0, r, dt1);
			}
			disks3[k] = disks2[i][width - 1];
		}
		boolean rot = true;
		for (int i = 0; i < len; i++) {
			if (disks3[i] == null) {
				disks3[i] = disks3[i - 1].attachDisk(rot ? -Math.PI / 2 : 0, r, dt1);
				rot = false;
			} else {
				if (i > 0) {
					dc.addSkeletonEdge(disks3[i - 1], disks3[i]);
				}
				rot = true;
			}
		}
		disks1[0].applyImpulse(1, 0.3);

		DiskType dt2 = new DiskType(DiskMaterial.RUBBER.withColor(Color.RED));

		int numSplitter = 5;
		Random rnd = new Random(RAND_SEED);
		double impulse = 0.005;
		for (int i = 0; i < numSplitter; i++) {
			Disk d = env.newRootDisk(rnd.nextDouble() * 8 + 1, rnd.nextDouble() * 8 + 1, 0.05, dt2);
			d.applyImpulse(impulse * rnd.nextGaussian(), impulse * rnd.nextGaussian());
			d.addEventHandler(new CollisionEventHandler() {
				@Override
				public void collision(CollidableObject collidableObject, Point collisionPoint, double exchangedImpulse) {
					if (collidableObject instanceof Disk) {
						env.deleteDisk((Disk) collidableObject);
					}
				}
			});
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
		DemoLauncher.runDemo(new Splitting());
	}

}