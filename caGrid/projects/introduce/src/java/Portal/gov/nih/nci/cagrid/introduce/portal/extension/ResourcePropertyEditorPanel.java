package gov.nih.nci.cagrid.introduce.portal.extension;

import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;

import java.io.File;

import javax.swing.JPanel;


public abstract class ResourcePropertyEditorPanel extends JPanel {
	private String rpString;
	private File schemaFile;
	private File schemaDir;
	private ResourcePropertyType prop;


	public ResourcePropertyEditorPanel(ResourcePropertyType prop, String doc, File schemaFile, File schemaDir) {
		this.rpString = doc;
		this.schemaFile = schemaFile;
		this.schemaDir = schemaDir;
		this.prop = prop;
	}

	public abstract String getResultRPString();

	
	public ResourcePropertyType getResourcePropertyType(){
	    return this.prop;
	}

	protected String getRPString() {
		return rpString;
	}


	public File getSchemaFile() {
		return schemaFile;
	}


	public void setSchemaFile(File schemaFile) {
		this.schemaFile = schemaFile;
	}


	public File getSchemaDir() {
		return schemaDir;
	}


	public void setSchemaDir(File schemaDir) {
		this.schemaDir = schemaDir;
	}
	
	
	public abstract void validateResourceProperty() throws Exception;
}
