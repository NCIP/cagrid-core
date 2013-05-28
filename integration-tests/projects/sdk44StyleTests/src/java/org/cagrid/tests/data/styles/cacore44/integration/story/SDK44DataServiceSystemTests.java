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
package org.cagrid.tests.data.styles.cacore44.integration.story;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.tests.data.styles.cacore44.integration.steps.SdkDestroyDatabaseStep;
import org.junit.After;
import org.junit.Test;

public class SDK44DataServiceSystemTests {
    
    private static Log LOG = LogFactory.getLog(SDK44DataServiceSystemTests.class);
    
    private long lastTime = 0;
    
    @Test
    public void sdk44DataServiceSystemTests() throws Throwable {
        // create and run a caGrid Data Service using the SDK's local API
        splitTime();
        LOG.debug("Running data service using local API story");
        SDK44StyleLocalApiStory localApiStory = new SDK44StyleLocalApiStory();
        localApiStory.runBare();
        
        // create and run a caGrid Data Service using the SDK's remote API
        splitTime();
        LOG.debug("Running data service using remote API story");
        SDK44StyleRemoteApiStory remoteApiStory = new SDK44StyleRemoteApiStory();
        remoteApiStory.runBare();
    }
    
    
    @After
    public void cleanUp() {
        LOG.debug("Cleaning up after tests");
        // tear down the sdk example database
        try {
            new SdkDestroyDatabaseStep().runStep();
        } catch (Exception ex) {
            LOG.warn("Error destroying SDK example project database: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    
    private void splitTime() {
        if (lastTime == 0) {
            LOG.debug("Timer started");
        } else {
            LOG.debug("Time elapsed: " 
                + (System.currentTimeMillis() - lastTime) / 1000D + " sec");
        }
        lastTime = System.currentTimeMillis();
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDK44DataServiceSystemTests.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
