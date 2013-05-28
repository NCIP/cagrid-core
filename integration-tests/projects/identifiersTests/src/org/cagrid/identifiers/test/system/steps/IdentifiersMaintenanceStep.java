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
package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;

import namingauthority.IdentifierData;
import namingauthority.IdentifierValues;
import namingauthority.KeyNameData;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;
import org.cagrid.identifiers.test.system.IdentifiersTestUtil;


public class IdentifiersMaintenanceStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public IdentifiersMaintenanceStep(IdentifiersTestInfo info) {
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
    public void testCreateKeys(IdentifiersNAServiceClient client, URI identifier) 
    	throws 
    		InvalidIdentifierFault, 
    		NamingAuthorityConfigurationFault, 
    		NamingAuthoritySecurityFault, 
    		InvalidIdentifierValuesFault, 
    		RemoteException {

    	String keyName = "KEY3";
    	String[] keyValues = new String[]{"KEY3 VALUE"};

    	IdentifiersTestUtil.createKey(client, null, identifier, keyName, keyValues);

    	KeyNameData values = client.getKeyData(identifier, keyName);
    	for(String value : values.getKeyData().getValue()) {
    		if (value.equals(keyValues[0])) {
    			// got it
    			return;
    		}
    	}
   		fail("Unexpected results");
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Test replaceKeys interface
    ////////////////////////////////////////////////////////////////////////////
    public void testReplaceKeys(IdentifiersNAServiceClient client, URI identifier) 
    	throws 
    		InvalidIdentifierFault, 
    		NamingAuthorityConfigurationFault, 
    		NamingAuthoritySecurityFault, 
    		InvalidIdentifierValuesFault, 
    		RemoteException {

    	String keyName = "KEY3";
    	String[] keyValues = new String[]{"KEY3 NEW VALUE"};
    	
    	IdentifiersTestUtil.replaceKeyValues(client, null, identifier, keyName, keyValues);

    	KeyNameData values = IdentifiersTestUtil.getKeyData(client, null, identifier, keyName);
    	for(String value : values.getKeyData().getValue()) {
    		if (value.equals(keyValues[0])) {
    			// got it
    			return;
    		}
    	}
   		fail("Unexpected results");
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Test deleteKeys interface
    ////////////////////////////////////////////////////////////////////////////
    public void testDeleteKeys(IdentifiersNAServiceClient client, URI identifier) 
    	throws 
    		InvalidIdentifierFault, 
    		NamingAuthorityConfigurationFault, 
    		NamingAuthoritySecurityFault, 
    		InvalidIdentifierValuesFault, 
    		RemoteException {

    	String[] keyList = new String[] { "KEY3" };
    	
    	IdentifiersTestUtil.deleteKeys(client, null, identifier, keyList);
    	
    	try {
    		IdentifiersTestUtil.getKeyData(client, null, identifier, keyList[0]);
    		fail("getKeyData was expected to fail");
    		
    	} catch (InvalidIdentifierValuesFault e) {
    		//expected
    	}
    }
}
