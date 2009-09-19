package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

public class SleepStep extends Step {

	private long seconds;

	public SleepStep(long seconds) {
		this.seconds = seconds;
	}

	public void runStep() throws Throwable {
		Thread.sleep((seconds * 1000));
	}
}
