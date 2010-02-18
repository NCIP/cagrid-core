package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.test.NamingAuthorityTestCaseBase;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;



public class NamingAuthoritySecurityTestCase extends NamingAuthorityTestCaseBase {

	private static IdentifierValues globalValues = null;
	
	static {
		globalValues = new IdentifierValues();
		
		globalValues.put("URL", new KeyData( null, 
				new String[]{"http://na.cagrid.org/foo", "http://na.cagrid.org/bar"} ));
		
		globalValues.put("CODE", new KeyData( null,
				new String[]{ "007" }));
	}
	
	////////////////////////////////////////////////////////
	// Resolve system identifier and look for expected keys
	////////////////////////////////////////////////////////
	public void testResolveSystemIdentifier() {
		
		IdentifierValues values = null;
		try {
			values = this.NamingAuthority.resolveIdentifier(null, getSystemIdentifier());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to resolve system identifier [" + e.getMessage() + "]");
		} 
		
		KeyData kd = values.getValues(Keys.PUBLIC_CREATION);
		if (kd == null || kd.getValues() == null || kd.getValues().size() != 1) {
			fail("No PUBLIC_CREATION flag defined under system identifier");
		}
		
		kd = values.getValues(Keys.ADMIN_USERS);
		if (kd == null || kd.getValues() == null) {
			fail("No ADMIN_USERS key defined under system identifier");
		} 
	}
	
