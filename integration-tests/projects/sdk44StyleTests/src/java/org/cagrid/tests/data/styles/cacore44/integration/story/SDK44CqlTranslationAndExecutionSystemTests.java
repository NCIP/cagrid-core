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

/**
 * SDK44CqlTranslationAndExecutionSystemTests
 * 
 * Runs the SDK with ISO types to generate a system,
 * sets up the CQL to HQL translator,
 * then translates several CQL queries to HQL
 * and invokes them against the caCORE SDK
 * local API
 *  
 * @author David
 */
public class SDK44CqlTranslationAndExecutionSystemTests {
    
    private static Log LOG = LogFactory.getLog(SDK44CqlTranslationAndExecutionSystemTests.class);
    
    private long lastTime = 0;
    
    @Test
    public void sdk43DataServiceSystemTests() throws Throwable {
        // using the artifacts from the previous, run some CQL queries
        splitTime();
        LOG.debug("Running CQL queryes against generated caCORE SDK example project");
        TranslateAndExecuteCqlStory executeStory = new TranslateAndExecuteCqlStory();
        executeStory.runBare();
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
        TestResult result = runner.doRun(new TestSuite(SDK44CqlTranslationAndExecutionSystemTests.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
