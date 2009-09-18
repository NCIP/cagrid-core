/**
 * 
 */
package org.cagrid.installer.tasks;

import org.cagrid.installer.model.CaGridInstallerModel;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public abstract class BasicTask extends AbstractTask {

	/**
	 * @param name
	 * @param description
	 */
	public BasicTask(String name, String description) {
		super(name, description);
	}


	/* (non-Javadoc)
	 * @see org.cagrid.installer.tasks.Task#execute(java.util.Map)
	 */
	public Object execute(CaGridInstallerModel model) throws Exception {
		Object result = internalExecute(model);
		setLastStep(getStepCount());
		return result;
	}
	
	protected abstract Object internalExecute(CaGridInstallerModel model) throws Exception;
}
