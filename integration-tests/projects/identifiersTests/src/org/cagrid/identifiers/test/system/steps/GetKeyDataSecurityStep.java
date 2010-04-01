package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import namingauthority.IdentifierValues;
import namingauthority.KeyNameData;
import namingauthority.KeyValues;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;
import org.cagrid.identifiers.test.system.IdentifiersTestUtil;

/*
 * A user can read a key from an identifier if any one of the below 
 * conditions are met:
 *
 *    (a) User is identifier's administrator
 *       - User is listed by ADMIN_USERS key, or
 *       - User is listed by ADMIN_IDENTIFIERS's ADMIN_USERS key, or
 *       - User is listed by root identifier's ADMIN_USERS key
 *
 *    (b) User is listed by the key's policy identifier (READ_USERS list)
 *    (c) Key has no policy identifier's READ_USERS and user is listed by identifier's READ_USERS
 *    (d) Key has no policy identifier's READ_USERS and user is listed by identifier's READWRITE_IDENTIFIERS.READ_USERS
 *    (e) No READ_USERS keys at any level (key & identifier)
 *
 * A security exception is thrown if the identifier has keys and none are returned due to
 * permission checks.
 */
public class GetKeyDataSecurityStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public GetKeyDataSecurityStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    /*********************************************************************
     * Summary of steps
     * 
     * 1) Create identifier to test with
     * 
     * 2) By default, any one can retrieve any key
     * 
     * 3) Block read access to anonymous and regular users
     * 
     * 4) Add User B as identifier administrator
     * 
     * 5) User B can now resolve AGENT1
     * 
     * 6) But User C can't
     * 
     *  7) Create policy identifier defining C as READ_USER
     *  
     *  8) User C can now resolve AGENT1
     *  
     *  9) But will fail to resolve AGENT2
     *  
     *  10) Add User C as identifier administrator using an 
     *  ADMIN_IDENTIFIER
     *  
     *  11) User C can now fully resolve AGENT2
     *********************************************************************/
    @Override
    public void runStep() throws Exception {
    	
    	boolean expected;
    	EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        
        /***************************************************************
         * Create identifier to test with
         ***************************************************************/
        URI identifier = IdentifiersTestUtil.createIdentifier(client, null, 
        		new String[] { "AGENT1", "AGENT2", "AGENT3", Keys.WRITE_USERS },
        		new String[][] { {"001"}, {"002"}, {"003"}, {} });
        System.out.println(identifier.toString());
        
        /***************************************************************
         *  By default, any one can retrieve any key
         ***************************************************************/
        KeyNameData knd = IdentifiersTestUtil.getKeyData(client, null, 
        		identifier, "AGENT1");
        if (!knd.getKeyName().equals("AGENT1")){
        	fail("Unexpected key retrieved");
        }
        
        /***************************************************************
         * Block read access to anonymous and regular users
         ***************************************************************/
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		identifier, Keys.READ_USERS, new String[]{});
        
        expected = false;
        try {
        	IdentifiersTestUtil.getKeyData(client, null, 
            		identifier, "AGENT1");
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }

        /****************************************************************
         * Add User B as identifier administrator
         ****************************************************************/
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		identifier, Keys.ADMIN_USERS, 
        		new String[]{testInfo.getUserB().getIdentity()});
        
        /****************************************************************
         * User B can now resolve AGENT1
         ****************************************************************/
        IdentifiersTestUtil.getKeyData(client, testInfo.getUserB(), 
        		identifier, "AGENT1");
        if (!knd.getKeyName().equals("AGENT1")){
        	fail("Unexpected key retrieved");
        }
        
        /****************************************************************
         * But User C can't
         ****************************************************************/
        expected = false;
        try {
        	IdentifiersTestUtil.getKeyData(client, testInfo.getUserC(), 
            		identifier, "AGENT1");
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        /****************************************************************
         * Create policy identifier defining C as READ_USER
         ****************************************************************/
        URI policyIdentifier = IdentifiersTestUtil.createIdentifier(client, null, 
        		new String[] { Keys.READ_USERS },
        		new String[][] { {testInfo.getUserC().getIdentity()}});
        
        // Add policy identifier reference to key AGENT1
        IdentifiersTestUtil.deleteKeys(client, testInfo.getUserB(), 
        		identifier, new String[]{"AGENT1"});
        IdentifiersTestUtil.createKey(client, testInfo.getUserB(), 
        		identifier, "AGENT1", policyIdentifier, 
        		new String[]{"001"});  
        
        /****************************************************************
         * User C can now resolve AGENT1
         ****************************************************************/
        knd = IdentifiersTestUtil.getKeyData(client, testInfo.getUserC(), 
        		identifier, "AGENT1");
        if (!knd.getKeyName().equals("AGENT1")){
        	fail("Unexpected key retrieved");
        }
        
        /****************************************************************
         * But will fail to resolve AGENT2
         ****************************************************************/
        expected = false;
        try {
        	IdentifiersTestUtil.getKeyData(client, testInfo.getUserC(), 
            		identifier, "AGENT2");
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }

        
        /****************************************************************
         * Add User C as identifier administrator using an 
         * ADMIN_IDENTIFIER
         ****************************************************************/
        URI adminIdentifier = IdentifiersTestUtil.createIdentifier(client, 
        		testInfo.getUserB(),
        		new String[]{Keys.ADMIN_USERS},
        		new String[][]{{testInfo.getUserC().getIdentity()}});
        
        // Add admin identifier reference to our test identifier
        IdentifiersTestUtil.createKey(client, testInfo.getUserB(), 
        		identifier, Keys.ADMIN_IDENTIFIERS, 
        		new String[]{adminIdentifier.toString()});
        
        /****************************************************************
         * User C can now fully resolve AGENT2
         ****************************************************************/
        knd = IdentifiersTestUtil.getKeyData(client, testInfo.getUserC(), 
        		identifier, "AGENT2");
        if (!knd.getKeyName().equals("AGENT2")){
        	fail("Unexpected key retrieved");
        }       
    }
}
