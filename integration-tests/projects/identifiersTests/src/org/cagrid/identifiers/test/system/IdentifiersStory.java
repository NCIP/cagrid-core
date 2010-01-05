package org.cagrid.identifiers.test.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import org.cagrid.identifiers.test.system.steps.CopyNamingAuthorityConfigStep;
import org.cagrid.identifiers.test.system.steps.CreateDatabasesStep;
import org.cagrid.identifiers.test.system.steps.CreateRedirectPurlStep;
import org.cagrid.identifiers.test.system.steps.GridServiceFaultsTestStep;
import org.cagrid.identifiers.test.system.steps.GridServiceIdentifierCreationStep;
import org.cagrid.identifiers.test.system.steps.IdentifiersClientGridResolutionStep;
import org.cagrid.identifiers.test.system.steps.IdentifiersClientHttpResolutionStep;
import org.cagrid.identifiers.test.system.steps.ShutdownPurlStep;
import org.cagrid.identifiers.test.system.steps.StartPurlStep;
import org.cagrid.identifiers.test.system.steps.UnpackPurlStep;
import org.cagrid.identifiers.test.system.steps.CreatePurlDomainStep;


public class IdentifiersStory extends Story {

	private IdentifiersTestInfo testInfo = null;

    @Override
    public String getName() {
        return getDescription();
    }

    @Override
    public String getDescription() {
        return "Identifiers System Test";
    }

	@Override
	protected boolean storySetUp() {
		
		try {
			testInfo = new IdentifiersTestInfo();
			testInfo.createGridSvcContainer();
			testInfo.createWebAppContainer();

		} catch (Throwable ex) {
			String message = "Error creating naming authority containers: "
					+ ex.getMessage();
			System.err.println(ex);
			fail(message);
		}

		return true;
	}


    @Override
    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();
        
        File webProjDir = new File(IdentifiersTestInfo.WEBAPP_PROJ_DIR);
        File webTmpDir = new File(IdentifiersTestInfo.WEBAPP_TMP_DIR);
        File gridProjDir = new File(IdentifiersTestInfo.GRIDSVC_PROJ_DIR);
        File gridTmpDir = new File(IdentifiersTestInfo.GRIDSVC_TMP_DIR);

        /////////////////////////////////////////////////////
        // Unpack PURLZ & Tomcat Containers
        /////////////////////////////////////////////////////
        steps.add(new UnpackPurlStep(testInfo));
        steps.add(new UnpackContainerStep(testInfo.getWebAppContainer()));
        steps.add(new UnpackContainerStep(testInfo.getGridSvcContainer()));
        
        /////////////////////////////////////////////////////
        // Copy Services to temp area
        /////////////////////////////////////////////////////
        steps.add(new CopyServiceStep(webProjDir, webTmpDir));
        steps.add(new CopyServiceStep(gridProjDir, gridTmpDir));
  
        /////////////////////////////////////////////////////
        // Copy common naming authority configuration to
        // both projects
        /////////////////////////////////////////////////////
        steps.add(new CopyNamingAuthorityConfigStep(testInfo));
        
        /////////////////////////////////////////////////////
        // Deploy services to containers
        /////////////////////////////////////////////////////
        steps.add(new DeployServiceStep(testInfo.getWebAppContainer(), 
        		webTmpDir.getAbsolutePath(), 
        		Arrays.asList(new String[]{"-Dno.deployment.validation=true"})));
        steps.add(new DeployServiceStep(testInfo.getGridSvcContainer(), 
        		gridTmpDir.getAbsolutePath(), 
        		Arrays.asList(new String[]{"-Dno.deployment.validation=true"})));
        
        /////////////////////////////////////////////////////
        // Create Databases
        /////////////////////////////////////////////////////
        steps.add(new CreateDatabasesStep(testInfo));

        /////////////////////////////////////////////////////
        // Start up PURLZ & Tomcat Containers
        /////////////////////////////////////////////////////
        steps.add(new StartPurlStep(testInfo));
        steps.add(new StartContainerStep(testInfo.getWebAppContainer()));
        steps.add(new StartContainerStep(testInfo.getGridSvcContainer()));


        ////////////////////////////////////////////////////
        // Create PURL Domain & Partial Redirect PURL
        ////////////////////////////////////////////////////
        steps.add(new CreatePurlDomainStep(testInfo));
        steps.add(new CreateRedirectPurlStep(testInfo));
       
        
        /////////////////////////////////////////////////////
        // Can we test now?
        /////////////////////////////////////////////////////
        steps.add(new GridServiceFaultsTestStep(testInfo));
        steps.add(new GridServiceIdentifierCreationStep(testInfo));
        steps.add(new IdentifiersClientHttpResolutionStep(testInfo));
        steps.add(new IdentifiersClientGridResolutionStep(testInfo));

        return steps;
    }


    @Override
    protected void storyTearDown() throws Throwable {
    	ServiceContainer webAppContainer = testInfo.getWebAppContainer();
    	ServiceContainer gridSvcContainer = testInfo.getGridSvcContainer();

        new StopContainerStep(webAppContainer).runStep();
        new StopContainerStep(gridSvcContainer).runStep();
        new DestroyContainerStep(webAppContainer).runStep();
        new DestroyContainerStep(gridSvcContainer).runStep();
    	
    	new ShutdownPurlStep(testInfo).runStep();
    }
}
