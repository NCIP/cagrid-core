package org.cagrid.fqp.test.local;

import gov.nih.nci.cagrid.fqp.processor.FederatedQueryEngine;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.test.common.AggregationStory;
import org.cagrid.fqp.test.common.DataServiceDeploymentStory;
import org.cagrid.fqp.test.common.FederatedQueryProcessorHelper;
import org.cagrid.fqp.test.common.QueryStory;
import org.cagrid.fqp.test.common.ServiceContainerSource;
import org.junit.After;
import org.junit.Test;

/** 
 *  LocalFqpSystemTests
 *  System tests for FQP using the local Federated Query API
 * 
 * @author David Ervin
 * 
 * @created Jul 10, 2008 10:57:40 AM
 * @version $Id: LocalFqpSystemTests.java,v 1.12 2009-01-16 17:26:11 dervin Exp $ 
 */
public class LocalFqpSystemTests {
    
    public static final Log logger = LogFactory.getLog(LocalFqpSystemTests.class);
    
    private DataServiceDeploymentStory[] deployments;

    @Test
    public void localFqpSystemTests() throws Throwable {
        // deploy two example SDK data services which pull from slightly different data
        DataServiceDeploymentStory exampleService1Deployment = 
            new DataServiceDeploymentStory(new File("resources/services/ExampleSdkService1.zip"), false);
        
        
        DataServiceDeploymentStory exampleService2Deployment =
            new DataServiceDeploymentStory(new File("resources/services/ExampleSdkService2.zip"), false);

        exampleService1Deployment.runBare();
        exampleService2Deployment.runBare();

        
        deployments = new DataServiceDeploymentStory[] {
            exampleService1Deployment, exampleService2Deployment
        };
        

        ServiceContainerSource[] containerSources = new ServiceContainerSource[] {
            exampleService1Deployment, exampleService2Deployment
        };
        FederatedQueryProcessorHelper queryHelper = 
            new FederatedQueryProcessorHelper(new FederatedQueryEngine(null, null));
        
        // run the local aggregation queries
        AggregationStory aggregationTests = new AggregationStory(containerSources, queryHelper);
        aggregationTests.runBare();
        
        // run local standard queries
        QueryStory queryTests = new QueryStory(containerSources, queryHelper);
        queryTests.runBare();

    }


    @After
    public void cleanUp() {
        logger.debug("Cleaning Up Local FQP Tests");
        for (DataServiceDeploymentStory deployment : deployments) {
            if (deployment != null && deployment.getServiceContainer() != null) {
                ServiceContainer container = deployment.getServiceContainer();
                try {
                    new StopContainerStep(container).runStep();
                    new DestroyContainerStep(container).runStep();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
