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
import diskworld.ObjectConstructor;
import diskworld.demos.DemoLauncher;
import diskworld.demos.PhysicsDemo;
import diskworld.environment.Floor;
import diskworld.environment.FloorCellType;
import diskworld.grid.Grid;
import diskworld.interfaces.GravityModel;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class Floating extends PhysicsDemo {

	@Override
	public String getTitle() {
		return "Floating";
	}

	@Override
	public Environment getEnvironment() {
		// create environment
		int sizex = 10;
		int sizey = 10;
		Environment env = new Environment(sizex, sizey);
		Floor floor = env.getFloor();
		FloorCellType fct = new FloorCellType(0.3, Color.BLUE);
		for (int i = 0; i < floor.getMaxX(); i++) {
			for (int j = 0; j < 5; j++) {
				floor.setType(i, j, fct);
			}
		}
		env.setStokesFriction();
		env.getPhysicsParameters().setGravityModel(getFloatingGravityModel(env.getFloor(), env.getFloorGrid()));

		DiskMaterial dm1 = new DiskMaterial(1.2, 0.1, 0.1, 0.5, Color.RED);
		DiskType dt1 = new DiskType(dm1);
		DiskMaterial dm2 = new DiskMaterial(0.4, 0.1, 0.4, 0.5, Color.GREEN);
		DiskType dt2 = new DiskType(dm2);

		double r = 0.1;
		ObjectConstructor oc1 = env.createObjectConstructor();
		oc1.setRoot(r, dt1);
		int num = 10;
		for (int i = 1; i < num; i++) {
			oc1.addItem(i - 1, (((i / 2) % 2) * 2 - 1) * Math.PI / 2, 0, r, dt1);
		}
		double rotangle = 0.7;
		oc1.createObject(4.5, 7, rotangle, 1);

		ObjectConstructor oc2 = env.createObjectConstructor();
		oc2.setRoot(r, dt2);
		int sign = 1;
		oc2.addItem(-1, Math.toRadians(60), 0, r, dt2);
		oc2.addItem(-1, Math.toRadians(120), 0, r, dt2);
		for (int i = 1; i <= 5; i++) {
			sign = -sign;
			oc2.addItem(-1, Math.toRadians(sign * 60), 0, r, dt2);
			oc2.addItem(-1, Math.toRadians(sign * 120), 0, r, dt2);
			for (int j = 0; j < i; j++) {
				oc2.addItem(-1, 0, 0, r, dt2);
			}
		}
		oc2.createObject(8.5, 4.4, 0.1, 1);

		ObjectConstructor oc3 = env.createObjectConstructor();
		oc3.setRoot(r, dt1);
		for (int i = 1; i < num; i++) {
			oc3.addItem(i - 1, (((i / 2) % 2) * 2 - 1) * Math.PI / 2, 0, r, i < 4 ? dt1 : dt2);
		}
		oc3.createObject(6, 7, rotangle, 1);

		Disk d1 = env.newRootDisk(0.5, 6, 0.1, dt1);
		Disk d2 = env.newRootDisk(1.5, 6, 0.2, dt1);
		Disk d3 = env.newRootDisk(2.5, 9, 0.1, dt2);
		Disk d4 = env.newRootDisk(3.5, 9, 0.2, dt2);
		return env;
	}

	private GravityModel getFloatingGravityModel(final Floor floor, final Grid floorGrid) {
		return new GravityModel() {
			@Override
			public double[] getGravityForce(Disk disk) {
				int x = floorGrid.getCellxIndex(disk.getX());
				int y = floorGrid.getCellyIndex(disk.getY());
				double mass = disk.getMass();
				double r = disk.getRadius();
				double volume = Math.PI * r * r;
				double gravityConstant = 1;
				FloorCellType fct = floor.getType(x, y);
				double densityFluid = (fct.getTypeIndex() == 0) ? 0 : 1;
				return new double[] { 0, gravityConstant * (-mass + densityFluid * volume) };
			}

			@Override
			public void nextTimeStep(double dt) {
			}
		};
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COLLISIONS).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		return true;
	}

	public static void main(String[] args) {
		DemoLauncher.runDemo(new Floating());
	}

}