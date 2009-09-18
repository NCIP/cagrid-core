package gov.nih.nci.cagrid.identifiers.test;

import java.rmi.RemoteException;
import java.util.Arrays;

import gov.nih.nci.cagrid.identifiers.TypeValues;
import gov.nih.nci.cagrid.identifiers.TypeValuesMap;
import gov.nih.nci.cagrid.identifiers.Values;
import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;

import org.apache.axis.client.Stub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class NATestCase extends TestCase {
	private static Log log = LogFactory.getLog(NATestCase.class);

	private static final TypeValues[] typeValues;
	private static final String gridSvcUrl = "http://localhost:8081/wsrf/services/cagrid/IdentifiersNAService";

	static {
		typeValues = new TypeValues[2];
		typeValues[0] = new TypeValues();
		typeValues[0].setType("URL");
		Values values = new Values();
		values.setValue(new String[] { "http://www.google.com" });
		typeValues[0].setValues(values);

		typeValues[1] = new TypeValues();
		typeValues[1].setType("EPR");
		values = new Values();
		values.setValue(new String[] { "end point reference 1", "end point reference 2" });
		typeValues[1].setValues(values);
	}

	public void testNamingAuthorityGridService() throws Exception {
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );

		TypeValuesMap tvm1 = new TypeValuesMap();
		tvm1.setTypeValues(typeValues);

		String identifier = client.createIdentifier(tvm1);
		log.info("Identifier: " + identifier);

		TypeValuesMap tvm2 = client.getTypeValues(identifier);
		if (!compare(tvm1, tvm2)) {
			fail("TypeValuesMap arrays are different");
		}
	}

	private String[] getSortedTypes(TypeValues[] tvs) {
		String[] types = new String[ tvs.length ];
		for(int i=0; i < tvs.length; i++) {
			types[i] = tvs[i].getType();
		}

		Arrays.sort(types);
		return types;
	}

	private boolean compare(TypeValues[] tvs, String type, String[] values) {
		for( TypeValues tv : tvs ) {
			if (tv.getType().equals(type)) {
				String[] myValues = tv.getValues().getValue();
				Arrays.sort(values);
				Arrays.sort(myValues);
				return Arrays.equals(values, myValues);
			}
		}
		return true;
	}

	private boolean compare(TypeValuesMap tvm1, TypeValuesMap tvm2) {
		TypeValues[] tvs1 = tvm1.getTypeValues();
		TypeValues[] tvs2 = tvm2.getTypeValues();

		// Make sure the types match
		if (!Arrays.equals(getSortedTypes(tvs1), getSortedTypes(tvs2))) {
			return false;
		}

		for( TypeValues tv : tvs1 ) {
			if (!compare(tvs2, tv.getType(), tv.getValues().getValue())) {
				return false;
			}
		}
		return true;
	}


	public static void main(String[] args) {
		junit.textui.TestRunner.run(NATestCase.class);
	}
}

