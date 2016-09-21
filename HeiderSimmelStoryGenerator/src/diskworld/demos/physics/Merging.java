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
import diskworld.DiskMaterial;
import diskworld.DiskType;
import diskworld.Environment;
import diskworld.demos.DemoLauncher;
import diskworld.demos.PhysicsDemo;
import diskworld.environment.Wall;
import diskworld.interfaces.CollidableObject;
import diskworld.interfaces.CollisionEventHandler;
import diskworld.linalg2D.Point;

public class Merging extends PhysicsDemo {

	public static long RAND_SEED = 2;
	private Environment env;
	private Wall wall;

	@Override
	public String getTitle() {
		return "Merging";
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 10;
		int sizey = 10;
		env = new Environment(sizex, sizey);
		double radius = 0.03;
		DiskType dt = new DiskType(DiskMaterial.METAL.withColor(Color.YELLOW));

		double impulse = 0.002;
		Random rand = new Random(RAND_SEED);
		for (int i = 0; i < 1000; i++) {
			Disk d;
			double x;
			double y;
			do {
				x = 0.5 + rand.nextDouble() * 9;
				y = 0.5 + rand.nextDouble() * 9;
				d = env.newRootDisk(x, y, radius, 0, dt);
			} while (env.withdrawDueToCollisions(d.getDiskComplex()));
			final Disk dfinal = d;
			dfinal.addEventHandler(new CollisionEventHandler() {
				@Override
				public void collision(final CollidableObject otherDisk, Point collisionPoint, double exchangedImpulse) {
					if (otherDisk instanceof Disk) {
						env.merge(dfinal, (Disk) otherDisk);
					}
				}
			});
			double dx = 5 - x;
			double dy = 5 - y;
			double dist = Math.sqrt(dx * dx + dy * dy);
			double f = impulse / dist;
			d.getDiskComplex().applyImpulse((1.0 + rand.nextGaussian()) * dx * f, (1.0 + rand.nextGaussian()) * dy * f, x, y);
		}
		return env;
	}

	@Override
	public void afterTimeStep() {
		if ((wall != null) && (env.getTime() > 5)) {
			env.removeWall(wall);
			wall = null;
		}
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new Merging());
	}

}