package gov.nih.nci.cagrid.identifiers.test;

import gov.nih.nci.cagrid.identifiers.common.IdentifiersNAUtil;

import java.net.URISyntaxException;

import junit.framework.TestCase;
import namingauthority.IdentifierData;
import namingauthority.KeyData;
import namingauthority.KeyNameData;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NATestCase extends TestCase {
	private static Log log = LogFactory.getLog(NATestCase.class);

	private static final KeyNameData[] knd1, knd2;

	static {
		//
		// KeyNameData1
		//
		KeyData kd = new KeyData();
		
		kd.setValue(new String[] { "http://www.google.com" });
		knd1 = new KeyNameData[2];
		knd1[0] = new KeyNameData();
		knd1[0].setKeyName("URL");
		knd1[0].setKeyData(kd);

		kd.setValue(new String[] { "end point reference 1", "end point reference 2" });
		knd1[1] = new KeyNameData();
		knd1[1].setKeyName("EPR");
		knd1[1].setKeyData(kd);
		
		//
		// KeyNameData2 (same as KeyNameData1)
		//
		knd2 = new KeyNameData[2];
		
		kd.setValue(new String[] { "http://www.google.com" });
		knd2[0] = new KeyNameData();
		knd2[0].setKeyName("URL");
		knd2[0].setKeyData(kd);

		kd.setValue(new String[] { "end point reference 1", "end point reference 2" });
		knd2[1] = new KeyNameData();
		knd2[1].setKeyName("EPR");
		knd2[1].setKeyData(kd);
	}
	
	public void testUtil() {
		IdentifierData gridValues1 = new IdentifierData(knd1);
		
		// convert to domain values
		org.cagrid.identifiers.namingauthority.domain.IdentifierData domainValues1;
		try {
			domainValues1 = IdentifiersNAUtil.map(gridValues1);
			
			// convert back to grid values
			IdentifierData gridValues2;
			gridValues2 = IdentifiersNAUtil.map(domainValues1);
			
			// must be the same
			IdentifiersNAUtil.assertEquals(gridValues1, gridValues2);
			IdentifiersNAUtil.assertEquals(gridValues1, new IdentifierData(knd2));
			IdentifiersNAUtil.assertEquals(knd1, knd2);
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (MalformedURIException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

//	MOVED TO System Test
//	public void testNamingAuthorityGridService() throws Exception {
//		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );
//
//		IdentifierKeyData values1 = new IdentifierKeyData(KeyData);
//		IdentifierKeyData values2 = null;
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
