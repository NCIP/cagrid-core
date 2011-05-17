package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.resolver.Resolver;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;
import org.cagrid.identifiers.test.system.IdentifiersTestUtil;


public class HttpGSIResolutionStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public HttpGSIResolutionStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    /*********************************************************************
     * Summary of steps
     *
     * 1) Create identifier to test with
     * 
     * 2) By default, any one can retrieve all keys 
     * 
     * 3) Block read access to anonymous and regular users
     * 
     * 4) Add User B as identifier administrator
     * 
     * 5) User B can now resolve
     * 
     * 6) But User C can't
     * 
     * 7) Create policy identifier defining C as READ_USER
     * 
     * 8) User C can now resolve (AGENT1 only)
     * 
     * 9) Add User C as identifier administrator using an 
     * ADMIN_IDENTIFIER
     * 
     * 10) User C can now fully resolve the identifier
     *********************************************************************/
    @Override
    public void runStep() throws Exception {
    	
    	boolean expected;
    	EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        Resolver resolver = new Resolver();
        
        /***************************************************************
         * Create identifier to test with
         ***************************************************************/
        URI identifierURI = IdentifiersTestUtil.createIdentifier(client, null, 
        		new String[] { "AGENT1", "AGENT2", Keys.WRITE_USERS },
        		new String[][] { {"001"}, {"002"}, {} });
        java.net.URI identifier = java.net.URI.create(identifierURI.toString());
        System.out.println(identifier.toString());
        
        /***************************************************************
         *  By default, any one can retrieve all keys 
         ***************************************************************/
        IdentifierData data = resolver.resolveHttp(identifier);
        IdentifiersTestUtil.assertKey("AGENT1", data.getKeys());
        IdentifiersTestUtil.assertKey("AGENT2", data.getKeys());
        IdentifiersTestUtil.assertKey(Keys.WRITE_USERS, data.getKeys());
        
        /***************************************************************
         * Block read access to anonymous and regular users
         ***************************************************************/
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		identifierURI, Keys.READ_USERS, new String[]{});
        
        expected = false;
        try {
        	/* anonymous */
        	resolver.resolveHttp(identifier); 
        } catch( NamingAuthoritySecurityException e) {
        	System.err.println(e.toString());
        	expected = true;
        }
        catch(Exception e) {
        	e.printStackTrace();
        	System.err.println("Caugth unexpected exception: " + e);
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }
        
        expected = false;
        try {
        	/* User B */
        	resolver.resolveHttp(identifier, testInfo.getUserB()); 
        } catch( NamingAuthoritySecurityException e) {
        	System.err.println(e.toString());
        	expected = true;
        }
        if (!expected) {
        	fail("Expected NamingAuthoritySecurity fault was not raised");
        }

        /****************************************************************
         * Add User B as identifier administrator
         ****************************************************************/
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		identifierURI, Keys.ADMIN_USERS, 
        		new String[]{testInfo.getUserB().getIdentity()});
        
        /****************************************************************
         * User B can now resolve
         ****************************************************************/
        resolver.resolveHttp(identifier, testInfo.getUserB());
        
        /****************************************************************
         * But User C can't
         ****************************************************************/
        expected = false;
        try {
        	resolver.resolveHttp(identifier, testInfo.getUserC());
        } catch( NamingAuthoritySecurityException e) {
        	System.err.println(e.toString());
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
        		identifierURI, new String[]{"AGENT1"});
        IdentifiersTestUtil.createKey(client, testInfo.getUserB(), 
        		identifierURI, "AGENT1", policyIdentifier, 
        		new String[]{"001"});  
        
        /****************************************************************
         * User C can now resolve (AGENT1 only)
         ****************************************************************/
        data = resolver.resolveHttp(identifier, testInfo.getUserC());
        if (data.getKeys().length != 1) {
        	fail("Unexpected number of keys returned");
        }
        IdentifiersTestUtil.assertKey("AGENT1", data.getKeys());
        
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
        		identifierURI, Keys.ADMIN_IDENTIFIERS, 
        		new String[]{adminIdentifier.toString()});
        
        /****************************************************************
         * User C can now fully resolve the identifier
         ****************************************************************/
        data = resolver.resolveHttp(identifier, testInfo.getUserC());
        if (data.getKeys().length != 6) {
        	fail("Unexpected number of keys returned");
        }        
    }
}
