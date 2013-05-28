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
package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.Tomcat6ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.TomcatServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.cagrid.tests.data.styles.cacore42.ExampleProjectInfo;

public class DeployExampleProjectStep extends Step {
    
    private ServiceContainer container = null;

    public DeployExampleProjectStep(ServiceContainer container) {
        super();
        this.container = container;
    }


    public void runStep() throws Throwable {
        assertTrue("Container must be Tomcat", container instanceof TomcatServiceContainer || container instanceof Tomcat6ServiceContainer);
        File webappsDir = new File(container.getProperties().getContainerDirectory(), "webapps");
        File exampleProjectWar = new File(ExampleProjectInfo.getExampleProjectDir(),
            "target" + File.separator + "dist" + File.separator +
            "exploded" + File.separator + "output" + File.separator +
            "example" + File.separator + "package" + File.separator +
            "server" + File.separator + "tomcat" + File.separator +
            "webapps" + File.separator + "example.war");
        assertTrue("Project WAR file not found (" + exampleProjectWar.getAbsolutePath() + ")", exampleProjectWar.exists());
        Utils.copyFile(exampleProjectWar, new File(webappsDir, exampleProjectWar.getName()));
    }
}
