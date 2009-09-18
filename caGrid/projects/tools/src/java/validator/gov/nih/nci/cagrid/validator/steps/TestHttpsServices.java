package gov.nih.nci.cagrid.validator.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class TestHttpsServices extends Step{
	public static class MyTrustManager implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

	private static void trustAllHttpsCertificates() throws Exception {
		// Trust manager does not validate certificate chains:
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager trustManager = new MyTrustManager();
		trustAllCerts[0] = trustManager;
		javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("SSL");
		sslContext.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}

	public static void main(String[] args) {
		try {
			// String urlName =
			// "https://cagrid-auth.nci.nih.gov:8443/wsrf/services/cagrid/AuthenticationService";
			// String urlName = "https://www.sun.com ";
			String urlName = "http://blah.com ";
			String resp = "";
			URL url;
			URLConnection urlConn;
			DataInputStream input;
			String str = "";

			try {
				// Properties sysProperties = System.getProperties();
				// change proxy settings if required and enable the below lines
				// sysProperties.put("proxyHost", "proxy.starhub.net.sg");
				// sysProperties.put("proxyPort", "8080");
				// sysProperties.put("proxySet", "true");

				// tell the JRE to ignore the hostname
				HostnameVerifier hv = new HostnameVerifier() {
					public boolean verify(String urlHostName, SSLSession session) {
						System.out.println("URL Host: " + urlHostName + " vs. "+ session.getPeerHost());
						return true;
					}
				};

				// Tell the JRE to trust any https server.
				trustAllHttpsCertificates();
				HttpsURLConnection.setDefaultHostnameVerifier(hv);

				url = new URL(urlName);
				// urlConn.connect();
				urlConn = url.openConnection();
				urlConn.setConnectTimeout(5000);

				urlConn.setDoInput(true);
				urlConn.setUseCaches(false);

				if (urlConn instanceof HttpsURLConnection) {
					HttpsURLConnection httpsConnection = (HttpsURLConnection) urlConn;
					try {
						System.out.println("----------");
						System.out.println("Trying to connect: " + urlName);
						httpsConnection.connect();
						int response = httpsConnection.getResponseCode();
						System.out.println("Response Code: " + response);
					} catch (Exception e) {
						// e.printStackTrace();
						System.out.println("The URL specified was unable to connect. Connection Timed out");
						return;
					}
					int n = 1;
					String key;
					while ((key = urlConn.getHeaderFieldKey(n)) != null) {
						String value = urlConn.getHeaderField(n);
						System.out.println(key + ": " + value);
						n++;
					}
					// urlConn.setRequestProperty("Content-Type",
					// "application/x-www-form-urlencoded");
					input = new DataInputStream(urlConn.getInputStream());

					// System.out.println("ContentType: " +
					// urlConn.getContentType());
					System.out.println("ContentLength: "+ urlConn.getContentLength());
					System.out.println("ContentEncoding: "+ urlConn.getContentEncoding());
					// System.out.println("Date: " + urlConn.getDate());
					System.out.println("Expiration: " + urlConn.getExpiration());
					System.out.println("LastModifed: " + urlConn.getLastModified());
					System.out.println("----------");

					while (null != ((str = input.readLine()))) {
						if (str.length() > 0) {
							str = str.trim();
							if (!str.equals("")) {
								// System.out.println(str);
								resp += str;
							}
						}
					}
					input.close();
				}
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			System.out.println("Response: " + resp);
		} catch (Exception exception) {
			System.out.println("Error: " + exception);
			exception.printStackTrace();
		}
	}
	@Override
	public void runStep() throws Throwable {

	}
}
