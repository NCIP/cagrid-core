package org.cagrid.data.style.test.cacore32;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;
import org.cagrid.data.test.system.RebuildServiceStep;

/** 
 *  SDK32StyleSystemTestStory
 *  Tests creating and invoking a caGrid Data Service using the SDK32 service style
 * 
 * @author David Ervin
 * 
 * @created Jul 18, 2007 2:35:15 PM
 * @version $Id: SDK32StyleSystemTestStory.java,v 1.2 2008-11-07 15:08:48 dervin Exp $ 
 */
public class SDK32StyleSystemTestStory extends Story {
    public static final String INTRODUCE_DIR_PROPERTY = "introduce.base.dir";
    
    private DataTestCaseInfo tci = null;
    private File sdkPackageDir = null;
    private ServiceContainer serviceContainer = null;

    public SDK32StyleSystemTestStory() {
        setName("Data Service System Test with caCORE 3_2 / 3_2_1 Style");
    }


    public String getDescription() {
        return "A test for creating and invoking a caGrid data service using the caCORE 3.2 / 3.2.1 service style";
    }
    
    
    public String getName() {
        return "Data Service Creation with caCORE 3_2 / 3_2_1 Style";
    }
    
    
    private String getIntroduceBaseDir() {
        String dir = System.getProperty(INTRODUCE_DIR_PROPERTY);
        if (dir == null) {
            fail("Introduce base dir environment variable " + INTRODUCE_DIR_PROPERTY + " is required");
        }
        return dir;
    }
    
    
    public boolean storySetUp() {
        // the data service's basic information
        this.tci = new DataTestCaseInfo() {
            public String getServiceDirName() {
                return getName();
            }

            
            public String getName() {
                return "TestCaCORE32StyleService";
            }

            
            public String getNamespace() {
                return "http://" + getPackageName() + "/" + getName();
            }
            

            public String getPackageName() {
                return "gov.nih.nci.cagrid.data.style.test.cacore32";
            }
        };
        
        // the caCORE SDK 3.2.1 package to grab artifacts out of
        try {
            sdkPackageDir = File.createTempFile("SDK321Package", "dir");
            sdkPackageDir.delete();
            sdkPackageDir.mkdirs();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error creating temp directory: " + ex.getMessage());
        }
        File sdkPackageZip = new File(Sdk32TestConstants.SDK_PACKAGE_ZIP);
        try {
            System.out.println("Unzipping SDK 3.2.1 package zip " + sdkPackageZip.getAbsolutePath());
            ZipUtilities.unzip(sdkPackageZip, sdkPackageDir);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error unzipping SDK package: " + ex.getMessage());
        }
        
        // the service container to deploy into
        try {
            serviceContainer = ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
        
        Step step = new UnpackContainerStep(serviceContainer);
        try {
            step.runStep();
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }

        return true;
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        // clean up
        steps.add(new DeleteOldServiceStep(tci));
        // create the service and set it up for running with SDK 3.2.1
        steps.add(new CreateSDK32StyleServiceStep(
            tci, getIntroduceBaseDir(), sdkPackageDir));
        steps.add(new EditServiceDescriptionStep(tci));
        steps.add(new AddCabioSchemaStep(tci, sdkPackageDir));
        steps.add(new CopyEtcFilesStep(tci));
        steps.add(new CopySdkLibrariesStep(tci, sdkPackageDir));
        steps.add(new RebuildServiceStep(tci, getIntroduceBaseDir()));
        // deploy the service to the container
        steps.add(new DeployServiceStep(serviceContainer, tci.getDir()));
        // start the container
        steps.add(new StartContainerStep(serviceContainer));
        // invoke the data service
        steps.add(new InvokeDataServiceStep(tci, serviceContainer));
        return steps;
    }
    
    
    protected void storyTearDown() throws Throwable {
        new StopContainerStep(serviceContainer).runStep();
        new DestroyContainerStep(serviceContainer).runStep();
        new DeleteOldServiceStep(tci).runStep();
        Step deleteTempSdkStep = new Step() {
            public void runStep() throws Throwable {
                Utils.deleteDir(sdkPackageDir);
            }
        };
        deleteTempSdkStep.runStep();
    }
}
