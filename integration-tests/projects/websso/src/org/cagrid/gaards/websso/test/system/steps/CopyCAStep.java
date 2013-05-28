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

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;


public class CopyCAStep extends Step {
    private File outPutDir;
    private ServiceContainer certsContainer;


    public CopyCAStep(ServiceContainer certsContainer,
			File outPutDir) {
		this.certsContainer = certsContainer;
		this.outPutDir = outPutDir;
	}


    public void runStep() throws Throwable {
        System.out.println("Copying user proxys to services dir");

        File inPutDir = new File(certsContainer.getProperties()
				.getContainerDirectory().getAbsolutePath()
				+ File.separator
				+ "certificates"
				+ File.separator
				+ "ca");
         Utils.copyDirectory(inPutDir, outPutDir);
    }

}
