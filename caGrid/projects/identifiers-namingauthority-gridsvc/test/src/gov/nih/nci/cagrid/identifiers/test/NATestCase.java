package gov.nih.nci.cagrid.identifiers.test;

import java.rmi.RemoteException;
import java.util.Arrays;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;

import namingauthority.IdentifierValues;
import namingauthority.KeyValues;

import org.apache.axis.client.Stub;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class NATestCase extends TestCase {
	private static Log log = LogFactory.getLog(NATestCase.class);

	private static final KeyValues[] keyValues;
	private static final String gridSvcUrl = "http://localhost:8081/wsrf/services/cagrid/IdentifiersNAService";

	static {
		keyValues = new KeyValues[2];
		keyValues[0] = new KeyValues();
		keyValues[0].setKey("URL");
		keyValues[0].setValue(new String[] { "http://www.google.com" });

		keyValues[1] = new KeyValues();
		keyValues[1].setKey("EPR");
		keyValues[1].setValue(new String[] { "end point reference 1", "end point reference 2" });
	}

	public void testNamingAuthorityGridService() throws Exception {
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );

		try {
			org.apache.axis.types.URI identifier = client.createIdentifier(new IdentifierValues(keyValues));
			System.out.println("Identifier: " + identifier.toString());

			IdentifierValues ivs2 = client.resolveIdentifier(identifier);
			if (!compare(keyValues, ivs2.getKeyValues())) {
				fail("IdentifierValues are different");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public void testInvalidIdentifier() throws Exception {
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );

		try {
			org.apache.axis.types.URI identifier = new URI("file://324324325");

			IdentifierValues ivs2 = client.resolveIdentifier(identifier);
			if (!compare(keyValues, ivs2.getKeyValues())) {
				fail("IdentifierValues are different");
			}
		}
		catch(InvalidIdentifierFault e) {
			//expected
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	private String[] getSortedKeys(KeyValues[] tvs) {
		String[] Keys = new String[ tvs.length ];
		for(int i=0; i < tvs.length; i++) {
			Keys[i] = tvs[i].getKey();
		}

		Arrays.sort(Keys);
		return Keys;
	}

	private boolean compare(KeyValues[] tvs, String Key, String[] values) {
		for( KeyValues tv : tvs ) {
			if (tv.getKey().equals(Key)) {
				String[] myValues = tv.getValue();
				Arrays.sort(values);
				Arrays.sort(myValues);
				return Arrays.equals(values, myValues);
			}
		}
		return true;
	}

	private boolean compare(KeyValues[] tvs1, KeyValues[] tvs2) {
		

		// Make sure the Keys match
		if (!Arrays.equals(getSortedKeys(tvs1), getSortedKeys(tvs2))) {
			return false;
		}

		for( KeyValues tv : tvs1 ) {
			if (!compare(tvs2, tv.getKey(), tv.getValue())) {
				return false;
			}
		}
		return true;
	}


	public static void main(String[] args) {
		junit.textui.TestRunner.run(NATestCase.class);
	}
}