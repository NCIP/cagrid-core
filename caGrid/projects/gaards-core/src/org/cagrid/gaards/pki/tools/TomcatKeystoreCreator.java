package org.cagrid.gaards.pki.tools;

import gov.nih.nci.cagrid.common.IOUtils;
import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;

public class TomcatKeystoreCreator {

	public static void main(String args[]) {
		try {
			String keystoreLocation = IOUtils
					.readLine("Enter a location and name for your keystore");
			String password = IOUtils
					.readLine("Enter a password for your keystore");

			KeyStore keyStore = KeyStore.getInstance("jks");
			keyStore.load(null);

			String certLocation = IOUtils
					.readLine("Enter the location of the certificate (PEM format)");

			java.security.cert.Certificate[] chain = { CertUtil
					.loadCertificate(new File(certLocation)) };

			String keyStr = IOUtils
					.readLine("Enter the location of the private key (PEM format)");
			String keyPassword = Utils.clean(IOUtils
					.readLine("Enter the current password of the private key"));

			File keyFile = new File(keyStr);

			PrivateKey privateKey = KeyUtil
					.loadPrivateKey(keyFile, keyPassword);

			keyStore.setEntry("tomcat", new KeyStore.PrivateKeyEntry(
					privateKey, chain), new KeyStore.PasswordProtection(
					password.toCharArray()));

			FileOutputStream fos = new FileOutputStream(keystoreLocation);
			keyStore.store(fos, password.toCharArray());
			fos.close();
		}

		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}