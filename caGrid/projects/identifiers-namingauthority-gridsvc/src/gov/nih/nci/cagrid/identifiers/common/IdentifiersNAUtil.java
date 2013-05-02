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
package gov.nih.nci.cagrid.identifiers.common;

import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;

import java.net.URISyntaxException;
import java.util.Arrays;

import junit.framework.Assert;

import namingauthority.IdentifierData;
import namingauthority.KeyNameData;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;

public class IdentifiersNAUtil {

	public static org.cagrid.identifiers.namingauthority.domain.IdentifierData map(
			namingauthority.IdentifierData identifierData) throws URISyntaxException {
		
		if (identifierData == null)
			return null;
		
		org.cagrid.identifiers.namingauthority.domain.IdentifierData ivs = 
			new org.cagrid.identifiers.namingauthority.domain.IdentifierData();
		
		if (identifierData.getKeyNameData() != null) {
			for( namingauthority.KeyNameData kd : identifierData.getKeyNameData() ) {
				ivs.put(kd.getKeyName(), map(kd));
			}
		}
		
		return ivs;
	}
	
	public static org.cagrid.identifiers.namingauthority.domain.IdentifierValues map(
			namingauthority.IdentifierValues identifierKeyValues) throws URISyntaxException {
		
		if (identifierKeyValues == null)
			return null;
		
		org.cagrid.identifiers.namingauthority.domain.IdentifierValues ivs = 
			new org.cagrid.identifiers.namingauthority.domain.IdentifierValues();
		
		if (identifierKeyValues.getKeyNameValues() != null) {
			for( namingauthority.KeyNameValues kv : identifierKeyValues.getKeyNameValues() ) {			
				ivs.put(kv.getKeyName(), map(kv));
			}
		}
		
		return ivs;
	}
	
	public static org.cagrid.identifiers.namingauthority.domain.KeyData map(
			namingauthority.KeyNameData kd) throws URISyntaxException {
	
		if (kd == null || kd.getKeyData() == null)
			return null;
		
		java.net.URI policyReference = null;
		if (kd.getKeyData().getPolicyIdentifier() != null)
			policyReference = new java.net.URI(kd.getKeyData().getPolicyIdentifier().toString());
		
		return new org.cagrid.identifiers.namingauthority.domain.KeyData(
				policyReference, kd.getKeyData().getValue());
	}
	
	public static org.cagrid.identifiers.namingauthority.domain.KeyValues map(
			namingauthority.KeyNameValues kv) throws URISyntaxException {
	
		if (kv == null || kv.getKeyValues() == null) {
			return null;
		}

		return new org.cagrid.identifiers.namingauthority.domain.KeyValues(
				kv.getKeyValues().getValue());
	}
	
	public static namingauthority.KeyNameData map( String keyName,
			org.cagrid.identifiers.namingauthority.domain.KeyData kd) 
		throws 
			URISyntaxException, 
			MalformedURIException {
	
		if (kd == null)
			return null;
		
		org.apache.axis.types.URI readWriteIdentifier = null;
		if (kd.getPolicyIdentifier() != null) {
			readWriteIdentifier = new org.apache.axis.types.URI(kd.getPolicyIdentifier().normalize().toString());
		}
	
		namingauthority.KeyData outKd = new namingauthority.KeyData();
		outKd.setPolicyIdentifier(readWriteIdentifier);
		outKd.setValue(kd.getValuesAsArray());
		
		return new namingauthority.KeyNameData(outKd, keyName);
	}
		
	public static namingauthority.IdentifierData map(
			org.cagrid.identifiers.namingauthority.domain.IdentifierData identifierValues) 
		throws 
			MalformedURIException, 
			URISyntaxException {
		
		if (identifierValues == null) {
			return null;
		}
		
		String[] keys = identifierValues.getKeys();
		namingauthority.KeyNameData[] kdArr = new namingauthority.KeyNameData[ keys.length ];
		for( int i=0; i < kdArr.length; i++) {
			kdArr[i] = map(keys[i], identifierValues.getValues(keys[i]));
		}
		
		return new namingauthority.IdentifierData( kdArr );
	}
	
