package org.cagrid.tests.data.styles.cacore42.steps;

import java.io.File;

import org.cagrid.tests.data.styles.cacore42.ExampleProjectInfo;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.TomcatServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class DeployExampleProjectStep extends Step {
    
    private ServiceContainer container = null;

    public DeployExampleProjectStep(ServiceContainer container) {
        super();
        this.container = container;
    }


    public void runStep() throws Throwable {
        assertTrue("Container must be Tomcat", container instanceof TomcatServiceContainer);
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
