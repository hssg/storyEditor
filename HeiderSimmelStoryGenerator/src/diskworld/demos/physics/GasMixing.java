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

import diskworld.DiskComplex;
import diskworld.DiskMaterial;
import diskworld.DiskType;
import diskworld.Environment;
import diskworld.ObjectConstructor;
import diskworld.demos.DemoLauncher;
import diskworld.demos.PhysicsDemo;
import diskworld.environment.Wall;
import diskworld.linalg2D.Line;
import diskworld.linalg2D.Point;

public class GasMixing extends PhysicsDemo {

	private Environment env;
	private Wall wall;
	public static double TEMP_FACTOR = 1.0;

	@Override
	public String getTitle() {
		return "Gas Mixing";
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 20;
		int sizey = 10;
		env = new Environment(sizex, sizey);

		double walloff = Environment.DEFAULT_WALL_THICKNESS * 0.5;
		wall = new Wall(new Line(new Point(10, walloff), new Point(10, 10 - walloff)), 0.1);
		env.canAddWall(wall);
		double r1 = 0.1;
		double r2 = 0.07;
		DiskType dt1 = new DiskType(DiskMaterial.RUBBER.withColor(Color.BLUE));
		DiskType dt2 = new DiskType(DiskMaterial.RUBBER.withColor(Color.RED));
		ObjectConstructor oc1 = env.createObjectConstructor();
		oc1.setRoot(r1, dt1);
		ObjectConstructor oc2 = env.createObjectConstructor();
		oc2.setRoot(r2, dt2);
		oc2.addItem(0, 0, 0, r2, dt2);

		double impulse1 = 0.05 * TEMP_FACTOR;
		Random rand = new Random(1);
		DiskComplex dc;
		for (int i = 0; i < 100; i++) {
			do {
				double x = rand.nextDouble() * 9 + 0.5;
				double y = rand.nextDouble() * 9 + 0.5;
				double a = rand.nextDouble() * Math.PI * 2;
				dc = oc1.createDiskComplex(x, y, a, 1);
			} while (dc == null);
			dc.applyImpulse(rand.nextGaussian() * impulse1, rand.nextGaussian() * impulse1, 0.0);
		}
		double impulse2 = 0.02 * TEMP_FACTOR;
		double angularimpulse2 = 0.001;
		for (int i = 0; i < 100; i++) {
			do {
				double x = rand.nextDouble() * 9 + 10.5;
				double y = rand.nextDouble() * 9 + 0.5;
				double a = rand.nextDouble() * Math.PI * 2;
				dc = oc2.createDiskComplex(x, y, a, 1);
			} while (dc == null);
			dc.applyImpulse(rand.nextGaussian() * impulse2, rand.nextGaussian() * impulse2, rand.nextGaussian() * angularimpulse2);
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
		DemoLauncher.runDemo(new GasMixing());
	}

}