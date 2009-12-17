package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.identifiers.test.Util;

import java.rmi.RemoteException;
import namingauthority.IdentifierValues;
import namingauthority.KeyValues;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;

public class GridServiceIdentifierCreationStep extends Step {

    private IdentifiersTestInfo testInfo;

    public GridServiceIdentifierCreationStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    @Override
    public void runStep() throws RemoteException, MalformedURIException {
      
		
		EndpointReferenceType epr = null;
        try {
            epr = testInfo.getGridSvcEPR();
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail("Error constructing client:" + e.getMessage());
        }
        
        KeyValues[] keyValues = new KeyValues[2];
		keyValues[0] = new KeyValues();
		keyValues[0].setKey("URL");
		keyValues[0].setValue(new String[] { 
				"http://na.cagrid.org/foo", "http://na.cagrid.org/bar" });

		keyValues[1] = new KeyValues();
		keyValues[1].setKey("CODE");
		keyValues[1].setValue(new String[] { "007" });
		
		IdentifierValues values = new IdentifierValues(keyValues);
		
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        URI identifier = null;
        try {
			identifier = 
				client.createIdentifier(new IdentifierValues(keyValues));
			System.out.println("Identifier: " + identifier.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
        this.testInfo.addIdentifier(identifier, values);
    }
}
