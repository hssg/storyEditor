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
package diskworld.demos;

import diskworld.demos.DemoLauncher.Demo;
import diskworld.demos.agents.BraitenbergVehicle;
import diskworld.demos.agents.Follower;
import diskworld.demos.agents.Octopus;
import diskworld.demos.agents.RobotHand;

public class DemoAgents {

	public static void main(String[] args) {
		Demo[] demos = new Demo[] {
				new Octopus(),
				new Snake(),
				new TypesOfDiskActions(),
				new RobotArm(),
				new RobotHitsBall(),
				new RobotHand(),
				new Follower(),
				new BraitenbergVehicle()
		};
		DemoLauncher.runDemos("DiskWorld Agents Demonstration", demos);
	}

}