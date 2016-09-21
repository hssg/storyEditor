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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import diskworld.Environment;
import diskworld.environment.AgentMapping;
import diskworld.interfaces.AgentController;
import diskworld.visualization.EnvironmentPanel;
import diskworld.visualization.VisualizationSettings;

public class DemoLauncher {

	public static interface Demo {

		/**
		 * Determines the real time factor of the visualisation. Note this has no effect on the
		 * delta_t that is added to the simulation time stamp in every time step!
		 * 
		 * @return how manz miliseconds shall pass (in real time) between executing two time steps
		 */
		long getMiliSecondsPerTimeStep();

		/**
		 * Creates and populates the simulation environment
		 * 
		 * @return environment including the agent's DiskComplexes
		 */
		Environment getEnvironment();

		/**
		 * Provide an array of AgentMapping objects, that provide the connection between the sensor array (used in the AgentController interface) and the
		 * actual disk world sensors, and between the actuator array (used in the AgentController interface) and the actual disk world actions&actuators
		 * Note: Since each agent uses different disks in the disk world, all AgentMapping objects must be different, even
		 * if some agents use the same controller
		 * 
		 * @return array of AgentMapping objects, one for each agent in the simulation
		 */
		AgentMapping[] getAgentMappings();

		/**
		 * Provide the controllers, that get sensor values and produce actuator values.
		 * Note: It is possible that agents share a controller (if the Controller implementation allows that). In that case,
		 * there may be multiple copies of the same AgentController object in the array returned. However, the length of the
		 * array is determined by the number of agents in the simulation (and must hence be identical to the length of
		 * the array returned by {@link #getAgentMappings()}.
		 * 
		 * @return array of AgentMapping objects, one for each agent in the simulation
		 */
		AgentController[] getControllers();

		/**
		 * A descriptive title of the demo
		 * 
		 * @return some human readable brief description
		 */
		String getTitle();

		/**
		 * May be used to change visualization settings.
		 * NOTE: The VisualizationOptions are created on the fly during painting, which means that they are not
		 * yet generated
		 * 
		 * @param settings
		 *            object passed in order to apply changes to the visualization defaults
		 * @return
		 *         true if the settings could be adapted
		 */
		boolean adaptVisualisationSettings(VisualizationSettings settings);

		void afterTimeStep();

	}

	private static final int PANEL_SIZE_Y = 700;
	private static final int FRAME_SIZE_INC_X = 20;
	private static final int FRAME_SIZE_INC_Y = 100;
	public static double DT_PER_STEP = 0.04;

	/**
	 * Runs a sequence of demos
	 * 
	 * @param title
	 *            title of the window
	 * @param demos
	 *            array of demos to show
	 */
	public static void runDemos(String title, Demo demos[]) {
		JFrame frame = new JFrame();
		frame.setTitle(title);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for (Demo demo : demos) {
			DemoLauncher dl = new DemoLauncher(demo, DT_PER_STEP, 0.0);
			dl.runDemo(frame);
		}
		frame.dispose();
	}

	/**
	 * Runs a single demo for a certain time
	 * 
	 * @param demo
	 *            demo to show
	 * @param endTime
	 *            time to stop demo (0.0 means indefinite)
	 */
	public static void runDemo(Demo demo, double endTime) {
		JFrame frame = new JFrame();
		frame.setTitle(demo.getTitle());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DemoLauncher dl = new DemoLauncher(demo, DT_PER_STEP, endTime);
		dl.runDemo(frame);
		frame.dispose();
	}

	/**
	 * Runs a single demo, infinitely long
	 * 
	 * @param demo
	 *            demo to show
	 */
	public static void runDemo(Demo demo) {
		runDemo(demo, 0.0);
	}

	private boolean settingsAdapted;
	private Environment env;
	private Demo demo;
	private AgentMapping[] mappings;
	private AgentController[] controllers;
	private AtomicBoolean running;
	private double endtime;
	private double dt;

	public DemoLauncher(Demo demo, double dt, double endTime) {
		this.demo = demo;
		env = demo.getEnvironment();
		running = new AtomicBoolean(true);
		controllers = demo.getControllers();
		mappings = demo.getAgentMappings();
		if (controllers == null)
			controllers = new AgentController[0];
		if (mappings == null)
			mappings = new AgentMapping[0];
		if (controllers.length != mappings.length)
			throw new IllegalArgumentException("length of arrays returned by getControllers() and getAgentMappings() do not agree!");
		settingsAdapted = false;
		this.endtime = endTime;
		this.dt = dt;
	}

	public int getSecond() {
		return (int) env.getTime();
	}

	/**
	 * Run the given demo in the frame
	 * 
	 * @param frame
	 *            frame to show the demo
	 * @param demo
	 *            the demo to be run
	 * @param endTime
	 *            stopping time of the demo, 0: never stop
	 */
	private void runDemo(JFrame frame) {
		EnvironmentPanel ep = new EnvironmentPanel();
		ep.setEnvironment(env);
		ep.getSettings().setFullView(env);
		JPanel panel = new JPanel();
		int sizey = PANEL_SIZE_Y;
		int sizex = (int) Math.round(sizey * env.getMaxX() / env.getMaxY());
		frame.setSize(new Dimension(sizex + FRAME_SIZE_INC_X, sizey + FRAME_SIZE_INC_Y));
		frame.add(panel);
		JLabel label = new JLabel(demo.getTitle());
		label.setFont(new Font(Font.SERIF, Font.BOLD, 36));
		panel.add(label);
		ep.setPreferredSize(new Dimension(sizex, sizey));
		panel.add(ep);
		frame.add(panel);
		frame.validate();
		frame.addKeyListener(getKeyListener(running));
		showNames(demo.getTitle(), mappings);
		long miliSecondsPerStep = demo.getMiliSecondsPerTimeStep();
		while (running.get()) {
			doTimeStep(ep.getSettings());
			long ts = System.currentTimeMillis();
			long time = System.currentTimeMillis() - ts;
			long sleep = miliSecondsPerStep - time;
			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
				}
			}
		}
		panel.remove(ep);
	}

	public void doTimeStep(VisualizationSettings vs) {
		if (!settingsAdapted) {
			settingsAdapted = demo.adaptVisualisationSettings(vs);
		}
		env.doTimeStep(dt, mappings);
		for (int i = 0; i < controllers.length; i++) {
			controllers[i].doTimeStep(mappings[i].getSensorValues(), mappings[i].getActuatorValues());
		}
		if ((endtime > 0.0) && (env.getTime() >= endtime)) {
			running.set(false);
		}
		demo.afterTimeStep();
	}

	private static void showNames(String title, AgentMapping[] mappings) {
		System.out.println(title);
		System.out.println();
		for (int i = 0; i < mappings.length; i++) {
			System.out.print("Agent " + i + " sensors:");
			for (String s : mappings[i].getSensorNames()) {
				System.out.print(" " + s);
			}
			System.out.println();
			System.out.print("Agent " + i + " actuators:");
			for (String s : mappings[i].getActuatorNames()) {
				System.out.print(" " + s);
			}
			System.out.println();
		}
		System.out.println();
	}

	private static KeyListener getKeyListener(final AtomicBoolean running) {
		return new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char key = e.getKeyChar();
				if (key == ' ')
					running.set(false);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		};
	}

	public boolean isRunning() {
		return running.get();
	}

}
