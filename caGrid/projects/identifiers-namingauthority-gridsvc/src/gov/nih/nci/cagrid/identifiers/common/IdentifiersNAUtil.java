package gov.nih.nci.cagrid.identifiers.common;

import java.net.URISyntaxException;
import java.util.Arrays;

import junit.framework.Assert;
import namingauthority.IdentifierValues;
import namingauthority.KeyValues;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;

import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;

public class IdentifiersNAUtil {

	public static org.cagrid.identifiers.namingauthority.domain.IdentifierValues map(
			namingauthority.IdentifierValues identifierValues) throws URISyntaxException {
		
		if (identifierValues == null)
			return null;
		
		org.cagrid.identifiers.namingauthority.domain.IdentifierValues ivs = 
			new org.cagrid.identifiers.namingauthority.domain.IdentifierValues();
		
		for( namingauthority.KeyValues kvs : identifierValues.getKeyValues() ) {
			ivs.put(kvs.getKey(), map(kvs.getKeyData()));
		}
		
		return ivs;
	}

	public static org.cagrid.identifiers.namingauthority.domain.KeyData map(
			namingauthority.KeyData kd) throws URISyntaxException {
	
		if (kd == null)
			return null;
		
		java.net.URI readWriteIdentifier = null;
		if (kd.getReadWriteIdentifier() != null)
			readWriteIdentifier = new java.net.URI(kd.getReadWriteIdentifier().toString());
		
		return new org.cagrid.identifiers.namingauthority.domain.KeyData(
					readWriteIdentifier, kd.getValue());
	}
	
	public static namingauthority.KeyData map(
			org.cagrid.identifiers.namingauthority.domain.KeyData kd) throws URISyntaxException, MalformedURIException {
	
		if (kd == null)
			return null;
		
		org.apache.axis.types.URI readWriteIdentifier = null;
		if (kd.getReadWriteIdentifier() != null) {
			readWriteIdentifier = new org.apache.axis.types.URI(kd.getReadWriteIdentifier().normalize().toString());
		}
	
		return new namingauthority.KeyData(
					readWriteIdentifier, kd.getValuesAsArray());
	}
	
	public static namingauthority.IdentifierValues map(
			org.cagrid.identifiers.namingauthority.domain.IdentifierValues identifierValues) throws MalformedURIException, URISyntaxException {
		
		if (identifierValues == null) {
			return null;
		}
		
		String[] keys = identifierValues.getKeys();
		namingauthority.KeyValues[] kvs = new namingauthority.KeyValues[ keys.length ];
		
		for( int i=0; i < kvs.length; i++) {
			kvs[i] = new namingauthority.KeyValues();
			kvs[i].setKey(keys[i]);
			kvs[i].setKeyData(map(identifierValues.getValues(keys[i])));
		}
				
		return new namingauthority.IdentifierValues( kvs );
	}
	
	public static InvalidIdentifierFault map(InvalidIdentifierException e) {
		InvalidIdentifierFault out = new InvalidIdentifierFault();
		out.setFaultString(e.getMessage());
		return out;
	}

	public static NamingAuthorityConfigurationFault map(NamingAuthorityConfigurationException e) {
		NamingAuthorityConfigurationFault out = new NamingAuthorityConfigurationFault();
		out.setFaultString(e.getMessage());
		return out;
	}

	public static InvalidIdentifierValuesFault map(InvalidIdentifierValuesException e) {
		InvalidIdentifierValuesFault out = new InvalidIdentifierValuesFault();
		out.setFaultString(e.getMessage());
		return out;
	}
	
	public static NamingAuthoritySecurityFault map(NamingAuthoritySecurityException e) {
		NamingAuthoritySecurityFault out = new NamingAuthoritySecurityFault();
		out.setFaultString(e.getMessage());
		return out;
	}
	
	public static void assertEquals( IdentifierValues values1, IdentifierValues values2 ) {
		IdentifiersNAUtil.assertEquals( values1.getKeyValues(), values2.getKeyValues() );
    }
	
	public static void assertEquals(KeyValues[] tvs1, KeyValues[] tvs2) {
		
		// Make sure the Keys match
		if (!Arrays.equals(getSortedKeys(tvs1), getSortedKeys(tvs2))) {
			Assert.fail("keys are not the same");
		}

		for( KeyValues tv : tvs1 ) {
			String[] values = null;
			if (tv.getKeyData() != null) {
				values = tv.getKeyData().getValue();
			}
			IdentifiersNAUtil.assertEquals(tvs2, tv.getKey(), values);
		}
	}
    
    private static String[] getSortedKeys(KeyValues[] tvs) {
		String[] Keys = new String[ tvs.length ];
		for(int i=0; i < tvs.length; i++) {
			Keys[i] = tvs[i].getKey();
		}

		Arrays.sort(Keys);
		return Keys;
	}
    
    private static void assertEquals(KeyValues[] tvs, String Key, String[] values) {
		for( KeyValues tv : tvs ) {
			if (tv.getKey().equals(Key)) {
				String[] myValues = null;
				if (tv.getKeyData() != null) {
					myValues = tv.getKeyData().getValue();
				}
				
				Arrays.sort(values);
				Arrays.sort(myValues);
				Assert.assertEquals("values are not the same", true, Arrays.equals(values, myValues));
			}
		}
	}
}
