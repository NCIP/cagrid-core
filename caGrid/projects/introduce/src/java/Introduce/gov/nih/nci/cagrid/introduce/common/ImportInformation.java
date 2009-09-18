package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;


/**
 * Used for organizing imports for wsdl
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class ImportInformation {

	private MethodTypeImportInformation information;
	private String prefix;


	public ImportInformation(MethodTypeImportInformation information, String prefix) {
		this.information = information;
		this.prefix = prefix;
	}


	public MethodTypeImportInformation getInformation() {
		return information;
	}


	public void setNamespace(MethodTypeImportInformation information) {
		this.information = information;
	}


	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}