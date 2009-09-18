/**
 * 
 */
package org.cagrid.installer.steps.options;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class ListPropertyConfigurationOption extends
		AbstractPropertyConfigurationOption {

	private LabelValuePair[] choices;

	public ListPropertyConfigurationOption() {

	}
	
	public ListPropertyConfigurationOption(String name, String description,
			String[] choices) {
		this(name, description, choices, false);
	}
	
	public ListPropertyConfigurationOption(String name, String description,
			String[] strChoices, boolean required) {
		super(name, description, false);
		LabelValuePair[] pairs = new LabelValuePair[strChoices.length];
		for(int i = 0; i < strChoices.length; i++){
			pairs[i] = new LabelValuePair(strChoices[i], strChoices[i]);
		}
		this.choices = pairs;
	}

	public ListPropertyConfigurationOption(String name, String description,
			LabelValuePair[] choices) {
		super(name, description, false);
		this.choices = choices;
	}

	public ListPropertyConfigurationOption(String name, String description,
			LabelValuePair[] choices, boolean required) {
		super(name, description, required);
		this.choices = choices;
	}

	public LabelValuePair[] getChoices() {
		return choices;
	}

	public void setChoices(LabelValuePair[] choices) {
		this.choices = choices;
	}

	public static class LabelValuePair {
		private String label;

		private String value;

		public LabelValuePair(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return this.label;
		}

		public String getValue() {
			return this.value;
		}
		
		public String toString(){
			return getLabel();
		}

	}

}
