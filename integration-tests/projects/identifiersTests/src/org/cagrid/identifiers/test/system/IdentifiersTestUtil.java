package org.cagrid.identifiers.test.system;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;

import java.rmi.RemoteException;

import junit.framework.Assert;

import namingauthority.IdentifierData;
import namingauthority.IdentifierValues;
import namingauthority.KeyData;
import namingauthority.KeyNameData;
import namingauthority.KeyNameValues;
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
	public static void replaceKeyValues(IdentifiersNAServiceClient client,
			GlobusCredential creds, URI identifier, String keyName, 
			String[] keyValues ) 
		throws 
			InvalidIdentifierFault, 
			NamingAuthorityConfigurationFault, 
			NamingAuthoritySecurityFault, 
			InvalidIdentifierValuesFault, 
			RemoteException {
		
    	setProxy(client, creds);
    	
		KeyNameValues[] newKeyValues = new KeyNameValues[1];
    	newKeyValues[0] = new KeyNameValues();
    	newKeyValues[0].setKeyName(keyName);
    	newKeyValues[0].setKeyValues(new KeyValues(keyValues));

   		client.replaceKeyValues(identifier, new IdentifierValues(newKeyValues));
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
		
		createKey(client, creds, identifier, keyName, null, keyValues);
	}
	
	public static void createKey(IdentifiersNAServiceClient client, 
			GlobusCredential creds, URI identifier, String keyName, 
			URI policyIdentifier, String[] keyValues ) 
		throws 
			InvalidIdentifierFault, 
			NamingAuthorityConfigurationFault, 
			NamingAuthoritySecurityFault, 
			InvalidIdentifierValuesFault, 
			RemoteException {
		
		setProxy(client, creds);

   		client.createKeys(identifier, new IdentifierData(
			new KeyNameData[]{createKeyNameData(keyName, policyIdentifier, 
					keyValues)})); 
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
		
		KeyNameData[] kvs = new KeyNameData[ keys.length ];
		for( int i=0; i < keys.length; i++) {
			kvs[i] = createKeyNameData(keys[i], keysValues[i]);
		}
		
		return client.createIdentifier(new IdentifierData(kvs));
	}
	
	public static KeyNameData[] resolveIdentifier(IdentifiersNAServiceClient client, 
			GlobusCredential creds, URI identifier) 
		throws 
			NamingAuthorityConfigurationFault, 
			InvalidIdentifierFault, 
			NamingAuthoritySecurityFault, 
			RemoteException {
	
		setProxy(client, creds);
		
		IdentifierData values = client.resolveIdentifier(identifier);
		if (values == null) {
			return null;
		}
		
		return values.getKeyNameData();	
	}
	
	public static KeyNameData getKeyData(IdentifiersNAServiceClient client, 
			GlobusCredential creds, URI identifier, String keyName) 
		throws 
			NamingAuthorityConfigurationFault, 
			InvalidIdentifierFault, 
			NamingAuthoritySecurityFault,
			InvalidIdentifierValuesFault,
			RemoteException {
	
		setProxy(client, creds);
		
		return client.getKeyData(identifier, keyName);
	}
	
	///////////////////////////////////////////////////
	// Use system identifier administrator
	// credentials to change the PUBLIC_CREATION value
	///////////////////////////////////////////////////
	public static void changePublicIdentifierCreation(IdentifiersTestInfo testInfo,
			String publicCreation) throws MalformedURIException, RemoteException {

		EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr );
          
        IdentifiersTestUtil.replaceKeyValues(client, testInfo.getSysAdminUser(),
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

	public static KeyNameData createKeyNameData( String keyName,
			URI policyIdentifier, String[] values ) {
		KeyData kd = new KeyData();
		kd.setValue(values);
		kd.setPolicyIdentifier(policyIdentifier);
		return new KeyNameData(kd, keyName);
	}
	
	public static KeyNameData createKeyNameData( String keyName,
			String[] values ) {
		return createKeyNameData(keyName, null, values);
	}
	
	public static void assertKey(String keyName, KeyNameValues[] keyValues) {
		for( KeyNameValues kvs : keyValues ) {
			if (kvs.getKeyName().equals(keyName)) {
				return;
			}
		}
		
		Assert.fail("Key [" + keyName + "] not found in result set");
	}
	
	public static void assertKey(String keyName, String[] keys) {
		for( String key : keys ) {
			if (key.equals(keyName)) {
				return;
			}
		}
		
		Assert.fail("Key [" + keyName + "] not found in result set");
	}
	
	public static void assertKey(String keyName, KeyNameData[] knd) {
		for( KeyNameData kvs : knd ) {
			if (kvs.getKeyName().equals(keyName)) {
				return;
			}
		}
		
		Assert.fail("Key [" + keyName + "] not found in result set");
	}
}
