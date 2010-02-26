package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;
import org.cagrid.identifiers.test.system.IdentifiersTestUtil;


public class CreateIdentifierSecurityStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public CreateIdentifierSecurityStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    /*********************************************************************
     * Summary of steps
     * 
     * 1) Show that PUBLIC_CREATION is "true" by default so, every one
     * can create identifiers
     * 
     * 2) Disable PUBLIC_CREATION
     * 
     * 3) Anonymous users can't create identifiers now
     * 
     * 4) Unauthorized regular users can't either
     * 
     * 5) Unauthorized administrators can't either
     * 
     * 6) Add User B to IDENTIFIER_CREATION_USERS
     * 
     * 7) User B can now create identifiers
	 ********************************************************************/
    @Override
    public void runStep() throws Exception {
    	
    	boolean expected;
    	EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        
        /////////////////////////////////////////////////////
    	// PUBLIC_CREATION is "true" by default so, every one
    	// can create identifiers
        /////////////////////////////////////////////////////
        try {
        	URI identifier = client.createIdentifier(null);
        	System.out.println(identifier.toString());
        	
        } catch (Exception e) {
        	e.printStackTrace();
        	fail("Unexpected exception: " + e.toString());
        }
    	
        /////////////////////////////////////////////////////
    	// Disable PUBLIC_CREATION
        /////////////////////////////////////////////////////
    	IdentifiersTestUtil.changePublicIdentifierCreation(testInfo, 
    			SecurityUtil.PUBLIC_CREATION_NO);
    	
    	/////////////////////////////////////////////////////
    	// Attempts to create identifiers should fail now
    	// unless appropriate credentials are provided
    	/////////////////////////////////////////////////////
    	expected = false;
    	try {
    		// Anonymous
    		client.setAnonymousPrefered(true);
    		client.createIdentifier(null);
    	} catch(NamingAuthoritySecurityFault e) {
    		System.err.println(e.getFaultString());
    		expected = true;
    	}
    	if (!expected) {
    		fail("Expected NamingAuthoritySecurityFault was not raised");
    	}
    	
    	/////////////////////////////////////////////////////
    	// Retry with non-administrator credentials.
    	// Should fail.
    	/////////////////////////////////////////////////////
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
    	
    	/////////////////////////////////////////////////////
    	// Retry with administrator credentials.
    	// Should fail too.
    	/////////////////////////////////////////////////////
    	expected = false;
    	try {
    		client.setAnonymousPrefered(false);
    		client.setProxy(testInfo.getSysAdminUser());
    		client.createIdentifier(null);
    	} catch(NamingAuthoritySecurityFault e) {
    		System.err.println(e.getFaultString());
    		expected = true;
    	}
    	if (!expected) {
    		fail("Expected NamingAuthoritySecurityFault was not raised");
    	}
    	
    	/////////////////////////////////////////////////////
    	// Authorize some one to create identifiers by adding
    	// the corresponding identity to 
    	// IDENTIFIERS_CREATION_USERS.
    	/////////////////////////////////////////////////////
    	
    	// Authorize User B to create identifiers
    	IdentifiersTestUtil.createIdentifierCreationUsers(testInfo, 
    			new String[] {testInfo.getUserB().getIdentity()});
    	
    	// Use User B credentials to create identifier
    	// Should succeed now.
    	try {
    		client.setAnonymousPrefered(false);
    		client.setProxy(testInfo.getUserB());
    		URI identifier = client.createIdentifier(null);
    		System.out.println("User B successfully created " + identifier.toString());
    	} catch(Exception e) {
    		e.printStackTrace();
    		fail("Unexpected exception: " + e.toString());
    	}
    	
    	/////////////////////////////////////////////////
    	// Rollback changes to system identifier back
    	// to default settings
    	/////////////////////////////////////////////////
    	client.setAnonymousPrefered(false);
    	client.setProxy(testInfo.getSysAdminUser());
    	client.deleteKeys(testInfo.getSystemIdentifier(), new String[]{Keys.IDENTIFIER_CREATION_USERS});
    	
    	IdentifiersTestUtil.changePublicIdentifierCreation(testInfo, 
    			SecurityUtil.PUBLIC_CREATION_YES);
    }
}
