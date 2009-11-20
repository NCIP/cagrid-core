package org.cagrid.tests.data.styles.cacore42.story;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.tests.data.styles.cacore42.steps.SdkDatabaseStep;
import org.cagrid.tests.data.styles.cacore42.steps.SdkDatabaseStep.DatabaseOperation;
import org.junit.After;
import org.junit.Test;

public class SDK42DataServiceSystemTests {
    
    private static Log LOG = LogFactory.getLog(SDK42DataServiceSystemTests.class);
    
    private long lastTime = 0;
    private File tempApplicationDir = null;
    
    @Test
    public void sdk42DataServiceSystemTests() throws Throwable {
        // create a temporary directory for the SDK application to package things in
        tempApplicationDir = File.createTempFile("SdkExample", "temp");
        tempApplicationDir.delete();
        tempApplicationDir.mkdirs();
        LOG.debug("Created temp application base dir: " + tempApplicationDir.getAbsolutePath());
        
        // throw out the ivy cache dirs used by the SDK
        splitTime();
        LOG.debug("Destroying SDK ivy cache");
        NukeIvyCacheStory nukeStory = new NukeIvyCacheStory();
        nukeStory.runBare();
        
        // create the caCORE SDK example project
        splitTime();
        LOG.debug("Running caCORE SDK example project creation story");
        CreateExampleProjectStory createExampleStory = new CreateExampleProjectStory(tempApplicationDir, false);
        createExampleStory.runBare();
        
        // create and run a caGrid Data Service using the SDK's local API
        splitTime();
        LOG.debug("Running data service using local API story");
        SDK42StyleLocalApiStory localApiStory = new SDK42StyleLocalApiStory(false, false);
        localApiStory.runBare();
        
        // create and run a secure caGrid Data Service using the SDK's local API
        splitTime();
        LOG.debug("Running secure data service using local API story");
        SDK42StyleLocalApiStory secureLocalApiStory = new SDK42StyleLocalApiStory(true, false);
        secureLocalApiStory.runBare();
        
        // create and run a caGrid Data Service using the SDK's remote API
        splitTime();
        LOG.debug("Running data service using remote API story");
        SDK42StyleRemoteApiStory remoteApiStory = new SDK42StyleRemoteApiStory(false);
        remoteApiStory.runBare();
        
        // create and run a secure caGrid Data Service using the SDK's remote API
        splitTime();
        LOG.debug("Running secure data service using remote API story");
        SDK42StyleRemoteApiStory secureRemoteApiStory = new SDK42StyleRemoteApiStory(true);
        secureRemoteApiStory.runBare();
        
        splitTime();
        LOG.debug("Running caCORE SDK example project with CSM creation story");
        CreateExampleProjectStory createExampleWithCsmStory = new CreateExampleProjectStory(tempApplicationDir, true);
        createExampleWithCsmStory.runBare();

        // create and run a caGrid Data Service using the SDK's local API
        splitTime();
        LOG.debug("Running secure data service using local API and CSM story");
        SDK42StyleLocalApiStory localApiWithCsmStory = new SDK42StyleLocalApiStory(true, true);
        localApiWithCsmStory.runBare();
    }
    
    
    @After
    public void cleanUp() {
        LOG.debug("Cleaning up after tests");
        // tear down the sdk example database
        try {
            new SdkDatabaseStep(DatabaseOperation.DESTROY).runStep();
        } catch (Exception ex) {
            LOG.warn("Error destroying SDK example project database: " + ex.getMessage());
            ex.printStackTrace();
        }
        // throw away the temp sdk dir
        LOG.debug("Deleting temp application base dir: " + tempApplicationDir.getAbsolutePath());
        Utils.deleteDir(tempApplicationDir);
    }
    
    
    private void splitTime() {
        if (lastTime == 0) {
            System.out.println("Timer started");
        } else {
            System.out.println("Time elapsed: " 
                + (System.currentTimeMillis() - lastTime) / 1000D + " sec");
        }
        lastTime = System.currentTimeMillis();
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDK42DataServiceSystemTests.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
