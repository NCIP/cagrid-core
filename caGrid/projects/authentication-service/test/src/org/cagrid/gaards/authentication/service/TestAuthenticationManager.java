package org.cagrid.gaards.authentication.service;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.io.File;
import java.util.Set;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.OneTimePassword;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.authentication.test.AuthenticationProperties;
import org.cagrid.gaards.authentication.test.Constants;
import org.cagrid.gaards.saml.encoding.SAMLUtils;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestAuthenticationManager extends TestCase {

	public static final File SUBJECT_PROVIDER_1_CONF = new File(
			"test/resources/authentication-config-subject-provider1.xml");
	public static final File SUBJECT_PROVIDER_2_CONF = new File(
			"test/resources/authentication-config-subject-provider2.xml");

	public void testBasicAuthentication() {
		AuthenticationProperties properties = null;
		try {
			properties = new AuthenticationProperties();
			AuthenticationManager manager = new AuthenticationManager(
					properties.getPropertiesFile(), SUBJECT_PROVIDER_1_CONF);

			// Check supported profiles
			Set<QName> profiles = manager.getSupportedAuthenticationProfiles();
			assertNotNull(profiles);
			assertEquals(1, profiles.size());
			assertTrue(profiles
					.contains(AuthenticationProfile.BASIC_AUTHENTICATION));

			// Test acceptable authentication
			BasicAuthentication auth = new BasicAuthentication();
			auth.setUserId("jdoe");
			auth.setPassword("password");
			SAMLAssertion saml = manager.authenticate(auth);
			saml.verify(properties.getSigningCertificate());
			assertEquals(auth.getUserId(), SAMLUtils.getAttributeValue(saml,
					SAMLConstants.UID_ATTRIBUTE_NAMESPACE,
					SAMLConstants.UID_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_FIRST_NAME, SAMLUtils
					.getAttributeValue(saml,
							SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE,
							SAMLConstants.FIRST_NAME_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_LAST_NAME, SAMLUtils
					.getAttributeValue(saml,
							SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE,
							SAMLConstants.LAST_NAME_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_EMAIL, SAMLUtils.getAttributeValue(
					saml, SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE,
					SAMLConstants.EMAIL_ATTRIBUTE));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (properties != null) {
				properties.cleanup();
			}
		}
	}

	public void testDeprecatedBasicAuthentication() {
		AuthenticationProperties properties = null;
		try {
			properties = new AuthenticationProperties();
			AuthenticationManager manager = new AuthenticationManager(
					properties.getPropertiesFile(), SUBJECT_PROVIDER_1_CONF);

			// Check supported profiles
			Set<QName> profiles = manager.getSupportedAuthenticationProfiles();
			assertNotNull(profiles);
			assertEquals(1, profiles.size());
			assertTrue(profiles
					.contains(AuthenticationProfile.BASIC_AUTHENTICATION));

			// Test acceptable authentication
			BasicAuthenticationCredential auth = new BasicAuthenticationCredential();
			auth.setUserId("jdoe");
			auth.setPassword("password");
			gov.nih.nci.cagrid.authentication.bean.Credential credential = new gov.nih.nci.cagrid.authentication.bean.Credential();
			credential.setBasicAuthenticationCredential(auth);
			SAMLAssertion saml = SAMLUtils.stringToSAMLAssertion(manager
					.authenticate(credential).getXml());
			saml.verify(properties.getSigningCertificate());
			assertEquals(auth.getUserId(), SAMLUtils.getAttributeValue(saml,
					SAMLConstants.UID_ATTRIBUTE_NAMESPACE,
					SAMLConstants.UID_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_FIRST_NAME, SAMLUtils
					.getAttributeValue(saml,
							SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE,
							SAMLConstants.FIRST_NAME_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_LAST_NAME, SAMLUtils
					.getAttributeValue(saml,
							SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE,
							SAMLConstants.LAST_NAME_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_EMAIL, SAMLUtils.getAttributeValue(
					saml, SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE,
					SAMLConstants.EMAIL_ATTRIBUTE));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (properties != null) {
				properties.cleanup();
			}
		}
	}

	public void testBasicAuthenticationBadPassword() {
		AuthenticationProperties properties = null;
		try {
			properties = new AuthenticationProperties();
			AuthenticationManager manager = new AuthenticationManager(
					properties.getPropertiesFile(), SUBJECT_PROVIDER_1_CONF);

			// Check supported profiles
			Set<QName> profiles = manager.getSupportedAuthenticationProfiles();
			assertNotNull(profiles);
			assertEquals(1, profiles.size());
			assertTrue(profiles
					.contains(AuthenticationProfile.BASIC_AUTHENTICATION));

			// Test acceptable authentication
			BasicAuthentication auth = new BasicAuthentication();
			auth.setUserId("jdoe");
			auth.setPassword("password2");
			try {
				manager.authenticate(auth);
				fail("Should not be able to authenticate.");
			} catch (InvalidCredentialFault e) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (properties != null) {
				properties.cleanup();
			}
		}
	}

	public void testDeprecatedBasicAuthenticationBadPassword() {
		AuthenticationProperties properties = null;
		try {
			properties = new AuthenticationProperties();
			AuthenticationManager manager = new AuthenticationManager(
					properties.getPropertiesFile(), SUBJECT_PROVIDER_1_CONF);

			// Check supported profiles
			Set<QName> profiles = manager.getSupportedAuthenticationProfiles();
			assertNotNull(profiles);
			assertEquals(1, profiles.size());
			assertTrue(profiles
					.contains(AuthenticationProfile.BASIC_AUTHENTICATION));

			// Test acceptable authentication
			BasicAuthenticationCredential auth = new BasicAuthenticationCredential();
			auth.setUserId("jdoe");
			auth.setPassword("password2");
			gov.nih.nci.cagrid.authentication.bean.Credential credential = new gov.nih.nci.cagrid.authentication.bean.Credential();
			credential.setBasicAuthenticationCredential(auth);
			try {
				manager.authenticate(credential);
				fail("Should not be able to authenticate.");
			} catch (gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault e) {

			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (properties != null) {
				properties.cleanup();
			}
		}
	}
	
	public void testBasicAuthenticationUnsupportedCredential() {
		AuthenticationProperties properties = null;
		try {
			properties = new AuthenticationProperties();
			AuthenticationManager manager = new AuthenticationManager(
					properties.getPropertiesFile(), SUBJECT_PROVIDER_1_CONF);

			// Check supported profiles
			Set<QName> profiles = manager.getSupportedAuthenticationProfiles();
			assertNotNull(profiles);
			assertEquals(1, profiles.size());
			assertTrue(profiles
					.contains(AuthenticationProfile.BASIC_AUTHENTICATION));

			// Test acceptable authentication
			OneTimePassword auth = new OneTimePassword();
			auth.setUserId("jdoe");
			auth.setOneTimePassword("onetimepassword");
			try {
				manager.authenticate(auth);
				fail("Should not be able to authenticate.");
			} catch (CredentialNotSupportedFault e) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (properties != null) {
				properties.cleanup();
			}
		}
	}

	public void testDeprecatedBasicAuthenticationUnsupportedCredential() {
		AuthenticationProperties properties = null;
		try {
			properties = new AuthenticationProperties();
			AuthenticationManager manager = new AuthenticationManager(
					properties.getPropertiesFile(), SUBJECT_PROVIDER_1_CONF);

			// Check supported profiles
			Set<QName> profiles = manager.getSupportedAuthenticationProfiles();
			assertNotNull(profiles);
			assertEquals(1, profiles.size());
			assertTrue(profiles
					.contains(AuthenticationProfile.BASIC_AUTHENTICATION));

			// Test acceptable authentication
			BasicAuthenticationCredential auth = new BasicAuthenticationCredential();
			auth.setUserId("jdoe");
			auth.setPassword("password");
			gov.nih.nci.cagrid.authentication.bean.Credential credential = new gov.nih.nci.cagrid.authentication.bean.Credential();
			credential.setBasicAuthenticationCredential(auth);
			credential.setCredentialExtension(new String());
			try {
				manager.authenticate(credential);
				fail("Should not be able to authenticate.");
			} catch (gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault e) {

			}
			credential.setBasicAuthenticationCredential(null);
			credential.setCredentialExtension(new String());
			try {
				manager.authenticate(credential);
				fail("Should not be able to authenticate.");
			} catch (gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault e) {

			}
			credential.setCredentialExtension(null);
			try {
				manager.authenticate(credential);
				fail("Should not be able to authenticate.");
			} catch (gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault e) {

			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (properties != null) {
				properties.cleanup();
			}
		}
	}
	
	public void testBasicAuthenticationWithOneTimePassword() {
		AuthenticationProperties properties = null;
		try {
			properties = new AuthenticationProperties();
			AuthenticationManager manager = new AuthenticationManager(
					properties.getPropertiesFile(), SUBJECT_PROVIDER_2_CONF);

			// Check supported profiles
			Set<QName> profiles = manager.getSupportedAuthenticationProfiles();
			assertNotNull(profiles);
			assertEquals(1, profiles.size());
			assertTrue(profiles
					.contains(AuthenticationProfile.ONE_TIME_PASSWORD));

			// Test acceptable authentication
			OneTimePassword auth = new OneTimePassword();
			auth.setUserId("jdoe");
			auth.setOneTimePassword("onetimepassword");
			SAMLAssertion saml = manager.authenticate(auth);
			saml.verify(properties.getSigningCertificate());
			assertEquals(auth.getUserId(), SAMLUtils.getAttributeValue(saml,
					SAMLConstants.UID_ATTRIBUTE_NAMESPACE,
					SAMLConstants.UID_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_FIRST_NAME, SAMLUtils
					.getAttributeValue(saml,
							SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE,
							SAMLConstants.FIRST_NAME_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_LAST_NAME, SAMLUtils
					.getAttributeValue(saml,
							SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE,
							SAMLConstants.LAST_NAME_ATTRIBUTE));
			assertEquals(Constants.DEFAULT_EMAIL, SAMLUtils.getAttributeValue(
					saml, SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE,
					SAMLConstants.EMAIL_ATTRIBUTE));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (properties != null) {
				properties.cleanup();
			}
		}
	}
}
