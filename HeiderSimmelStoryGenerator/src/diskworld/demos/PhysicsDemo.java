package diskworld.demos;

import diskworld.demos.DemoLauncher.Demo;
import diskworld.environment.AgentMapping;
import diskworld.interfaces.AgentController;
import diskworld.visualization.VisualizationOptions;
import diskworld.visualization.VisualizationSettings;

public abstract class PhysicsDemo implements Demo {

	@Override
	public long getMiliSecondsPerTimeStep() {
		return 40;
	}

	@Override
	public AgentMapping[] getAgentMappings() {
		return new AgentMapping[0];
	}

	@Override
	public AgentController[] getControllers() {
		return new AgentController[0];
	}

	@Override
	public boolean adaptVisualisationSettings(VisualizationSettings settings) {
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_TIME).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_SKELETON).setEnabled(false);
		settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_COLLISIONS).setEnabled(false);
		return true;
	}

	@Override
	public void afterTimeStep() {
	}

}
