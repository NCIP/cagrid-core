package gov.nih.nci.cagrid.identifiers.common;

import java.util.Arrays;

import junit.framework.Assert;
import namingauthority.IdentifierValues;
import namingauthority.KeyValues;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;

import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;

public class IdentifiersNAUtil {

	public static org.cagrid.identifiers.namingauthority.domain.IdentifierValues map(
			namingauthority.IdentifierValues identifierValues) {
		
		if (identifierValues == null)
			return null;
		
		org.cagrid.identifiers.namingauthority.domain.IdentifierValues ivs = 
			new org.cagrid.identifiers.namingauthority.domain.IdentifierValues();
		
		for( namingauthority.KeyValues kvs : identifierValues.getKeyValues() ) {
			ivs.set( kvs.getKey(), kvs.getValue() );
		}
		
		return ivs;
	}

	public static namingauthority.IdentifierValues map(
			org.cagrid.identifiers.namingauthority.domain.IdentifierValues identifierValues) {
		
		String[] keys = identifierValues.getKeys();
		namingauthority.KeyValues[] kvs = new namingauthority.KeyValues[ keys.length ];
		
		for( int i=0; i < kvs.length; i++) {
			kvs[i] = new namingauthority.KeyValues();
			kvs[i].setKey(keys[i]);
			kvs[i].setValue(identifierValues.getValues(keys[i]));
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
	
	public static void assertEquals( IdentifierValues values1, IdentifierValues values2 ) {
		IdentifiersNAUtil.assertEquals( values1.getKeyValues(), values2.getKeyValues() );
    }
	
	public static void assertEquals(KeyValues[] tvs1, KeyValues[] tvs2) {
		
		// Make sure the Keys match
		if (!Arrays.equals(getSortedKeys(tvs1), getSortedKeys(tvs2))) {
			Assert.fail("keys are not the same");
		}

		for( KeyValues tv : tvs1 ) {
			IdentifiersNAUtil.assertEquals(tvs2, tv.getKey(), tv.getValue());
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
				String[] myValues = tv.getValue();
				Arrays.sort(values);
				Arrays.sort(myValues);
				Assert.assertEquals("values are not the same", true, Arrays.equals(values, myValues));
			}
		}
	}
}
