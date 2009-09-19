package org.cagrid.gme.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchemaNamespace;


public class DeleteSchemasStep extends Step {

    private final EndpointReferenceType gmeEPR;
    private final Collection<XMLSchemaNamespace> namespaces;


    public DeleteSchemasStep(EndpointReferenceType gmeEPR, Collection<XMLSchemaNamespace> namespaces) {
        this.gmeEPR = gmeEPR;
        this.namespaces = namespaces;
    }


    @Override
    public void runStep() throws RemoteException, MalformedURIException {
        assertNotNull("A non-null EPR must be passed in.", this.gmeEPR);
        assertNotNull("A non-null Collection of namespaces to publish must be passed in.", this.namespaces);

        GlobalModelExchangeClient gme = new GlobalModelExchangeClient(this.gmeEPR);

        gme.deleteXMLSchemas(this.namespaces.toArray(new XMLSchemaNamespace[this.namespaces.size()]));

    }
}
