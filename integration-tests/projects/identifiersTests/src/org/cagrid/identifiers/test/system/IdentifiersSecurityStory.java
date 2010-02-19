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

import org.cagrid.identifiers.namingauthority.util.SecurityUtil;
import org.cagrid.identifiers.test.system.steps.ChangePublicIdentifierCreationStep;
import org.cagrid.identifiers.test.system.steps.CopyNamingAuthorityConfigStep;
import org.cagrid.identifiers.test.system.steps.CreateDatabasesStep;
import org.cagrid.identifiers.test.system.steps.CreateSystemAdminStep;


public class IdentifiersSecurityStory extends Story {

	private IdentifiersTestInfo testInfo = null;

    @Override
    public String getName() {
        return getDescription();
    }

    @Override
    public String getDescription() {
        return "Identifiers Security System Test";
    }

	@Override
	protected boolean storySetUp() {
		
		try {
			testInfo = new IdentifiersTestInfo();
			testInfo.createGridSvcContainer(true);

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
        // Unpack Tomcat Container
        /////////////////////////////////////////////////////
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
        steps.add(new DeployServiceStep(testInfo.getGridSvcContainer(), 
        		gridTmpDir.getAbsolutePath(), 
        		Arrays.asList(new String[]{"-Dno.deployment.validation=true"})));
        
        /////////////////////////////////////////////////////
        // Create Databases
        /////////////////////////////////////////////////////
        steps.add(new CreateDatabasesStep(testInfo));
        
        /////////////////////////////////////////////////////
        // Add administrator identity
        /////////////////////////////////////////////////////
        steps.add(new CreateSystemAdminStep(testInfo));

        /////////////////////////////////////////////////////
        // Start up Tomcat Container
        /////////////////////////////////////////////////////
        steps.add(new StartContainerStep(testInfo.getGridSvcContainer()));

        /////////////////////////////////////////////////////
        // Can we test now?
        /////////////////////////////////////////////////////
        steps.add(new ChangePublicIdentifierCreationStep(testInfo, SecurityUtil.PUBLIC_CREATION_NO));

        return steps;
    }


    @Override
    protected void storyTearDown() throws Throwable {
    	ServiceContainer gridSvcContainer = testInfo.getGridSvcContainer();
        new StopContainerStep(gridSvcContainer).runStep();
        new DestroyContainerStep(gridSvcContainer).runStep();
    }
}
