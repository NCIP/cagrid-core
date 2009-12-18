package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;

public class GridServiceFaultsTestStep extends Step {

    private IdentifiersTestInfo testInfo;

    public GridServiceFaultsTestStep(IdentifiersTestInfo info) {
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
        
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        testInvalidIdentifierFault( client );
    }
    
    private void testInvalidIdentifierFault( IdentifiersNAServiceClient client ) {
		try {
			org.apache.axis.types.URI identifier = new URI("file://324324325");
			client.resolveIdentifier(identifier);
		}
		catch(InvalidIdentifierFault e) {
			//expected
			System.out.println("Caught expected fault: InvalidIdentifierFault");
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
