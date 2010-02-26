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
 * Case 1) Deleting security-type keys as defined by Keys.isAdminKey()
 * 		A user can delete security-type keys if s/he is explicitly
 * 		listed as an ADMIN_USER by either the identifier, or the system
 * 		(root) identifier.
 * 
 * Case 2) Deleting other keys
 * 		A user can delete other keys if s/he is listed as a WRITE_USER
 * 		by the identifier, or if a WRITE_USERS key is not configured
 * 		by the identifier (or a READWRITEIDENTIFIER referred by the
 * 		identifier)
 *
 *		Identifier's ADMIN_USERS can create keys of any type. It is
 *		unnecessary to list them as WRITE_USERS.
 */
public class DeleteKeysSecurityStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public DeleteKeysSecurityStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    /*********************************************************************
     * Summary of steps
     *
     * 1) Create identifier to test with
     *
     * 2) Show that, by default, any one can delete non-security keys
     *
     * 3) Show that anonymous users can't delete security keys
     *
     * 4) Show administrators can delete security keys
     *
     * 5) How to prevent regular users from deleting any keys 
     * (empty WRITE_USERS)
     *
     * 6) Show anonymous users can't delete keys now
     *
     * 7) Show regular users (e.g., User B) can't delete keys now
     *
     * 8) Show that, at this point, only administrators can delete keys
     *
     * 9) Add User B as identifier's writer
     *
     * 10) Show that User B can delete keys now
     *
     * 11) Show that User C can't
     *
     * 12) Create READWRITE identifier defining User C as WRITE_USER
     *
     * 13) Add READWRITE identifier reference (12) to our identifier
     *
     * 14) Verify that User C can now delete keys. This shows the system
     * adds WRITE_USERS from READWRITE_IDENTIFIERS to those directly
     * specified directly under the identifier
     *
     * 15) Show that regular users (e.g., User B) can't delete 
     * security keys, even if they are WRITE_USERS
     *
     * 16) Make User B an identifier administrator
     *
     * 17) Show User B is now able to delete security keys
     *
     * 18) Show that User C can't
     *
     * 19) Make User C an identifier administrator by using
     * a reference to an admin identifier
     *
     * 20) Verify that User C can now delete security keys. This shows
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
        URI identifier = IdentifiersTestUtil.createIdentifier(client, null, 
        		new String[] { "AGENT1", "AGENT2", "AGENT3", "AGENT4", Keys.READ_USERS },
        		new String[][] { {"007"}, {"008"}, {"009"}, {"010"}, {} });
        System.out.println(identifier.toString());
        
        ////////////////////////////////////////////////////////////////
        // By default, any one can delete non-security keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.deleteKeys(client, null, identifier, 
        		new String[]{"AGENT2"}); 
        
        ////////////////////////////////////////////////////////////////
        // Anonymous users can't delete security keys
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.deleteKeys(client, null, identifier, 
        			new String[]{ Keys.READ_USERS });
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // Administrators can delete security keys
        // The following change will prevent regular users from
        // deleting any keys (empty WRITE_USERS)
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		identifier, Keys.WRITE_USERS, null);
        
        ////////////////////////////////////////////////////////////////
        // Anonymous users can't delete keys now
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.deleteKeys(client, null, identifier, 
        			new String[]{"AGENT1"});
        
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // User B (for example) shouldn't be able to delete keys either
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
        	IdentifiersTestUtil.deleteKeys(client, testInfo.getUserB(), 
        			identifier, new String[]{"AGENT1"});
        
        } catch( NamingAuthoritySecurityFault e) {
        	System.err.println(e.getFaultString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        ////////////////////////////////////////////////////////////////
        // At this point, only an administrator can delete keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.deleteKeys(client, testInfo.getSysAdminUser(), 
        		identifier, new String[]{"AGENT1"});
        
        ////////////////////////////////////////////////////////////////
        // Add User B as identifier's writer
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.replaceKey(client, testInfo.getSysAdminUser(), identifier, 
        		Keys.WRITE_USERS, new String[]{testInfo.getUserB().getIdentity()});
        
        ////////////////////////////////////////////////////////////////
        // User B can delete keys now
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.deleteKeys(client, testInfo.getUserB(), 
        		identifier, new String[]{"AGENT3"});
        
        ////////////////////////////////////////////////////////////////
        // But User C can't
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
            IdentifiersTestUtil.deleteKeys(client, testInfo.getUserC(),
            		identifier, new String[]{"AGENT4"});
        
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
        // Now User C should be able to delete keys. This shows the system
        // adds WRITE_USERS from READWRITE_IDENTIFIERS to those directly
        // specified directly under the identifier
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.deleteKeys(client, testInfo.getUserC(),
            		identifier, new String[]{"AGENT4"});
        
        ////////////////////////////////////////////////////////////////
        // Regular users (e.g., User B) can't delete security keys,
        // even if they are WRITE_USERS
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
            IdentifiersTestUtil.deleteKeys(client, testInfo.getUserB(),
            		identifier, new String[]{Keys.READ_USERS});
        
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
        // User B is now able to delete security keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.deleteKeys(client, testInfo.getUserB(), 
        		identifier, new String[]{Keys.READ_USERS});
        
        IdentifiersTestUtil.createKey(client, testInfo.getUserB(), identifier,
        		Keys.READ_USERS, new String[]{testInfo.getUserB().getIdentity()});
        
        ////////////////////////////////////////////////////////////////
        // But User C still can't
        ////////////////////////////////////////////////////////////////
        expected = false;
        try {
            IdentifiersTestUtil.deleteKeys(client, testInfo.getUserC(),
            		identifier, new String[]{Keys.READ_USERS});
        
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
        // User C can now delete admin keys
        ////////////////////////////////////////////////////////////////
        IdentifiersTestUtil.deleteKeys(client, testInfo.getUserC(),
        		identifier, new String[]{Keys.READ_USERS});
    }
}
