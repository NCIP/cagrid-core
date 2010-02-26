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
 * Case 1) Creating security-type keys as defined by Keys.isAdminKey()
 * 		A user can create security-type keys if s/he is explicitly
 * 		listed as an ADMIN_USER by either the identifier, or the system
 * 		(root) identifier.
 * 
 * Case 2) Creating other keys
 * 		A user can create other keys if s/he is listed as a WRITE_USER
 * 		by the identifier, or if a WRITE_USERS key is not configured
 * 		by the identifier (or a READWRITEIDENTIFIER referred by the
 * 		identifier)
 *
 *		Identifier's ADMIN_USERS can create keys of any type. It is
 *		unnecessary to list them as WRITE_USERS.
 */
public class CreateKeysSecurityStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public CreateKeysSecurityStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    /*********************************************************************
     * Summary of steps
     *
     * 1) Create identifier to test with
     *
     * 2) Show that, by default, any one can create non-security keys
     *
     * 3) Show that anonymous users can't create security keys
     *
     * 4) Show administrators can create security keys
     *
     * 5) How to prevent regular users from creating any keys 
     * (empty WRITE_USERS)
     *
     * 6) Show anonymous users can't create keys now
     *
     * 7) Show regular users (e.g., User B) can't create keys now
     *
     * 8) Show that, at this point, only administrators can create keys
     *
     * 9) Add User B as identifier's writer
     *
     * 10) Show that User B can create keys now
     *
     * 11) Show that User C can't
     *
     * 12) Create READWRITE identifier defining User C as WRITE_USER
     *
     * 13) Add READWRITE identifier reference (12) to our identifier
     *
     * 14) Verify that User C can now create keys. This shows the system
     * adds WRITE_USERS from READWRITE_IDENTIFIERS to those directly
     * specified directly under the identifier
     *
     * 15) Show that regular users (e.g., User B) can't create 
     * security keys, even if they are WRITE_USERS
     *
     * 16) Make User B an identifier administrator
     *
     * 17) Show User B is now able to create security keys
     *
     * 18) Show that User C can't
     *
     * 19) Make User C an identifier administrator by using
     * a reference to an admin identifier
     *
     * 20) Verify that User C can now create security keys. This shows
     * the system adds ADMIN_USERS from ADMIN_IDENTIFIERS to those
     * directly specified under the identifier
     *********************************************************************/
    @Override
    public void runStep() throws Exception {
    	
    	boolean expected;
    	EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        
        ////////////////////////////////////////////////////////////////
        // Create identifier to test with
        ////////////////////////////////////////////////////////////////
        URI identifier = client.createIdentifier(null);
        System.out.println(identifier.toString());
        
        ////////////////////////////////////////////////////////////////
        // By default, any one can create non-security keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, null, identifier, 
        		"AGENT", new String[]{"007"});
        
        ////////////////////////////////////////////////////////////////
        // Anonymous users can't create security keys
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.createKey(client, null, identifier, 
        			Keys.WRITE_USERS, null);
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // Administrators can create security keys
        // The following change now prevent regular users from
        // creating any keys (empty WRITE_USERS)
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		identifier, Keys.WRITE_USERS, null);
        
        ////////////////////////////////////////////////////////////////
        // Anonymous users can't create keys now
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.createKey(client, null, identifier, "CODE", null);
        
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // User B (for example) shouldn't be able to create keys either
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.createKey(client, testInfo.getUserB(), identifier, "CODE", null);
        
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // At this point, only an administrator can add keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		identifier, "CODE", new String[]{"007"});
        
        ////////////////////////////////////////////////////////////////
        // Add User B as identifier's writer
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.replaceKey(client, testInfo.getSysAdminUser(), identifier, 
        		Keys.WRITE_USERS, new String[]{testInfo.getUserB().getIdentity()});
        
        ////////////////////////////////////////////////////////////////
        // User B can create keys now
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getUserB(), 
        		identifier, "WEB", new String[]{"PAGE"});
        
        ////////////////////////////////////////////////////////////////
        // But User C can't
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
            IdentifiersTestUtil.createKey(client, testInfo.getUserC(),
            		identifier, "TEXT", null);
        
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // Let's create a READWRITEIDENTIFIER that authorizes User C to write
        ////////////////////////////////////////////////////////////////
        URI rwIdentifier = IdentifiersTestUtil.createIdentifier(client, null, 
        		new String[] { Keys.WRITE_USERS },
        		new String[][] { {testInfo.getUserC().getIdentity()} });
        
        // Add READWRITEIDENTIFIER reference to our identifier
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		identifier, Keys.READWRITE_IDENTIFIERS,
        		new String[] { rwIdentifier.toString() });
        
        ////////////////////////////////////////////////////////////////
        // Now User C should be able to create keys. This shows the system
        // adds WRITE_USERS from READWRITE_IDENTIFIERS to those directly
        // specified directly under the identifier
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getUserC(),
            		identifier, "TEXT", null);
        
        ////////////////////////////////////////////////////////////////
        // Regular users (e.g., User B) can't create security keys,
        // even if they are WRITE_USERS
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
            IdentifiersTestUtil.createKey(client, testInfo.getUserB(),
            		identifier, Keys.READ_USERS, null);
        
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // Let's make User B an identifier administrator
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), identifier,
        		Keys.ADMIN_USERS, new String[]{testInfo.getUserB().getIdentity()});
        
        ////////////////////////////////////////////////////////////////
        // User B is now able to create security keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getUserB(),
          		identifier, Keys.READ_USERS, null);
        IdentifiersTestUtil.deleteKeys(client, testInfo.getUserB(), 
        		identifier, new String[]{Keys.READ_USERS});
        
        ////////////////////////////////////////////////////////////////
        // But User C still can't
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
            IdentifiersTestUtil.createKey(client, testInfo.getUserC(),
            		identifier, Keys.READ_USERS, null);
        
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
      
        ////////////////////////////////////////////////////////////////
        // Let's make User C an identifier administrator by using
        // a reference to an admin identifier
        ////////////////////////////////////////////////////////////////
        
        // We create the identifier first
        URI adminIdentifier = IdentifiersTestUtil.createIdentifier(client, null, 
        		new String[] { Keys.ADMIN_USERS },
        		new String[][] { {testInfo.getUserC().getIdentity()} });
        
        // Now add reference to our identifier
        IdentifiersTestUtil.createKey(client, testInfo.getUserB(), 
        		identifier, Keys.ADMIN_IDENTIFIERS,
        		new String[] { adminIdentifier.toString() });
        
        ////////////////////////////////////////////////////////////////
        // User C can now create admin keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getUserC(),
        		identifier, Keys.READ_USERS, null);
    }
}
