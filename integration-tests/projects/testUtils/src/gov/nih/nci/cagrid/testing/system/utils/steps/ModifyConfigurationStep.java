package gov.nih.nci.cagrid.testing.system.utils.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

public class ModifyConfigurationStep extends Step {

	private File originalPropertyFile;
	private File customPropertyFile;

	public ModifyConfigurationStep(File originalPropertyFile, File customPropertyFile) {
		this.originalPropertyFile = originalPropertyFile;
		this.customPropertyFile = customPropertyFile;
	}

	public void runStep() throws Throwable {
		Properties originalProperties = new Properties();
		originalProperties.load(new FileInputStream(originalPropertyFile));
		
		Properties customProperties = new Properties();
		customProperties.load(new FileInputStream(customPropertyFile));

		Enumeration<?> propertyNames = customProperties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String propertyName = (String) propertyNames.nextElement();
			if (originalProperties.containsKey(propertyName)) {
				originalProperties.setProperty(propertyName, customProperties.getProperty(propertyName));
			} else { 
				fail("Attempting to change a property that does not exist.");
			}
		}
		originalProperties.store(new FileOutputStream(originalPropertyFile), "Updated with values from the test project");
	}

}
