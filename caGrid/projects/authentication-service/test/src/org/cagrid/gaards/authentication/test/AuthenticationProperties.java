package org.cagrid.gaards.authentication.test;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.cagrid.gaards.pki.CA;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;

public class AuthenticationProperties {
	private String csmContext;
	private X509Certificate signingCertificate;
	private PrivateKey signingKey;
	private String signingKeyPassword;
	private long id;
	private CA ca;
	private File properties;
	private File key;
	private File certificate;

	public AuthenticationProperties() throws Exception {
		this(null);
	}

	public AuthenticationProperties(String csmContext) throws Exception {
		java.util.Date d = new java.util.Date();
		id = d.getTime();
		this.csmContext = csmContext;
		this.ca = new CA();
		org.cagrid.gaards.pki.Credential c = this.ca
				.createIdentityCertificate("Mr Assertion Signer");
		this.signingCertificate = c.getCertificate();
		this.signingKey = c.getPrivateKey();
		this.signingKeyPassword = "password";
		certificate = new File(id + ".cert");
		CertUtil.writeCertificate(signingCertificate, certificate);
		key = new File(id + ".key");
		KeyUtil.writePrivateKey(signingKey, key, signingKeyPassword);
		properties = new File(id + ".properties");
		Properties props = new Properties();
		props.setProperty("gaards.authentication.saml.cert", certificate
				.getAbsolutePath());
		props.setProperty("gaards.authentication.saml.key", key
				.getAbsolutePath());
		props.setProperty("gaards.authentication.saml.key.password",
				signingKeyPassword);
		if (Utils.clean(this.csmContext) != null) {
			props.setProperty("gaards.authentication.csm.app.context",
					this.csmContext);
		}
		props.store(new FileOutputStream(properties), null);
	}

	public void cleanup() {
		try {
			properties.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			certificate.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			key.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCsmContext() {
		return csmContext;
	}

	public X509Certificate getSigningCertificate() {
		return signingCertificate;
	}

	public PrivateKey getSigningKey() {
		return signingKey;
	}

	public String getSigningKeyPassword() {
		return signingKeyPassword;
	}
	
	public File getPropertiesFile(){
		return this.properties;
	}

}