	public static InvalidIdentifierFault map(InvalidIdentifierException e) {
		InvalidIdentifierFault out = new InvalidIdentifierFault();
		out.setFaultString(e.getMessage());
		out.setFaultDetailString(IdentifierUtil.getStackTrace(e));
		return out;
	}

	public static NamingAuthorityConfigurationFault map(NamingAuthorityConfigurationException e) {
		NamingAuthorityConfigurationFault out = new NamingAuthorityConfigurationFault();
		out.setFaultString(e.getMessage());
		out.setFaultDetailString(IdentifierUtil.getStackTrace(e));
		return out;
	}

	public static InvalidIdentifierValuesFault map(InvalidIdentifierValuesException e) {
		InvalidIdentifierValuesFault out = new InvalidIdentifierValuesFault();
		out.setFaultString(e.getMessage());
		out.setFaultDetailString(IdentifierUtil.getStackTrace(e));
		return out;
	}
	
	public static NamingAuthoritySecurityFault map(NamingAuthoritySecurityException e) {
		NamingAuthoritySecurityFault out = new NamingAuthoritySecurityFault();
		out.setFaultString(e.getMessage());
		out.setFaultDetailString(IdentifierUtil.getStackTrace(e));
		return out;
	}
	
	public static void assertEquals( IdentifierData values1, IdentifierData values2 ) {
		IdentifiersNAUtil.assertEquals( values1.getKeyNameData(), values2.getKeyNameData() );
    }
	
//	public static void assertEquals(KeyNameValues[] tvs1, KeyNameValues[] tvs2) {
//		
//		// Make sure the Keys match
//		if (!Arrays.equals(getSortedKeys(tvs1), getSortedKeys(tvs2))) {
//			Assert.fail("keys are not the same");
//		}
//
//		for( KeyNameValues tv : tvs1 ) {
//			String[] values = null;
//			if (tv.getKeyValues() != null && tv.getKeyValues().getValues() != null) {
//				values = tv.getKeyValues().getValues().getValue();
//			}
//			IdentifiersNAUtil.assertEquals(tvs2, tv.getKeyName(), values);
//		}
//	}
    
	public static void assertEquals(KeyNameData[] tvs1, KeyNameData[] tvs2) {
		
		// Make sure the Keys match
		if (!Arrays.equals(getSortedKeys(tvs1), getSortedKeys(tvs2))) {
			Assert.fail("keys are not the same");
		}

		for( KeyNameData tv : tvs1 ) {
			String[] values = null;
			if (tv.getKeyData() != null) {
				values = tv.getKeyData().getValue();
			}
			IdentifiersNAUtil.assertEquals(tvs2, tv.getKeyName(), values);
		}
	}
	
//    private static String[] getSortedKeys(KeyNameValues[] tvs) {
//		String[]keys = new String[ tvs.length ];
//		for(int i=0; i < tvs.length; i++) {
//			keys[i] = tvs[i].getKeyName();
//		}
//
//		Arrays.sort(keys);
//		return keys;
//	}
    
    private static String[] getSortedKeys(KeyNameData[] tvs) {
		String[]keys = new String[ tvs.length ];
		for(int i=0; i < tvs.length; i++) {
			keys[i] = tvs[i].getKeyName();
		}

		Arrays.sort(keys);
		return keys;
	}
    
//    private static void assertEquals(KeyNameValues[] tvs, String Key, String[] values) {
//		for( KeyNameValues tv : tvs ) {
//			if (tv.getKeyName().equals(Key)) {
//				String[] myValues = null;
//				if (tv.getKeyValues() != null && tv.getKeyValues().getValues() != null) {
//					myValues = tv.getKeyValues().getValues().getValue();
//				}
//				
//				Arrays.sort(values);
//				Arrays.sort(myValues);
//				Assert.assertEquals("values are not the same", true, Arrays.equals(values, myValues));
//			}
//		}
//	}
    
    private static void assertEquals(KeyNameData[] tvs, String Key, String[] values) {
		for( KeyNameData tv : tvs ) {
			if (tv.getKeyName().equals(Key)) {
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
