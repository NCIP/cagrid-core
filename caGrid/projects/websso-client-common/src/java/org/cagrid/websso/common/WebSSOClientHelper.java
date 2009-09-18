package org.cagrid.websso.common;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.springframework.core.io.ClassPathResource;

public class WebSSOClientHelper {

	public static Map<String, String> getUserAttributes(String attributeString){
		Map<String, String> userAttributes = new HashMap<String, String>();
		StringTokenizer stringTokenizer = new StringTokenizer(attributeString,WebSSOConstants.ATTRIBUTE_DELIMITER);
		while (stringTokenizer.hasMoreTokens()) {
			String attributeKeyValuePair = stringTokenizer.nextToken();
			final int index = attributeKeyValuePair.indexOf(WebSSOConstants.KEY_VALUE_PAIR_DELIMITER);
			if (index == -1)
				throw new RuntimeException("Invalid UserAttributes from WebSSO-Server ");
			final String key = attributeKeyValuePair.substring(0, index);
			final String value = attributeKeyValuePair.substring(index + 1,attributeKeyValuePair.length());
			userAttributes.put(key, value);
		}
		return userAttributes;
	}
	
	public static GlobusCredential getUserCredential(String delegationEPR,
			String certificateFilePath,
			String keyFilePath) throws WebSSOClientException{
		DelegatedCredentialReference delegatedCredentialReference = getDelegatedCredentialReference(delegationEPR);
		GlobusCredential hostCredential = getHostCredential(certificateFilePath, keyFilePath);
		DelegatedCredentialUserClient delegatedCredentialUserClient = getDelegatedCredentialUserClient(
				delegatedCredentialReference, hostCredential);
		GlobusCredential userCredential = getUserCredential(delegatedCredentialUserClient);
		return userCredential;
	}

	private static GlobusCredential getUserCredential(
			DelegatedCredentialUserClient delegatedCredentialUserClient)
			throws WebSSOClientException {
		GlobusCredential userCredential;
		try {
			userCredential = delegatedCredentialUserClient.getDelegatedCredential();
		} catch (CDSInternalFault e) {
			FaultUtil.printFaultToString(e);
			throw new WebSSOClientException("Error retrieving the Delegated Credentials", e);
		} catch (DelegationFault e) {
			FaultUtil.printFaultToString(e);
			throw new WebSSOClientException("Error retrieving the Delegated Credentials", e);
		} catch (PermissionDeniedFault e) {
			FaultUtil.printFaultToString(e);
			throw new WebSSOClientException("Permission denied to retrieve Delegated Credentials"+FaultUtil.printFaultToString(e));
		}catch (RemoteException e) {
			throw new WebSSOClientException("Error retrieving the Delegated Credentials", e);
		}
		return userCredential;
	}

	private static DelegatedCredentialUserClient getDelegatedCredentialUserClient(
			DelegatedCredentialReference delegatedCredentialReference,
			GlobusCredential hostCredential) throws WebSSOClientException {
		DelegatedCredentialUserClient delegatedCredentialUserClient = null;
		try {
			delegatedCredentialUserClient = new DelegatedCredentialUserClient(delegatedCredentialReference, hostCredential);
		} catch (Exception e) {
			throw new WebSSOClientException("Unable to Initialize the Delegation Lookup Client", e);
		}
		return delegatedCredentialUserClient;
	}

	private static DelegatedCredentialReference getDelegatedCredentialReference(
			String delegationEPR) throws WebSSOClientException {
		DelegatedCredentialReference delegatedCredentialReference=null;
		ClassPathResource pathResource=new ClassPathResource("client-config.wsdd");
		InputStream inputStream;
		try {
			inputStream = pathResource.getInputStream();
		} catch (IOException e) {
			throw new WebSSOClientException("file client-config.wsdd not found in classpath ", e);
		}
		try {
			delegatedCredentialReference = (DelegatedCredentialReference) Utils
					.deserializeObject(new StringReader(delegationEPR),
							DelegatedCredentialReference.class,inputStream
							);
		} catch (Exception e) {
			throw new WebSSOClientException("Unable to deserialize the Delegation Reference", e);
		}
		return delegatedCredentialReference;
	}

	private static GlobusCredential getHostCredential(
			String certificateFilePath,
			String keyFilePath) throws WebSSOClientException {
		GlobusCredential hostCredential=null;
		try {
			hostCredential = new GlobusCredential(certificateFilePath,keyFilePath);
		} catch (GlobusCredentialException e) {
			throw new WebSSOClientException("Invalid Certificate and Key File. Error creating Globus Credential",e);
		}
		return hostCredential;
	}
	
	public static String getLogoutURL(Properties properties, String delegationEPR) {
		String logoutURL = properties.getProperty("cas.server.url")+ "/logout";
		String logoutLandingURL=properties.getProperty("logout.landing.url");
		logoutURL = logoutURL + "?service=" + logoutLandingURL;
		logoutURL = logoutURL + "&" + WebSSOConstants.CAGRID_SSO_DELEGATION_SERVICE_EPR+ "=" + delegationEPR;
		return logoutURL;
	}
}
