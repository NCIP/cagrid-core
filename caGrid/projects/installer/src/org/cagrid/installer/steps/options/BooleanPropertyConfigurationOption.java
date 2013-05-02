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
public class BooleanPropertyConfigurationOption extends
		AbstractPropertyConfigurationOption {

	private boolean defaultValue;

	public BooleanPropertyConfigurationOption() {

	}
	
	public BooleanPropertyConfigurationOption(String name, String description,
			boolean defaultValue) {
		super(name, description, false);
		this.defaultValue = defaultValue;
	}

	public BooleanPropertyConfigurationOption(String name, String description,
			boolean defaultValue, boolean required) {
		super(name, description, required);
		this.defaultValue = defaultValue;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

}
