package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.security.cert.X509Certificate;

import org.cagrid.gaards.authentication.service.SAMLConstants;
import org.cagrid.gaards.saml.encoding.SAMLUtils;

public class SuccessfullAuthentication extends BaseAuthenticationOutcome
		implements AuthenticationOutcome {

	private String expectedUserId;
	private String expectedFirstName;
	private String expectedLastName;
	private String expectedEmail;
	private SigningCertificateProxy proxy;
	private String userIdAttributeNamespace = SAMLConstants.UID_ATTRIBUTE_NAMESPACE;
	private String userIdAttributeName = SAMLConstants.UID_ATTRIBUTE;
	private String firstNameAttributeNamespace = SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE;
	private String firstNameAttributeName = SAMLConstants.FIRST_NAME_ATTRIBUTE;
	private String lastNameAttributeNamespace = SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE;
	private String lastNameAttributeName = SAMLConstants.LAST_NAME_ATTRIBUTE;
	private String emailAttributeNamespace = SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE;
	private String emailAttributeName = SAMLConstants.EMAIL_ATTRIBUTE;
	
	public SuccessfullAuthentication(String expectedUserId,
			String expectedFirstName, String expectedLastName,
			String expectedEmail, SigningCertificateProxy proxy) {
		super();
		this.expectedUserId = expectedUserId;
		this.expectedFirstName = expectedFirstName;
		this.expectedLastName = expectedLastName;
		this.expectedEmail = expectedEmail;
		this.proxy = proxy;
		}

	public SuccessfullAuthentication(String expectedUserId,
			String expectedFirstName, String expectedLastName,
			String expectedEmail, X509Certificate expectedSigningCertificate) {
		this(expectedUserId,expectedFirstName,expectedLastName,expectedEmail,new CertificateProxy(expectedSigningCertificate));
	}

	public String getExpectedUserId() {
		return expectedUserId;
	}

	public String getExpectedFirstName() {
		return expectedFirstName;
	}

	public String getExpectedLastName() {
		return expectedLastName;
	}

	public String getExpectedEmail() {
		return expectedEmail;
	}

	public X509Certificate getExpectedSigningCertificate() {
		return proxy.getSigningCertificate();
	}

	public String getUserIdAttributeNamespace() {
		return userIdAttributeNamespace;
	}

	public void setUserIdAttributeNamespace(String userIdAttributeNamespace) {
		this.userIdAttributeNamespace = userIdAttributeNamespace;
	}

	public String getUserIdAttributeName() {
		return userIdAttributeName;
	}

	public void setUserIdAttributeName(String userIdAttributeName) {
		this.userIdAttributeName = userIdAttributeName;
	}

	public String getFirstNameAttributeNamespace() {
		return firstNameAttributeNamespace;
	}

	public void setFirstNameAttributeNamespace(
			String firstNameAttributeNamespace) {
		this.firstNameAttributeNamespace = firstNameAttributeNamespace;
	}

	public String getFirstNameAttributeName() {
		return firstNameAttributeName;
	}

	public void setFirstNameAttributeName(String firstNameAttributeName) {
		this.firstNameAttributeName = firstNameAttributeName;
	}

	public String getLastNameAttributeNamespace() {
		return lastNameAttributeNamespace;
	}

	public void setLastNameAttributeNamespace(String lastNameAttributeNamespace) {
		this.lastNameAttributeNamespace = lastNameAttributeNamespace;
	}

	public String getLastNameAttributeName() {
		return lastNameAttributeName;
	}

	public void setLastNameAttributeName(String lastNameAttributeName) {
		this.lastNameAttributeName = lastNameAttributeName;
	}

	public String getEmailAttributeNamespace() {
		return emailAttributeNamespace;
	}

	public void setEmailAttributeNamespace(String emailAttributeNamespace) {
		this.emailAttributeNamespace = emailAttributeNamespace;
	}

	public String getEmailAttributeName() {
		return emailAttributeName;
	}

	public void setEmailAttributeName(String emailAttributeName) {
		this.emailAttributeName = emailAttributeName;
	}

	public void check(SAMLAssertion saml, Exception error) throws Exception {
		if (error != null) {
			getLog().error(error);
			throw new Exception(
					"An error was encountered authenticating when one was not expected.",
					error);
		} else {
			saml.verify(getExpectedSigningCertificate());
			String uid = SAMLUtils.getAttributeValue(saml,
					getUserIdAttributeNamespace(), getUserIdAttributeName());
			String firstName = SAMLUtils.getAttributeValue(saml,
					getFirstNameAttributeNamespace(),
					getFirstNameAttributeName());
			String lastName = SAMLUtils
					.getAttributeValue(saml, getLastNameAttributeNamespace(),
							getLastNameAttributeName());
			String email = SAMLUtils.getAttributeValue(saml,
					getEmailAttributeNamespace(), getEmailAttributeName());
			if (!getExpectedUserId().equals(uid)) {
				throw new Exception(
						"The SAML Assertion was not as expected, the expected user id was "
								+ getExpectedUserId()
								+ " however the user id received was " + uid
								+ ".");
			}

			if (!getExpectedFirstName().equals(firstName)) {
				throw new Exception(
						"The SAML Assertion was not as expected, the expected first name was "
								+ getExpectedFirstName()
								+ " however the first name received was "
								+ firstName + ".");
			}

			if (!getExpectedLastName().equals(lastName)) {
				throw new Exception(
						"The SAML Assertion was not as expected, the expected last name was "
								+ getExpectedLastName()
								+ " however the last name received was "
								+ lastName + ".");
			}

			if (!getExpectedEmail().equals(email)) {
				throw new Exception(
						"The SAML Assertion was not as expected, the expected email was "
								+ getExpectedEmail()
								+ " however the email received was " + email
								+ ".");
			}

		}

	}

}
