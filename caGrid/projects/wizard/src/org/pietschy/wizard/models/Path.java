package org.pietschy.wizard.models;

import java.util.ArrayList;

import org.pietschy.wizard.WizardStep;

public interface Path {

	/**
	 * Adds a wizard step to this path.  Paths must contain at least one step, and the steps
	 * will be traversed in the order they are added.
	 * @param step the next {@link WizardStep} in the path.
	 */
	void addStep(WizardStep step);

	WizardStep firstStep();

	WizardStep nextStep(WizardStep currentStep);

	WizardStep previousStep(WizardStep currentStep);

	WizardStep lastStep();

	/**
	 * Checks if the specified step is the first step in the path.
	 * @param step the step to check
	 * @return <tt>true</tt> if the step is the first in the path, <tt>false</tt> otherwise.
	 */
	boolean isFirstStep(WizardStep step);

	/**
	 * Checks if the specified step is the last step in the path.
	 * @param step the step to check
	 * @return <tt>true</tt> if the step is the last in the path, <tt>false</tt> otherwise.
	 */
	boolean isLastStep(WizardStep step);

	ArrayList getSteps();

	boolean contains(WizardStep step);

	void acceptVisitor(PathVisitor visitor);

}