/**
 * 
 */
package org.cagrid.installer.steps.options;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public abstract class AbstractPropertyConfigurationOption implements
		PropertyConfigurationOption {
	
	private String name;
	private String description;
	private boolean required;
	
	public AbstractPropertyConfigurationOption(){
		
	}
	
	public AbstractPropertyConfigurationOption(String name, String description){
		this(name, description, false);
	}
	
	public AbstractPropertyConfigurationOption(String name, String description, boolean required){
		this.name = name;
		this.description = description;
		this.required = required;
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
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}


}
