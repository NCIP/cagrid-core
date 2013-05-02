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
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class FilePropertyConfigurationOption extends
		TextPropertyConfigurationOption {

	private String[] extensions = new String[0];
	private String browseLabel;
	private boolean directoriesOnly;
	
	public boolean isDirectoriesOnly() {
		return directoriesOnly;
	}

	public void setDirectoriesOnly(boolean directoriesOnly) {
		this.directoriesOnly = directoriesOnly;
	}

	/**
	 * 
	 */
	public FilePropertyConfigurationOption() {

	}

	/**
	 * @param name
	 * @param description
	 * @param defaultValue
	 */
	public FilePropertyConfigurationOption(String name, String description,
			String defaultValue) {
		this(name, description, defaultValue, false);

	}

	/**
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 */
	public FilePropertyConfigurationOption(String name, String description,
			String defaultValue, boolean required) {
		super(name, description, defaultValue, required);

	}

	public String[] getExtensions() {
		return extensions;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	public String getBrowseLabel() {
		return browseLabel;
	}

	public void setBrowseLabel(String browseLabel) {
		this.browseLabel = browseLabel;
	}

}
