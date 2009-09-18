/**
 * 
 */
package org.cagrid.installer.tasks;

import java.beans.PropertyChangeListener;

import org.cagrid.installer.model.CaGridInstallerModel;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public interface Task {
	
	String getName();
	String getDescription();
	Object execute(CaGridInstallerModel model) throws Exception;
	int getStepCount();
	int getLastStep();
	void setStepCount(int count);
	void setLastStep(int lastStep);
	void addPropertyChangeListener(PropertyChangeListener l);
	boolean isAbortOnError();
	
}
