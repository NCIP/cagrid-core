package gov.nih.nci.cagrid.validator.steps;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.*;
//import javax.net.ssl.HttpsURLConnection;

public class TestSecureServicesBak {
  public static void main(String[] args) {
	  try {		  
	        //String urlName = "https://cagrid-auth.nci.nih.gov:8443/wsrf/services/cagrid/AuthenticationService";
	        String urlName = "https://www.sun.com ";
	        
	     // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[]{
	            new X509TrustManager() {
	                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }
	                public void checkClientTrusted(
	                    java.security.cert.X509Certificate[] certs, String authType) {
	                }
	                public void checkServerTrusted(
	                    java.security.cert.X509Certificate[] certs, String authType) {
	                }
	            }
	        };
	        
	        // Install the all-trusting trust manager
	        try {
	            SSLContext sc = SSLContext.getInstance("SSL");
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        } catch (Exception e) {
	        }
	        
	        // Now you can access an https URL without having the certificate in the truststore
	        try {
	            URL url = new URL(urlName);
	            //
	            URLConnection connection = url.openConnection();
	            if (connection instanceof HttpsURLConnection ) {
					HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
					System.out.println(connection.getURL());
					connection.connect();
				}
				
		        int n = 1;
		        String key;
		        while ((key = connection.getHeaderFieldKey(n)) != null) {
		          String value = connection.getHeaderField(n);
		          System.out.println(key + ": " + value);
		          n++;
		        }
	            //
	            
	        } catch (MalformedURLException e) {
	        }
	      
	        /*
	        URL url = new URL(urlName);
	        URLConnection connection = url.openConnection();
	        if (connection instanceof HttpsURLConnection ) {
				HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
				connection.connect();
			}
	        // print header fields

	        int n = 1;
	        String key;
	        while ((key = connection.getHeaderFieldKey(n)) != null) {
	          String value = connection.getHeaderField(n);
	          System.out.println(key + ": " + value);
	          n++;
	        }

	        // print convenience functions

	        System.out.println("----------");
	        System.out.println("getContentType: " + connection.getContentType());
	        System.out.println("getContentLength: " + connection.getContentLength());
	        System.out.println("getContentEncoding: " + connection.getContentEncoding());
	        System.out.println("getDate: " + connection.getDate());
	        System.out.println("getExpiration: " + connection.getExpiration());
	        System.out.println("getLastModifed: " + connection.getLastModified());
	        System.out.println("----------");

	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

	        // print first 100 lines of contents

	        String line;
	        n = 1;
	        while ((line = in.readLine()) != null && n <= 100) {
	          System.out.println(line);
	          n++;
	        }
	        if (line != null)
	          System.out.println(". . .");
	          */
	      } catch (Exception exception) {
	        System.out.println("Error: " + exception);
	        exception.printStackTrace();
    }

  }

}

