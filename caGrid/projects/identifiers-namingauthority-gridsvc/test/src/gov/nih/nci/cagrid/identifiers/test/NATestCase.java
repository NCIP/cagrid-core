package gov.nih.nci.cagrid.identifiers.test;

import java.rmi.RemoteException;
import java.util.Arrays;

import gov.nih.nci.cagrid.identifiers.KeyValues;
import gov.nih.nci.cagrid.identifiers.KeyValuesMap;
import gov.nih.nci.cagrid.identifiers.Values;
import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;

import org.apache.axis.client.Stub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class NATestCase extends TestCase {
	private static Log log = LogFactory.getLog(NATestCase.class);

	private static final KeyValues[] keyValues;
	private static final String gridSvcUrl = "http://140.254.126.81:8080/wsrf/services/cagrid/IdentifiersNAService";

	static {
		keyValues = new KeyValues[2];
		keyValues[0] = new KeyValues();
		keyValues[0].setKey("URL");
		Values values = new Values();
		values.setValue(new String[] { "http://www.google.com" });
		keyValues[0].setValues(values);

		keyValues[1] = new KeyValues();
		keyValues[1].setKey("EPR");
		values = new Values();
		values.setValue(new String[] { "end point reference 1", "end point reference 2" });
		keyValues[1].setValues(values);
	}

	public void testNamingAuthorityGridService() throws Exception {
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );

		KeyValuesMap tvm1 = new KeyValuesMap();
		tvm1.setKeyValues(keyValues);

		org.apache.axis.types.URI identifier = client.createIdentifier(tvm1);
		System.out.println("Identifier: " + identifier);

		KeyValuesMap tvm2 = client.resolveIdentifier(identifier);
		if (!compare(tvm1, tvm2)) {
			fail("KeyValuesMap arrays are different");
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
				String[] myValues = tv.getValues().getValue();
				Arrays.sort(values);
				Arrays.sort(myValues);
				return Arrays.equals(values, myValues);
			}
		}
		return true;
	}

	private boolean compare(KeyValuesMap tvm1, KeyValuesMap tvm2) {
		KeyValues[] tvs1 = tvm1.getKeyValues();
		KeyValues[] tvs2 = tvm2.getKeyValues();

		// Make sure the Keys match
		if (!Arrays.equals(getSortedKeys(tvs1), getSortedKeys(tvs2))) {
			return false;
		}

		for( KeyValues tv : tvs1 ) {
			if (!compare(tvs2, tv.getKey(), tv.getValues().getValue())) {
				return false;
			}
		}
		return true;
	}


	public static void main(String[] args) {
		junit.textui.TestRunner.run(NATestCase.class);
	}
}

