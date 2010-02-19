package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import java.rmi.RemoteException;
import java.util.ArrayList;

import namingauthority.IdentifierValues;
import namingauthority.KeyData;
import namingauthority.KeyValues;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;


public class IdentifiersMaintenanceStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public IdentifiersMaintenanceStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    @Override
    public void runStep() throws RemoteException, MalformedURIException {
    	
    	assertNotNull("Null identifier list", testInfo.getIdentifiers());
    	assertNotNull("Null identifier values list", testInfo.getIdentifierValues());
    	
    	// Just pick one
    	URI identifier = testInfo.getIdentifiers().get(0);
    	IdentifierValues values = testInfo.getIdentifierValues().get(0);
    	
    	assertNotNull("Null identifier", identifier);
    	assertNotNull("Null values", values);
    	
    	EndpointReferenceType epr = null;
        try {
            epr = testInfo.getGridSvcEPR();
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail("Error constructing client:" + e.getMessage());
        }
        
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        testCreateKeys(client, identifier);
        testReplaceKeys(client, identifier);
        testDeleteKeys(client, identifier);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Test createKeys interface
    ////////////////////////////////////////////////////////////////////////////
    public void testCreateKeys(IdentifiersNAServiceClient client, URI identifier) throws InvalidIdentifierFault, NamingAuthorityConfigurationFault, NamingAuthoritySecurityFault, InvalidIdentifierValuesFault, RemoteException {

    	KeyValues[] newKeyValues = new KeyValues[1];
    	newKeyValues[0] = new KeyValues();
    	newKeyValues[0].setKey("KEY3");
    	newKeyValues[0].setKeyData(new KeyData(null, new String[]{"KEY3 VALUE"}));

    	client.createKeys(identifier, new IdentifierValues(newKeyValues));

    	String[] values = client.getKeyValues(identifier, "KEY3");
    	if (values == null || values.length != 1
    			|| !values[0].equals("KEY3 VALUE")) {
    		fail("Unexpected results");
    	}
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Test createKeys interface
    ////////////////////////////////////////////////////////////////////////////
    public void testReplaceKeys(IdentifiersNAServiceClient client, URI identifier) throws InvalidIdentifierFault, NamingAuthorityConfigurationFault, NamingAuthoritySecurityFault, InvalidIdentifierValuesFault, RemoteException {

    	KeyValues[] newKeyValues = new KeyValues[1];
    	newKeyValues[0] = new KeyValues();
    	newKeyValues[0].setKey("KEY3");
    	newKeyValues[0].setKeyData(new KeyData(null, new String[]{"KEY3 NEW VALUE"}));

    	client.replaceKeys(identifier, new IdentifierValues(newKeyValues));

    	String[] values = client.getKeyValues(identifier, "KEY3");
    	if (values == null || values.length != 1
    			|| !values[0].equals("KEY3 NEW VALUE")) {
    		fail("Unexpected results");
    	}
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Test deleteKeys interface
    ////////////////////////////////////////////////////////////////////////////
    public void testDeleteKeys(IdentifiersNAServiceClient client, URI identifier) throws InvalidIdentifierFault, NamingAuthorityConfigurationFault, NamingAuthoritySecurityFault, InvalidIdentifierValuesFault, RemoteException {

    	String[] keyList = new String[] { "KEY3" };
    	
    	client.deleteKeys(identifier, keyList);

    	String[] values = client.getKeyValues(identifier, keyList[0]);
    	if (values != null) {
    		fail("Unexpected results");
    	}
    }
}
