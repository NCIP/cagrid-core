package org.cagrid.gaards.pki.tools;

import gov.nih.nci.cagrid.common.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;

import org.cagrid.gaards.pki.CertUtil;

public class TruststoreCreator {

	public static void main(String args[]) {
		try {
			String keystoreLocation = IOUtils
					.readLine("Enter a location and name for your truststore");
			KeyStore keyStore = KeyStore.getInstance("jks");
			keyStore.load(null);
			String certLocation = IOUtils
					.readLine("Enter the location of the certificate (PEM format)");

			java.security.cert.Certificate[] chain = { CertUtil
					.loadCertificate(new File(certLocation)) };


			keyStore.setEntry("tomcat", new KeyStore.TrustedCertificateEntry(chain[0]), null);
			String password = IOUtils
            .readLine("Enter a password for you truststore");

			FileOutputStream fos = new FileOutputStream(keystoreLocation);
			keyStore.store(fos,password.toCharArray());
			fos.close();
		}

		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}