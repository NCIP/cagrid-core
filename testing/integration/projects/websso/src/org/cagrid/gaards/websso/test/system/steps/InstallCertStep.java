package org.cagrid.gaards.websso.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;
import org.cagrid.gaards.websso.test.system.WebSSOSystemTest;

//install certs into websso-client jdk cacerts file
public class InstallCertStep extends Step {
	private File cacertsFile;
	private String webSSOServerHostName;
	private int portNumber;
	private int aliasID;
	private String passphrase = "changeit";
	private static Logger log = Logger.getLogger(InstallCertStep.class);
	
	public InstallCertStep(File cacertsFile,String webSSOServerHostName, int portNumber,int aliasID) {
		this.cacertsFile=cacertsFile;
		this.webSSOServerHostName = webSSOServerHostName;
		this.portNumber = portNumber;
		this.aliasID=aliasID;
	}

	public void runStep() throws Throwable {
		installWebSSOCertificates();
	}

	private void installWebSSOCertificates()throws Exception{
		InputStream in = new FileInputStream(cacertsFile);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, passphrase.toCharArray());
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf
				.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();

		log.debug("Opening connection to " + webSSOServerHostName + ":" + portNumber + "...");
		SSLSocket socket = (SSLSocket) factory.createSocket(webSSOServerHostName, portNumber);
		socket.setSoTimeout(10000);
		try {
			log.debug("Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			log.debug("No errors, certificate is already trusted");
		} catch (SSLException e) {
			log.debug("certificates not found in truststore. Adding certificates to trust store.");
		}

		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			log.debug("Could not obtain server certificate chain");
			return;
		}
		log.debug("Server sent " + chain.length + " certificate(s):");
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			log.debug(" " + (i + 1) + " Subject "+ cert.getSubjectDN());
			log.debug("   Issuer  " + cert.getIssuerDN());
			sha1.update(cert.getEncoded());
			log.debug("   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			log.debug("   md5     " + toHexString(md5.digest()));
		}
		
		int k=0;
		X509Certificate cert = chain[k];
		String alias = webSSOServerHostName + "-" + (k + 1+aliasID);
		ks.setCertificateEntry(alias, cert);

		OutputStream out = new FileOutputStream(cacertsFile);
		ks.store(out, passphrase.toCharArray());
		out.close();
		log.debug(cert);
		log.debug("Added certificate to keystore 'cacerts' using alias '"+ alias + "'");
	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	private static class SavingTrustManager implements X509TrustManager {
		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}
	
	public static void main(String[] args) throws Throwable{
		File file = new File("C:/devroot/caGrid/cagrid-1-0/tests/projects/websso/tmp/websso-client-example/ext/dependencies-cert/cert/cacerts-"+WebSSOSystemTest.getProjectVersion()+".cert");
		InstallCertStep certStep=new InstallCertStep(file,"NCI-GARMILLAS-1",18443,2);
		certStep.runStep();
	}
}
