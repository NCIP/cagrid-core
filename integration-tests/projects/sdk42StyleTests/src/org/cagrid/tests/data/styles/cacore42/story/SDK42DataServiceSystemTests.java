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
package org.cagrid.tests.data.styles.cacore42.story;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SDK42DataServiceSystemTests {
    
    private static Log LOG = LogFactory.getLog(SDK42DataServiceSystemTests.class);
    
    private static File tempApplicationDir;
    
    @BeforeClass
    public static void setUp() throws Throwable {
        // create a temporary directory for the SDK application to package things in
        tempApplicationDir = File.createTempFile("SdkExample", "temp");
        tempApplicationDir.delete();
        tempApplicationDir.mkdirs();
        LOG.debug("Created temp application base dir: " + tempApplicationDir.getAbsolutePath());
        
        // throw out the ivy cache dirs used by the SDK
        LOG.debug("Destroying SDK ivy cache");
        NukeIvyCacheStory nukeStory = new NukeIvyCacheStory();
        nukeStory.runBare();
        
        // create the caCORE SDK example project
        LOG.debug("Running caCORE SDK example project creation story");
        CreateExampleProjectStory createExampleStory = new CreateExampleProjectStory(tempApplicationDir, false);
        createExampleStory.runBare();
    }
    
    @Test
    public void localSDKApiTests() throws Throwable {
        // create and run a caGrid Data Service using the SDK's local API
        LOG.debug("Running data service using local API story");
        SDK42StyleLocalApiStory localApiStory = new SDK42StyleLocalApiStory(false, false);
        localApiStory.runBare();
    }
    
    @Test
    public void secureLocalSDKApiTests() throws Throwable {    
        // create and run a secure caGrid Data Service using the SDK's local API
        LOG.debug("Running secure data service using local API story");
        SDK42StyleLocalApiStory secureLocalApiStory = new SDK42StyleLocalApiStory(true, false);
        secureLocalApiStory.runBare();
    }
    
    @Test
    public void remoteSDKApiTests() throws Throwable {
        // create and run a caGrid Data Service using the SDK's remote API
        LOG.debug("Running data service using remote API story");
        SDK42StyleRemoteApiStory remoteApiStory = new SDK42StyleRemoteApiStory(false);
        remoteApiStory.runBare();
    }
    
    @Test
    public void secureRemoteSDKApiTests() throws Throwable {
        // create and run a secure caGrid Data Service using the SDK's remote API
        LOG.debug("Running secure data service using remote API story");
        SDK42StyleRemoteApiStory secureRemoteApiStory = new SDK42StyleRemoteApiStory(true);
        secureRemoteApiStory.runBare();
    }
    
    @Test
    public void localSDKApiWithCsmTests() throws Throwable {
        LOG.debug("Running caCORE SDK example project with CSM creation story");
        CreateExampleProjectStory createExampleWithCsmStory = new CreateExampleProjectStory(tempApplicationDir, true);
        createExampleWithCsmStory.runBare();

        // create and run a caGrid Data Service using the SDK's local API
        LOG.debug("Running secure data service using local API and CSM story");
        SDK42StyleLocalApiStory localApiWithCsmStory = new SDK42StyleLocalApiStory(true, true);
        localApiWithCsmStory.runBare();
    }
    
    
    @AfterClass
    public static void cleanUp() {
        LOG.debug("Cleaning up after tests");
        // throw away the temp sdk dir
        LOG.debug("Deleting temp application base dir: " + tempApplicationDir.getAbsolutePath());
        Utils.deleteDir(tempApplicationDir);
    }  

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDK42DataServiceSystemTests.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
