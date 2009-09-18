/**
 * 
 */
package org.cagrid.installer.steps.options;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class TextPropertyConfigurationOption extends
		AbstractPropertyConfigurationOption {

	private String defaultValue;
	
	public TextPropertyConfigurationOption(){
		
	}
	
	public TextPropertyConfigurationOption(String name, String description, String defaultValue){
		super(name, description, false);
		this.defaultValue = defaultValue;
	}
	
	public TextPropertyConfigurationOption(String name, String description, String defaultValue, boolean required){
		super(name, description, required);
		this.defaultValue = defaultValue;
	}


	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
