/**
 * 
 */
package org.cagrid.installer.tasks.cagrid;


/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class ConfigureTargetGridTask extends CaGridAntTask {

	/**
	 * @param name
	 * @param description
	 * @param targetName
	 */
	public ConfigureTargetGridTask(String name, String description) {
		super(name, description, "configure");
	}
}
