package org.cagrid.data.test.system;

import org.apache.axis.message.addressing.EndpointReferenceType;

import gov.nih.nci.cagrid.data.utilities.DataServiceFeatureDiscoveryUtil;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class VerifyOperationsStep extends Step {
    
    private ServiceContainer container = null;
    private String serviceName = null;
    private boolean shouldSupportCql2 = false;
    private boolean shouldHaveCql2Enumeration = false;
    private boolean shouldHaveCql2Transfer = false;
    
    public VerifyOperationsStep(ServiceContainer container, String serviceName, 
        boolean shouldSupportCql2, boolean shouldHaveCql2Enumeration, boolean shouldHaveCql2Transfer) {
        this.container = container;
        this.serviceName = serviceName;
        this.shouldSupportCql2 = shouldSupportCql2;
        this.shouldHaveCql2Enumeration = shouldHaveCql2Enumeration;
        this.shouldHaveCql2Transfer = shouldHaveCql2Transfer;
    }



    public void runStep() throws Throwable {
        EndpointReferenceType serviceEPR = container.getServiceEPR("cagrid/" + serviceName);
        assertEquals("CQL 2 support was not as expected", shouldSupportCql2, 
            DataServiceFeatureDiscoveryUtil.serviceSupportsCql2(serviceEPR));
        assertEquals("CQL 2 enumeration query method support was not as expected", shouldHaveCql2Enumeration,
            DataServiceFeatureDiscoveryUtil.serviceHasCql2EnumerationOperation(serviceEPR));
        assertEquals("CQL 2 transfer query method support was not as expected", shouldHaveCql2Transfer, 
            DataServiceFeatureDiscoveryUtil.serviceHasCql2TransferOperation(serviceEPR));
    }
}
