/**
 * 
 */
package org.cagrid.installer.tasks;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public abstract class AbstractTask implements Task {
	
	private String name;
	private String description;
	private int stepCount = 1;
	private int lastStep = 0;
	private boolean abortOnError = true;
	private PropertyChangeSupport psc;
	
	private static final Log logger = LogFactory.getLog(AbstractTask.class);

	public int getLastStep() {
		return lastStep;
	}

	public void setLastStep(int lastStep) {
		int oldLastStep = this.lastStep;
		this.lastStep = lastStep;
		this.psc.firePropertyChange("lastStep", oldLastStep, lastStep);
	}

	public int getStepCount() {
		return stepCount;
	}

	public void setStepCount(int stepCount) {
		int oldStepCount = this.stepCount;
		this.stepCount = stepCount;
		this.psc.firePropertyChange("stepCount", oldStepCount, stepCount);		
	}

	/**
	 * 
	 */
	public AbstractTask(String name, String description) {
		this.name = name;
		this.description = description;
		this.psc = new PropertyChangeSupport(this);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		this.psc.addPropertyChangeListener(l);
	}

	public boolean isAbortOnError() {
		return abortOnError;
	}

	public void setAbortOnError(boolean abortOnError) {
		this.abortOnError = abortOnError;
	}

}
