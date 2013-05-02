/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
