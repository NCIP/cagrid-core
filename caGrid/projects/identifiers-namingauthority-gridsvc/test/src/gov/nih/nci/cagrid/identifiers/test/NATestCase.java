package gov.nih.nci.cagrid.identifiers.test;

import gov.nih.nci.cagrid.identifiers.common.IdentifiersNAUtil;
import namingauthority.IdentifierValues;
import namingauthority.KeyValues;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class NATestCase extends TestCase {
	private static Log log = LogFactory.getLog(NATestCase.class);

	private static final KeyValues[] keyValues1, keyValues2;

	static {
		//
		// keyValues1
		//
		keyValues1 = new KeyValues[2];
		keyValues1[0] = new KeyValues();
		keyValues1[0].setKey("URL");
		keyValues1[0].setValue(new String[] { "http://www.google.com" });

		keyValues1[1] = new KeyValues();
		keyValues1[1].setKey("EPR");
		keyValues1[1].setValue(new String[] { "end point reference 1", "end point reference 2" });
		
		//
		// keyValues2 (same as keyvalues1)
		//
		keyValues2 = new KeyValues[2];
		keyValues2[0] = new KeyValues();
		keyValues2[0].setKey("URL");
		keyValues2[0].setValue(new String[] { "http://www.google.com" });

		keyValues2[1] = new KeyValues();
		keyValues2[1].setKey("EPR");
		keyValues2[1].setValue(new String[] { "end point reference 1", "end point reference 2" });
	}
	
	public void testUtil() {
		IdentifierValues gridValues1 = new IdentifierValues(keyValues1);
		
		// convert to domain values
		org.cagrid.identifiers.namingauthority.domain.IdentifierValues
			domainValues1 = IdentifiersNAUtil.map(gridValues1);
		
		// convert back to grid values
		IdentifierValues gridValues2 = IdentifiersNAUtil.map(domainValues1);
		
		// must be the same
		IdentifiersNAUtil.assertEquals(gridValues1, gridValues2);
		IdentifiersNAUtil.assertEquals(gridValues1, new IdentifierValues(keyValues2));
		IdentifiersNAUtil.assertEquals(keyValues1, keyValues2);
	}

//	MOVED TO System Test
//	public void testNamingAuthorityGridService() throws Exception {
//		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );
//
//		IdentifierValues values1 = new IdentifierValues(keyValues);
//		IdentifierValues values2 = null;
//		
//		try {
//			org.apache.axis.types.URI identifier = client.createIdentifier(values1);
//			System.out.println("Identifier: " + identifier.toString());
//
//			values2 = client.resolveIdentifier(identifier);	
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			fail(e.toString());
//		}
//		
//		Util.assertEquals(values1, values2);
//	}

//  MOVED TO System Test	
//	public void testInvalidIdentifier() throws Exception {
//		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );
//		
//		try {
//			org.apache.axis.types.URI identifier = new URI("file://324324325");
//
//			client.resolveIdentifier(identifier);
//
//		}
//		catch(InvalidIdentifierFault e) {
//			//expected
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			fail(e.toString());
//		}
//	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(NATestCase.class);
	}
}
