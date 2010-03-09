package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;

import namingauthority.IdentifierData;
import namingauthority.IdentifierValues;
import namingauthority.KeyData;
import namingauthority.KeyNameData;
import namingauthority.KeyValues;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;
import org.cagrid.identifiers.test.system.IdentifiersTestUtil;

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
        
        String[] keys = new String[] { "CODES", "URLS" };
        String[][] values = new String[][]{
        		{"007"},
        		{"http://na.cagrid.org/foo", "http://na.cagrid.org/bar" }
        };
        
        KeyNameData[] kvs = new KeyNameData[ keys.length ];
		for( int i=0; i < keys.length; i++) {
			kvs[i] = IdentifiersTestUtil.createKeyNameData(keys[i], values[i]);
		}
		
		IdentifierData id = new IdentifierData(kvs);
		
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        URI identifier = client.createIdentifier(id);
        this.testInfo.addIdentifier(identifier, id);
    }
}
