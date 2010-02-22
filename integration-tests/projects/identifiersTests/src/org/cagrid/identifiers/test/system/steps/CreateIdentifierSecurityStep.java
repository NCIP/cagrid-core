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
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;


public class CreateIdentifierSecurityStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public CreateIdentifierSecurityStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    @Override
    public void runStep() throws Exception {
    	
    	boolean expected;
    	EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        
    	// PUBLIC_CREATION is "true" by default so, every one
    	// can create identifiers
    	client.createIdentifier(null);
    	
    	// Disable PUBLIC_CREATION
    	new ChangePublicIdentifierCreationStep(testInfo, 
    			SecurityUtil.PUBLIC_CREATION_NO).runStep();
    	
    	// Let's try again. Should fail now
    	expected = false;
    	try {
    		client.createIdentifier(null);
    	} catch(NamingAuthoritySecurityFault e) {
    		System.err.println(e.getFaultString());
    		expected = true;
    	}
    	if (!expected) {
    		fail("Expected NamingAuthoritySecurityFault was not raised");
    	}
    	
    	// Let's try now with non-admin credentials. Should fail again
    	expected = false;
    	try {
    		client.setAnonymousPrefered(false);
    		client.setProxy(testInfo.getUserB());
    		client.createIdentifier(null);
    	} catch(NamingAuthoritySecurityFault e) {
    		System.err.println(e.getFaultString());
    		expected = true;
    	}
    	if (!expected) {
    		fail("Expected NamingAuthoritySecurityFault was not raised");
    	}
    	
    	// Let's try now with admin credentials

//    	KeyValues[] newKeyValues = new KeyValues[1];
//    	newKeyValues[0] = new KeyValues();
//    	newKeyValues[0].setKey(Keys.PUBLIC_CREATION);
//    	newKeyValues[0].setKeyData(new KeyData(null, new String[]{this.publicCreation}));
//
//    	try {
//    		client.replaceKeys(testInfo.getSystemIdentifier(), new IdentifierValues(newKeyValues));
//    	} catch(NamingAuthoritySecurityFault e) {
//    		//expected
//    	}
    }
}
