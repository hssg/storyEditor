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
package diskworld.demovideos;

import diskworld.Environment;
import diskworld.demos.DemoLauncher;
import diskworld.demos.DemoLauncher.Demo;
import diskworld.demos.agents.AffordanceGripper;
import diskworld.environment.AgentMapping;
import diskworld.interfaces.AgentController;
import diskworld.interfaces.MovieMaker;
import diskworld.visualization.QuicktimeMovieMaker;
import diskworld.visualization.VideoCapturer;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public class AgentVideos {

	public static class DemoWrapper implements Demo {

		private VideoCapturer capturer;
		private Demo demo;

		public DemoWrapper(VideoCapturer vc, Demo demo) {
			this.capturer = vc;
			this.demo = demo;
		}

		@Override
		public long getMiliSecondsPerTimeStep() {
			return demo.getMiliSecondsPerTimeStep();
		}

		@Override
		public Environment getEnvironment() {
			Environment env = demo.getEnvironment();
			capturer.capture(env);
			return env;
		}

		@Override
		public AgentMapping[] getAgentMappings() {
			return demo.getAgentMappings();
		}

		@Override
		public AgentController[] getControllers() {
			return demo.getControllers();
		}

		@Override
		public String getTitle() {
			return demo.getTitle();
		}

		@Override
		public boolean adaptVisualisationSettings(VisualizationSettings settings) {
			return demo.adaptVisualisationSettings(settings);
		}

		@Override
		public void afterTimeStep() {
			demo.afterTimeStep();
		}

	}

	private static final int SIZEX = 900;
	private static final int SIZEY = 900;
	private static final int FRAME_RATE = 25; // frames per second
	private static int DROP_FRAMES = 0; // show every frame
	private static final String PATH = "./demoVideos/agents/";

	public static void record(String name, Class<? extends Demo> demoClass, double startTime, double endtime) {
		MovieMaker mm = new QuicktimeMovieMaker(PATH + name + ".mov");
		try {
			System.out.print("Recording: " + name);
			Demo demo = demoClass.newInstance();
			double sizex = demo.getEnvironment().getMaxX();
			double sizey = demo.getEnvironment().getMaxY();
			final VisualizationSettings vs = new VisualizationSettings();
			vs.setViewDimension(SIZEX, SIZEY);
			vs.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
			double scalex = sizex / SIZEX;
			double scaley = sizey / SIZEY;
			double scale = Math.max(scalex, scaley);
			double newsizex = scale * SIZEX;
			double newsizey = scale * SIZEY;
			double offsetx = -(newsizex - sizex) * 0.5;
			double offsety = -(newsizey - sizey) * 0.5;
			vs.setViewedRect(offsetx, offsety, offsetx + newsizex, offsety + newsizey);
			demo.adaptVisualisationSettings(vs);
			final VideoCapturer vc = new VideoCapturer(mm, vs, FRAME_RATE, DROP_FRAMES, startTime);
			DemoWrapper wrapper = new DemoWrapper(vc, demo);
			recordDemo(wrapper, endtime, vs);
			vc.done();
			System.out.println(" done");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static void recordDemo(Demo demo, double endtime, VisualizationSettings vs) {
		int lastSec = 0;
		DemoLauncher dl = new DemoLauncher(demo, DemoLauncher.DT_PER_STEP, endtime);
		while (dl.isRunning()) {
			dl.doTimeStep(vs);
			if (dl.getSecond() > lastSec) {
				lastSec = dl.getSecond();
				System.out.print(".");
			}
		}
	}

	public static void main(String[] args) {
		//record("Follower", Follower.class, 0.0, 20.0);
		//record("Braitenberg", BraitenbergVehicle.class, 0.0, 20.0);
		DemoLauncher.DT_PER_STEP = 0.01;
		DROP_FRAMES = 9;
		record("Affordance2", AffordanceGripper.class, 0.0, 60.0);
		//record("Octopus", Octopus.class, 0.0, 20.0);
	}
}
