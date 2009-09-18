package org.cagrid.gaards.pki;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.bouncycastle.openssl.PEMReader;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class KeyUtil {

	public static KeyPair generateRSAKeyPair2048() throws Exception {
		SecurityUtil.init();
		return generateRSAKeyPair2048("BC");
	}

	public static KeyPair generateRSAKeyPair2048(String provider)
			throws Exception {
		return generateRSAKeyPair(provider, 2048);
	}

	public static KeyPair generateRSAKeyPair1024() throws Exception {
		SecurityUtil.init();
		return generateRSAKeyPair1024("BC");
	}

	public static KeyPair generateRSAKeyPair1024(String provider)
			throws Exception {
		return generateRSAKeyPair(provider, 1024);
	}

	public static KeyPair generateRSAKeyPair512() throws Exception {
		SecurityUtil.init();
		return generateRSAKeyPair512("BC");
	}

	public static KeyPair generateRSAKeyPair512(String provider)
			throws Exception {
		return generateRSAKeyPair(provider, 512);
	}

	public static KeyPair generateRSAKeyPair(int size) throws Exception {
		SecurityUtil.init();
		return generateRSAKeyPair("BC", size);
	}

	public static KeyPair generateRSAKeyPair(String provider, int size)
			throws Exception {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", provider);
		kpGen.initialize(size, new SecureRandom());
		return kpGen.generateKeyPair();
	}

	public static void writePrivateKey(PrivateKey key, File file)
			throws Exception {
		writePrivateKey(key, file, null);
	}

	public static void writePrivateKey(PrivateKey key, File file,
			String password) throws Exception {
		OpenSSLKey ssl = new BouncyCastleOpenSSLKey(key);
		if (password != null) {
			ssl.encrypt(password);
		}
		ssl.writeTo(file.getAbsolutePath());
	}

	public static String writePrivateKey(PrivateKey key, String password)
			throws Exception {
		OpenSSLKey ssl = new BouncyCastleOpenSSLKey(key);
		if (password != null) {
			ssl.encrypt(password);
		}
		StringWriter sw = new StringWriter();
		ssl.writeTo(sw);
		String s = sw.toString();
		sw.close();
		return s;
	}

	public static void writePublicKey(PublicKey key, File path)
			throws IOException {
		PEMWriter pem = new PEMWriter(new FileWriter(path));
		pem.writeObject(key);
		pem.close();
	}

	public static PrivateKey loadPrivateKey(File location, String password)
			throws IOException, GeneralSecurityException {
		OpenSSLKey key = new BouncyCastleOpenSSLKey(location.getAbsolutePath());
		if (key.isEncrypted()) {
			key.decrypt(password);
		}
		return key.getPrivateKey();
	}

	public static PrivateKey loadPrivateKey(InputStream in, String password)
			throws IOException, GeneralSecurityException {
		OpenSSLKey key = new BouncyCastleOpenSSLKey(in);
		if (key.isEncrypted()) {
			key.decrypt(password);
		}
		return key.getPrivateKey();
	}

	public static PublicKey loadPublicKey(String key) throws IOException,
			GeneralSecurityException {
		SecurityUtil.init();
		return loadPublicKey("BC", key);
	}

	public static PublicKey loadPublicKey(String provider, String key)
			throws IOException, GeneralSecurityException {
		StringReader in = new StringReader(key);
		PEMReader reader = new PEMReader(in, null, provider);
		PublicKey pk = (PublicKey) reader.readObject();
		reader.close();
		return pk;
	}

	public static PublicKey loadPublicKey(File location) throws IOException,
			GeneralSecurityException {
		SecurityUtil.init();
		return loadPublicKey("BC", location);
	}

	public static PublicKey loadPublicKey(String provider, File location)
			throws IOException, GeneralSecurityException {
		FileReader in = new FileReader(location);
		PEMReader reader = new PEMReader(in, null, provider);
		PublicKey pk = (PublicKey) reader.readObject();
		reader.close();
		return pk;
	}

	public static String writePublicKey(PublicKey key) throws IOException {
		StringWriter sw = new StringWriter();
		PEMWriter pem = new PEMWriter(sw);
		pem.writeObject(key);
		pem.close();
		return sw.toString();
	}
}
