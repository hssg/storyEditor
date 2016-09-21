package diskworld.environment;

import diskworld.Disk;
import diskworld.interfaces.GravityModel;

public class ConstantGravityModel implements GravityModel {

	private double gravityConstant;
	private int timeStepsCycle;
	private int counter;

	public ConstantGravityModel(double gravityConstant, int numTimeStepsWithoutGravity) {
		this.timeStepsCycle = numTimeStepsWithoutGravity+1;
		this.gravityConstant = gravityConstant*this.timeStepsCycle;
		this.counter = 0;
	}
	
	public ConstantGravityModel(double gravityConstant) {
		this(gravityConstant,0);
	}

	@Override
	public double[] getGravityForce(Disk disk) {
		double mass = disk.getMass();
		return new double[] { 0, counter == 0 ? -gravityConstant * mass : 0 };
	}

	@Override
	public void nextTimeStep(double dt) {
		counter = (counter+1)%timeStepsCycle;
	}
}
