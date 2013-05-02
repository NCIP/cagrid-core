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
