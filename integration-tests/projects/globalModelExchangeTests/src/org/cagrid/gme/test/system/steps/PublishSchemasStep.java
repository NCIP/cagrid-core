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
package org.cagrid.gme.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchema;


public class PublishSchemasStep extends Step {

    private final EndpointReferenceType gmeEPR;
    private final Collection<XMLSchema> schemas;


    public PublishSchemasStep(EndpointReferenceType gmeEPR, Collection<XMLSchema> schemas) {
        this.gmeEPR = gmeEPR;
        this.schemas = schemas;
    }


    @Override
    public void runStep() throws RemoteException, MalformedURIException {
        assertNotNull("A non-null EPR must be passed in.", this.gmeEPR);
        assertNotNull("A non-null Collection of XMLSchemas to publish must be passed in.", this.schemas);

        GlobalModelExchangeClient gme = new GlobalModelExchangeClient(this.gmeEPR);

        gme.publishXMLSchemas(this.schemas.toArray(new XMLSchema[this.schemas.size()]));

    }
}
