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
package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.cagrid.gaards.dorian.service.BeanUtils;
import org.cagrid.tools.database.Database;
import org.springframework.core.io.FileSystemResource;

public class CleanupDorianStep extends Step {

	private ServiceContainer container;
	private ConfigureGlobusToTrustDorianStep trust;

	public CleanupDorianStep(ServiceContainer container,
			ConfigureGlobusToTrustDorianStep trust) {
		this.container = container;
		this.trust = trust;
	}

	public void runStep() throws Throwable {
		if (trust != null) {
			trust.cleanup();
		}
		File conf = new File(this.container.getProperties()
				.getContainerDirectory().getAbsolutePath()
				+ File.separator
				+ "webapps"
				+ File.separator
				+ "wsrf"
				+ File.separator
				+ "WEB-INF"
				+ File.separator
				+ "etc"
				+ File.separator
				+ "cagrid_Dorian"
				+ File.separator
				+ "dorian-configuration.xml");
		File props = new File(this.container.getProperties()
				.getContainerDirectory().getAbsolutePath()
				+ File.separator
				+ "webapps"
				+ File.separator
				+ "wsrf"
				+ File.separator
				+ "WEB-INF"
				+ File.separator
				+ "etc"
				+ File.separator
				+ "cagrid_Dorian"
				+ File.separator
				+ "dorian.properties");

		BeanUtils utils = new BeanUtils(new FileSystemResource(conf),
				new FileSystemResource(props));
		Database db = utils.getDatabase();
		db.destroyDatabase();
	}
}
