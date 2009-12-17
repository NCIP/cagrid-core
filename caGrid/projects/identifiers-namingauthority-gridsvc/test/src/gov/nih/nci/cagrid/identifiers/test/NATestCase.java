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

		IdentifierValues values1 = new IdentifierValues(keyValues);
		IdentifierValues values2 = null;
		
		try {
			org.apache.axis.types.URI identifier = client.createIdentifier(values1);
			System.out.println("Identifier: " + identifier.toString());

			values2 = client.resolveIdentifier(identifier);	
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
		Util.assertEquals(values1, values2);
	}

	public void testInvalidIdentifier() throws Exception {
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );
		
		try {
			org.apache.axis.types.URI identifier = new URI("file://324324325");

			client.resolveIdentifier(identifier);

		}
		catch(InvalidIdentifierFault e) {
			//expected
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(NATestCase.class);
	}
}
