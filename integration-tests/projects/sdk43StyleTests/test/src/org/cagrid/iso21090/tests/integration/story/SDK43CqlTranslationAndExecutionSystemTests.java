package org.cagrid.iso21090.tests.integration.story;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.tests.integration.steps.SdkDestroyDatabaseStep;
import org.junit.After;
import org.junit.Test;

/**
 * SDK43CqlTranslationAndExecutionSystemTests
 * 
 * Runs the SDK with ISO types to generate a system,
 * sets up the CQL to HQL translator,
 * then translates several CQL queries to HQL
 * and invokes them against the caCORE SDK
 * local API
 *  
 * @author David
 */
public class SDK43CqlTranslationAndExecutionSystemTests {
    
    private static Log LOG = LogFactory.getLog(SDK43CqlTranslationAndExecutionSystemTests.class);
    
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
        TestResult result = runner.doRun(new TestSuite(SDK43CqlTranslationAndExecutionSystemTests.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
