package org.cagrid.gme.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaNamespace;


public class CheckGetSchemasStep extends Step {

    private final EndpointReferenceType gmeEPR;
    private final Collection<XMLSchema> schemas;


    public CheckGetSchemasStep(EndpointReferenceType gmeEPR, Collection<XMLSchema> schemas) {
        this.gmeEPR = gmeEPR;
        this.schemas = schemas;
    }


    @Override
    public void runStep() throws RemoteException, MalformedURIException {
        assertNotNull("A non-null EPR must be passed in.", this.gmeEPR);
        assertNotNull("A non-null Collection of XMLSchemas to retrieve must be passed in.", this.schemas);

        GlobalModelExchangeClient gme = new GlobalModelExchangeClient(this.gmeEPR);

        for (XMLSchema goldSchema : this.schemas) {
            XMLSchema retrievedSchema = gme.getXMLSchema(new XMLSchemaNamespace(goldSchema.getTargetNamespace()));
            assertEquals("The retrieved schema did not match the expected schema.", goldSchema, retrievedSchema);
        }

    }
}
