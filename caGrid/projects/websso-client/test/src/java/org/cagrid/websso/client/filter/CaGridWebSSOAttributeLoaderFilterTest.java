package org.cagrid.websso.client.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cagrid.websso.common.WebSSOClientHelper;

import junit.framework.TestCase;

public class CaGridWebSSOAttributeLoaderFilterTest extends TestCase {
	
	private Map<String, String> expectedUserAttributes=new HashMap<String, String>();
	@Override
	protected void setUp() throws Exception {
		expectedUserAttributes.put("CAGRID_SSO_FIRST_NAME", "user");
		expectedUserAttributes.put("CAGRID_SSO_GRID_IDENTITY", "/O=caBIG/OU=caGrid/OU=Training/OU=Dorian/CN=userid");
		expectedUserAttributes.put("CAGRID_SSO_LAST_NAME", "lastname");
		expectedUserAttributes.put("CAGRID_SSO_DELEGATION_SERVICE_EPR", testDelegationEPR);
		expectedUserAttributes.put("CAGRID_SSO_EMAIL_ID", "email@nih.gov");
	}

	public void testValidateSuccessUserAttributes() throws Exception {
		Map<String, String> userAttributes =WebSSOClientHelper.getUserAttributes(attributeString);
		Iterator<String> iterator = userAttributes.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			assertEquals(expectedUserAttributes.get(key), userAttributes.get(key));
		}
	}

	public void testValidateFailureUserAttributes() {
		try {
			WebSSOClientHelper.getUserAttributes("failure");
			fail("must throw servlet exception");
		} catch (RuntimeException e) {
			assertEquals("Invalid UserAttributes from WebSSO-Server ", e.getMessage());
		}
	}

	private String testDelegationEPR = "<ns1:DelegatedCredentialReference xmlns:ns1=\"http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential/types\"><ns2:EndpointReference xmlns:ns2=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\"><ns2:Address>https://cds.training.cagrid.org:8443/wsrf/services/cagrid/DelegatedCredential</ns2:Address><ns2:ReferenceProperties>	<ns2:DelegatedCredentialKey xmlns:ns2=\"http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential\"><ns3:delegationId xmlns:ns3=\"http://gaards.cagrid.org/cds\">270</ns3:delegationId></ns2:DelegatedCredentialKey></ns2:ReferenceProperties><ns2:ReferenceParameters/></ns2:EndpointReference></ns1:DelegatedCredentialReference>";

	private String attributeString = "CAGRID_SSO_GRID_IDENTITY^/O=caBIG/OU=caGrid/OU=Training/OU=Dorian/CN=userid$CAGRID_SSO_FIRST_NAME^user$CAGRID_SSO_LAST_NAME^lastname$CAGRID_SSO_DELEGATION_SERVICE_EPR^<ns1:DelegatedCredentialReference xmlns:ns1=\"http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential/types\">"
			+ "<ns2:EndpointReference xmlns:ns2=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\">"
			+ "<ns2:Address>https://cds.training.cagrid.org:8443/wsrf/services/cagrid/DelegatedCredential</ns2:Address>"
			+ "<ns2:ReferenceProperties>	<ns2:DelegatedCredentialKey xmlns:ns2=\"http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential\">"
			+ "<ns3:delegationId xmlns:ns3=\"http://gaards.cagrid.org/cds\">270</ns3:delegationId>"
			+ "</ns2:DelegatedCredentialKey></ns2:ReferenceProperties><ns2:ReferenceParameters/></ns2:EndpointReference></ns1:DelegatedCredentialReference>"
			+ "$CAGRID_SSO_EMAIL_ID^email@nih.gov";
}
