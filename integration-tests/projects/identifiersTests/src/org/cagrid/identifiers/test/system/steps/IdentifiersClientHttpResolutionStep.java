package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;

import namingauthority.IdentifierData;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.resolver.Resolver;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;


public class IdentifiersClientHttpResolutionStep extends Step {

    private IdentifiersTestInfo testInfo;


    public IdentifiersClientHttpResolutionStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }


    @Override
    public void runStep() throws RemoteException, MalformedURIException {
        assertNotNull("Null identifier list", testInfo.getIdentifiers());
        assertNotNull("Null identifier values list", testInfo.getIdentifierData());

        // Just pick one
        URI identifier = testInfo.getIdentifiers().get(0);
        IdentifierData values = testInfo.getIdentifierData().get(0);

        assertNotNull("Null identifier", identifier);
        assertNotNull("Null values", values);

        System.out.println("Going to HTTP resolve [" + identifier.toString() + "]");
        org.cagrid.identifiers.namingauthority.domain.IdentifierData resolvedValues = null;
        org.cagrid.identifiers.namingauthority.domain.IdentifierData insertedValues = null;

        try {
            insertedValues = gov.nih.nci.cagrid.identifiers.common.IdentifiersNAUtil.map(values);
            resolvedValues = new Resolver().resolveHttp(new java.net.URI(identifier.toString()));
            System.out.println(resolvedValues.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail("HTTP Resolution failed: " + e.getMessage());
        }

        assertEquals(insertedValues, resolvedValues);
    }
}
