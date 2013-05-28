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
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;
import org.cagrid.identifiers.test.system.IdentifiersTestUtil;

/* 
 * Identifier's ADMIN_USERS can replace any key values
 * 
 * Security-type key values can only be replaced by ADMIN_USERS
 * 
 * Regular users can replace the values for a key if s/he is listed as
 * a WRITE_USER for that key.
 * 
 * When no WRITE_USERS key is defined at the key level, the user
 * must be listed as a WRITE_USER for the identifier.
 * 
 * When no WRITE_USERS key is defined for either the key or 
 * the identifier, any user can replace the values. 
 *
 */
public class ReplaceKeysSecurityStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public ReplaceKeysSecurityStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    /*********************************************************************
     * Summary of steps
     *
     * 1) Create identifier to test with
     * 
     * 2) With no security settings (no WRITE_USERS), any one can
     * replace non-security keys
     * 
     * 3) Regular users can't replace security key values
     * 
     * 4) Add empty WRITE_USERS list to identifier, so that no regular 
     * users can replace keys
     * 
     * 5) Show anonymous users can't replace key
     * 
     * 6) Show regular users (e.g.User B) can't replace key
     * 
     * 7) Create policy identifier to authorize User B
     * 
     * 8) User B can now replace the "CODE" key
     * 
     * 9) But User C still can't
     * 
     * 10) Add User C as identifier WRITE_USER
     * 
     * 11) User C still can't replace the "CODE" key. This is because key's
     * security settings completely overwrite any settings at the
     * identifier level.
     * 
     * 12) Add User C as key's WRITE_USERS
     * 
     * 13) User C can now replace CODE's values
     *********************************************************************/
    @Override
    public void runStep() throws Exception {
    	
    	boolean expected;
    	EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        
        ////////////////////////////////////////////////////////////////
        // Create identifier to test with
        ////////////////////////////////////////////////////////////////
        URI identifier = IdentifiersTestUtil.createIdentifier(client, null, 
        		new String[] { "CODE", Keys.READ_USERS},
        		new String[][] { {"1"}, {} });
        System.out.println(identifier.toString());
        
        ////////////////////////////////////////////////////////////////
        // With no security settings (no WRITE_USERS), any one can 
        // replace non-security keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.replaceKeyValues(client, null, identifier, 
        		"CODE", new String[]{"2"});
        
        ////////////////////////////////////////////////////////////////
        // Regular users can't replace security key values
        ////////////////////////////////////////////////////////////////   
        expected = false;
        try {
        	IdentifiersTestUtil.replaceKeyValues(client, null, identifier, 
        			Keys.READ_USERS, new String[]{ "hacker" });
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // Add identifiers WRITE_USERS so that no regular users can
        // replace keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(),
        		identifier, Keys.WRITE_USERS, new String[]{});
        
        ////////////////////////////////////////////////////////////////
        // Anonymous user can't replace key
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.replaceKeyValues(client, null, identifier, 
        			"CODE", new String[]{ "2" });
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // User B can't replace key
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.replaceKeyValues(client, testInfo.getUserB(), 
        			identifier, "CODE", new String[]{ "2" });
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // Create policy identifier to authorize User B
        ////////////////////////////////////////////////////////////////
        URI policyIdentifier = IdentifiersTestUtil.createIdentifier(client, 
        		null, 
        		new String[] { Keys.WRITE_USERS },
        		new String[][] { {testInfo.getUserB().getIdentity()} });
        
        // We can't currently update the policy identifier
        // So delete the key and re-create it
        IdentifiersTestUtil.deleteKeys(client, testInfo.getSysAdminUser(),
        		identifier, new String[]{"CODE"});
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(),
        		identifier, "CODE", policyIdentifier,
        		new String[]{"007"});
        
        ////////////////////////////////////////////////////////////////
        // User B can now replace the "CODE" key
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.replaceKeyValues(client, testInfo.getUserB(), 
        		identifier, "CODE", new String[]{ "4" });
        
        ////////////////////////////////////////////////////////////////
        // But User C still can't
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.replaceKeyValues(client, testInfo.getUserC(), 
        			identifier, "CODE", new String[]{ "2" });
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // Add User C as identifier WRITE_USER
        ////////////////////////////////////////////////////////////////

        IdentifiersTestUtil.deleteKeys(client, testInfo.getSysAdminUser(),
        		identifier, new String[]{Keys.WRITE_USERS});
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(),
        		identifier, Keys.WRITE_USERS,  
        		new String[]{testInfo.getUserC().getIdentity()});
        
        ////////////////////////////////////////////////////////////////
        // User C still can't replace the "CODE" key. This is because key's
        // security settings completely overwrite any settings at the
        // identifier level.
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.replaceKeyValues(client, testInfo.getUserC(), 
        			identifier, "CODE", new String[]{ "2" });
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // Add User C as key's WRITE_USERS
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.deleteKeys(client, testInfo.getSysAdminUser(),
        		policyIdentifier, new String[]{Keys.WRITE_USERS});
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(),
        		policyIdentifier, Keys.WRITE_USERS, new String[]{
        			testInfo.getUserC().getIdentity()});
        
      
        ////////////////////////////////////////////////////////////////
        // User C can now replace CODE's values
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.replaceKeyValues(client, testInfo.getUserC(), 
        		identifier, "CODE", new String[]{ "2" });
    }
}