	////////////////////////////////////////////////////////////
	// createKeys authorization error
	////////////////////////////////////////////////////////////
	public void testCreateKeys() {
		boolean gotexpected = false;
		
		IdentifierValues newKeys = new IdentifierValues();
		newKeys.put("A KEY", new KeyData(null, new String[]{ "A VALUE" }));
		try {
			this.NamingAuthority.createKeys(null, getSystemIdentifier(), newKeys);
		} catch (NamingAuthoritySecurityException e) {
			gotexpected = true;
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
	}
	
	///////////////////////////////////////////////////////////////
	// resolveIdentifier authorization error
	// (identifier security - READ_USERS)
	///////////////////////////////////////////////////////////////
	public void testResolveIdentifier1() {
		URI id = null;
		IdentifierValues values = new IdentifierValues();
		values.put("CODE", new KeyData(null, new String[]{"007"}));
		values.put(Keys.READ_USERS, null);
		try {
			id = this.NamingAuthority.createIdentifier(null, values);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
		
		boolean gotexpected = false;
		try {
			this.NamingAuthority.resolveIdentifier(null, id);
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
		
		gotexpected = false;
		try {
			this.NamingAuthority.getKeys(null, id);
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
		
		gotexpected = false;
		try {
			this.NamingAuthority.getKeyValues(null, id, "CODE");
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
	}
	
	///////////////////////////////////////////////////////////////
	// resolveIdentifier authorization error
	// (identifier security - READWRITE_IDENTIFIERS.READ_USERS)
	///////////////////////////////////////////////////////////////
	public void testResolveIdentifier2() {
		
		// Create security identifier
		URI rwIdentifier = null;
		IdentifierValues values = new IdentifierValues();
		values.put(Keys.READ_USERS, new KeyData(null, new String[]{"A"}));
		try {
			rwIdentifier = this.NamingAuthority.createIdentifier(null, values);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		// Create reference to security identifier
		URI id = null;
		values = new IdentifierValues();
		values.put("CODE", null);
		values.put(Keys.READWRITE_IDENTIFIERS, new KeyData(null, 
				new String[]{ rwIdentifier.normalize().toString() }));
		try {
			id = this.NamingAuthority.createIdentifier(null, values);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		boolean gotexpected = false;
		try {
			this.NamingAuthority.resolveIdentifier(null, id);
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
		
		gotexpected = false;
		try {
			this.NamingAuthority.getKeys(null, id);
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
		
		gotexpected = false;
		try {
			this.NamingAuthority.getKeyValues(null, id, "CODE");
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
	}
	
	///////////////////////////////////////////////////////////////
	// resolveIdentifier authorization error
	// (key security)
	///////////////////////////////////////////////////////////////
	public void testResolveIdentifier3() {
		
		// Create security identifier
		URI rwIdentifier = null;
		IdentifierValues values = new IdentifierValues();
		values.put(Keys.READ_USERS, new KeyData(null, new String[]{"A"}));
		try {
			rwIdentifier = this.NamingAuthority.createIdentifier(null, values);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		// Create reference to security identifier
		URI id = null;
		values = new IdentifierValues();
		values.put("CODE", new KeyData(rwIdentifier, new String[]{"008"}));
		try {
			id = this.NamingAuthority.createIdentifier(null, values);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		boolean gotexpected = false;
		try {
			this.NamingAuthority.resolveIdentifier(null, id);
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}	
		
		gotexpected = false;
		try {
			this.NamingAuthority.getKeys(null, id);
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
		
		gotexpected = false;
		try {
			this.NamingAuthority.getKeyValues(null, id, "CODE");
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			gotexpected = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		if (!gotexpected) {
			fail("Expected NamingAuthoritySecurityException was not raised");
		}
	}
	
//	////////////////////////////////////////////////////////////////////////////
//	// Test deleteKeys interface
//	////////////////////////////////////////////////////////////////////////////
//	public void testDeleteKeys() {
//		URI id = null;
//		IdentifierValues resolvedValues = null;
//		boolean gotexpected = false;
//		
//		try {
//			id = this.NamingAuthority.createIdentifier(null, globalValues);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail("Failed to create identifier");
//		} 
//		
//		////////////////////////////////////////////////////////////////////////////
//		// InvalidIdentifierValues (null)
//		//
//		gotexpected = false;
//		try {
//			this.NamingAuthority.deleteKeys(null, id, null);
//		} catch (InvalidIdentifierValuesException e) {
//			gotexpected = true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//		if (!gotexpected) {
//			fail("Expected InvalidIdentifierValuesException was not raised");
//		}
//		
//		////////////////////////////////////////////////////////////////////////////
//		// InvalidIdentifierValues (key doesn't exist)
//		//
//		String[] keyList = new String[]{ "wrongKeyName" };
//		gotexpected = false;
//		try {
//			this.NamingAuthority.deleteKeys(null, id, keyList);
//		} catch (InvalidIdentifierValuesException e) {
//			e.printStackTrace();
//			gotexpected = true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} 
//		if (!gotexpected) {
//			fail("Expected InvalidIdentifierValuesException was not raised");
//		}
//		
//		////////////////////////////////////////////////////////////////////////////
//		// This should be successful
//		//
//		keyList = new String[] { "CODE" };
//		try {
//			this.NamingAuthority.deleteKeys(null, id, keyList);
//			resolvedValues = this.NamingAuthority.resolveIdentifier(null, id);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//
//		if (resolvedValues.getValues("CODE") != null) {
//			fail("CODE still exists");
//		}
//		
//		if (resolvedValues.getValues("URL") == null) {
//			fail("URL is no longer present");
//		}
//	}
//	
//	//////////////////////////////////////////////////////////////////////////////
//	// Test replaceKeys interface
//	//////////////////////////////////////////////////////////////////////////////
//	public void testReplaceKeys() {
//		URI id = null;
//		IdentifierValues resolvedValues = null;
//		boolean gotexpected = false;
//		
//		try {
//			id = this.NamingAuthority.createIdentifier(null, globalValues);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail("Failed to create identifier");
//		} 
//		
//		////////////////////////////////////////////////////////////////////////////
//		// InvalidIdentifierValues (null)
//		//
//		try {
//			this.NamingAuthority.replaceKeys(null, id, null);
//		} catch (InvalidIdentifierValuesException e) {
//			//expected
//			gotexpected = true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//		if (!gotexpected) {
//			fail("Expected InvalidIdentifierValuesException was not raised");
//		}
//		
//		////////////////////////////////////////////////////////////////////////////
//		// InvalidIdentifierValues (key doesn't exist)
//		//
//		gotexpected = false;
//		IdentifierValues values = new IdentifierValues();
//		values.put("wrongKeyName", null);
//		try {
//			this.NamingAuthority.replaceKeys(null, id, values);
//		} catch (InvalidIdentifierValuesException e) {
//			gotexpected = true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} 
//		if (!gotexpected) {
//			fail("Expected InvalidIdentifierValuesException was not raised");
//		}
//		
//		////////////////////////////////////////////////////////////////////////////
//		// This should be successful
//		//
//		String newCode = "008";
//		values = new IdentifierValues();
//		values.put("CODE", new KeyData(null, new String[] {newCode}));
//		try {
//			this.NamingAuthority.replaceKeys(null, id, values);
//			resolvedValues = this.NamingAuthority.resolveIdentifier(null, id);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//
//		if (!resolvedValues.getValues("CODE").getValues().get(0).equals(newCode)) {
//			fail("Unexpected CODE");
//		}
//
//	}
	

}
