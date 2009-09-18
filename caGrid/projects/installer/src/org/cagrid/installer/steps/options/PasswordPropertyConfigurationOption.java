/**
 * 
 */
package org.cagrid.installer.steps.options;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class PasswordPropertyConfigurationOption extends
		TextPropertyConfigurationOption {

	/**
	 * 
	 */
	public PasswordPropertyConfigurationOption() {

	}

	/**
	 * @param name
	 * @param description
	 * @param defaultValue
	 */
	public PasswordPropertyConfigurationOption(String name, String description,
			String defaultValue) {
		super(name, description, defaultValue);

	}

	/**
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 */
	public PasswordPropertyConfigurationOption(String name, String description,
			String defaultValue, boolean required) {
		super(name, description, defaultValue, required);

	}

}
