/**
 * 
 */
package org.cagrid.installer.tasks;

import java.beans.PropertyChangeListener;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.Condition;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class ConditionalTask implements Condition, Task {
	
	private Task task;
	private Condition condition;

	public ConditionalTask(Task task){
		this(task, new Condition(){

			public boolean evaluate(WizardModel arg0) {
				return true;
			}
			
		});
	}
	
	public ConditionalTask(Task task, Condition condition){
		this.task = task;
		this.condition = condition;
	}

	/* (non-Javadoc)
	 * @see org.pietschy.wizard.models.Condition#evaluate(org.pietschy.wizard.WizardModel)
	 */
	public boolean evaluate(WizardModel model) {
		return this.condition.evaluate(model);
	}

	/* (non-Javadoc)
	 * @see org.cagrid.installer.tasks.Task#execute(java.util.Map)
	 */
	public Object execute(CaGridInstallerModel model) throws Exception {
		return this.task.execute(model);
	}

	/* (non-Javadoc)
	 * @see org.cagrid.installer.tasks.Task#getDescription()
	 */
	public String getDescription() {
		return this.task.getDescription();
	}

	/* (non-Javadoc)
	 * @see org.cagrid.installer.tasks.Task#getName()
	 */
	public String getName() {
		return this.task.getName();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		this.task.addPropertyChangeListener(l);
	}

	public int getLastStep() {
		return this.task.getLastStep();
	}

	public int getStepCount() {
		return this.task.getStepCount();
	}

	public void setLastStep(int lastStep) {
		this.task.setLastStep(lastStep);
	}

	public void setStepCount(int count) {
		this.task.setStepCount(count);
	}

	public boolean isAbortOnError() {
		return this.task.isAbortOnError();
	}

}
