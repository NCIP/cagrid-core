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
package org.cagrid.gaards.websso.test.system.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import gov.nih.nci.cagrid.testing.system.deployment.ContainerProperties;
import gov.nih.nci.cagrid.testing.system.haste.Step;

/**
 * add truststore properties
 * @author garmillas
 *
 */
public class ChangeCatalinaPropertiesStep extends Step {
	
	private ContainerProperties containerProperties;
	private String cacertsFileName;

	public ChangeCatalinaPropertiesStep(ContainerProperties containerProperties,
			String cacertsFileName) {
		this.containerProperties = containerProperties;
		this.cacertsFileName = cacertsFileName;
	}

	@Override
	public void runStep() throws Throwable {
		String confDir = containerProperties.getContainerDirectory().getCanonicalPath()
				+ File.separator + "conf" + File.separator;
		String catalinaPropertiesFile =confDir+"catalina.properties";

		Properties catalinaProperties = new Properties();
		catalinaProperties.load(new FileInputStream(catalinaPropertiesFile));
		
		catalinaProperties.setProperty("javax.net.ssl.trustStore", confDir
				+ cacertsFileName);
		catalinaProperties.setProperty("javax.net.ssl.trustStorePassword",
				"changeit");
		catalinaProperties.store(new FileOutputStream(catalinaPropertiesFile), null);
	}

	public static void main(String[] args) throws Throwable {
		
		ContainerProperties containerProperties=new ContainerProperties();
		ChangeCatalinaPropertiesStep step = new ChangeCatalinaPropertiesStep(containerProperties,"cacerts");
		step.runStep();
	}
}
