package org.cagrid.identifiers.test.system;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;

import java.rmi.RemoteException;

import namingauthority.IdentifierValues;
import namingauthority.KeyData;
import namingauthority.KeyValues;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.globus.gsi.GlobusCredential;

public class IdentifiersTestUtil {

	private static void setProxy(IdentifiersNAServiceClient client,
			GlobusCredential creds) {
		
		if (creds != null) {
    		client.setAnonymousPrefered(false);
    		client.setProxy(creds);
    	
    	} else {
    		client.setAnonymousPrefered(true);
    	}
	}
	
	////////////////////////////////////////////////////////
	// Replaces keyName's values with the new keyValues in
	// the given identifier
	////////////////////////////////////////////////////////
	public static void replaceKey(IdentifiersNAServiceClient client,
			GlobusCredential creds, URI identifier, String keyName, 
			String[] keyValues ) 
		throws 
			InvalidIdentifierFault, 
			NamingAuthorityConfigurationFault, 
			NamingAuthoritySecurityFault, 
			InvalidIdentifierValuesFault, 
			RemoteException {
		
    	setProxy(client, creds);
    	
		KeyValues[] newKeyValues = new KeyValues[1];
    	newKeyValues[0] = new KeyValues();
    	newKeyValues[0].setKey(keyName);
    	newKeyValues[0].setKeyData(new KeyData(null, keyValues));

   		client.replaceKeys(identifier, new IdentifierValues(newKeyValues));
	}
	
	////////////////////////////////////////////////////////
	// Adds keyName with keyValues to given identifier
	////////////////////////////////////////////////////////
	public static void createKey(IdentifiersNAServiceClient client, 
			GlobusCredential creds, URI identifier, String keyName, 
			String[] keyValues ) 
		throws 
			InvalidIdentifierFault, 
			NamingAuthorityConfigurationFault, 
			NamingAuthoritySecurityFault, 
			InvalidIdentifierValuesFault, 
			RemoteException {
		
		setProxy(client, creds);
		
    	KeyValues[] newKeyValues = new KeyValues[1];
    	newKeyValues[0] = new KeyValues();
    	newKeyValues[0].setKey(keyName);
    	newKeyValues[0].setKeyData(new KeyData(null, keyValues));

   		client.createKeys(identifier, new IdentifierValues(newKeyValues));
	}
	
	////////////////////////////////////////////////////////
	// Deletes keys from given identifier
	////////////////////////////////////////////////////////
	public static void deleteKeys(IdentifiersNAServiceClient client, 
			GlobusCredential creds, URI identifier, String[] keyList ) 
		throws 
			InvalidIdentifierFault, 
			NamingAuthorityConfigurationFault, 
			NamingAuthoritySecurityFault, 
			InvalidIdentifierValuesFault, 
			RemoteException {
		
		setProxy(client, creds);
   		client.deleteKeys(identifier, keyList);
	}
	
	/////////////////////////////////////////////////////////
	// Creates identifier with provided values
	public static URI createIdentifier(IdentifiersNAServiceClient client,
			GlobusCredential creds, String[] keys, String[][] keysValues) 
		throws 
			NamingAuthorityConfigurationFault, 
			InvalidIdentifierFault, 
			NamingAuthoritySecurityFault, 
			InvalidIdentifierValuesFault, 
			RemoteException {
		
		setProxy(client, creds);
		
		KeyValues[] kvs = new KeyValues[ keys.length ];
		for( int i=0; i < keys.length; i++) {
			kvs[i] = new KeyValues();
			kvs[i].setKey(keys[i]);
			kvs[i].setKeyData(new KeyData(null, keysValues[i]));
		}
		
		return client.createIdentifier(new IdentifierValues(kvs));
	}
	
	///////////////////////////////////////////////////
	// Use system identifier administrator
	// credentials to change the PUBLIC_CREATION value
	///////////////////////////////////////////////////
	public static void changePublicIdentifierCreation(IdentifiersTestInfo testInfo,
			String publicCreation) throws MalformedURIException, RemoteException {

		EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
          
        IdentifiersTestUtil.replaceKey(client, testInfo.getSysAdminUser(),
        		testInfo.getSystemIdentifier(), Keys.PUBLIC_CREATION, new String[]{publicCreation});
	}
	
	///////////////////////////////////////////////////
	// Use system identifier administrator
	// credentials to create IDENTIFIER_CREATION_USERS
	///////////////////////////////////////////////////
	public static void createIdentifierCreationUsers(IdentifiersTestInfo testInfo,
			String[] identifierCreationUsers) throws MalformedURIException, RemoteException {

		EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
        
        IdentifiersTestUtil.createKey(client, testInfo.getSysAdminUser(), 
        		testInfo.getSystemIdentifier(), 
        		Keys.IDENTIFIER_CREATION_USERS, identifierCreationUsers);
	}
}
