package org.cagrid.gme.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchemaNamespace;


public class CheckPublishedNamespacesStep extends Step {

    private final EndpointReferenceType gmeEPR;
    private final Collection<XMLSchemaNamespace> expectedNamespaces;


    public CheckPublishedNamespacesStep(EndpointReferenceType gmeEPR, Collection<XMLSchemaNamespace> expectedNamespaces) {
        this.gmeEPR = gmeEPR;
        this.expectedNamespaces = expectedNamespaces;
    }


    @Override
    public void runStep() throws RemoteException, MalformedURIException {
        assertNotNull("A non-null EPR must be passed in.", this.gmeEPR);
        assertNotNull("A non-null Collection of expected namespaces must be passed in.", this.expectedNamespaces);

        GlobalModelExchangeClient gme = new GlobalModelExchangeClient(this.gmeEPR);

        XMLSchemaNamespace[] schemaNamespaces = gme.getXMLSchemaNamespaces();
        if (schemaNamespaces == null || schemaNamespaces.length == 0) {
            if (this.expectedNamespaces.size() != 0) {
                fail("Expected " + this.expectedNamespaces.size() + " namespaces but received none.");
            }
            // got what we expected
            return;
        }

        assertEquals("The number of namespace was not as expected.", this.expectedNamespaces.size(),
            schemaNamespaces.length);

        for (XMLSchemaNamespace foundNS : schemaNamespaces) {
            if (!this.expectedNamespaces.contains(foundNS)) {
                fail("Found an unexpected namespace:" + foundNS);
            }
        }
    }
}
