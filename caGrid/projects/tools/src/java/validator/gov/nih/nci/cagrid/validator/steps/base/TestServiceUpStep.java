package gov.nih.nci.cagrid.validator.steps.base;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.validator.steps.TransportUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


/**
 * This step connects to a grid service and verifies that it is running
 * 
 * @author Rakesh Dhaval
 * @author David W. Ervin
 */
public class TestServiceUpStep extends Step {
    
    private String serviceURL;
    
    public TestServiceUpStep(String serviceURL) {
        this.serviceURL = serviceURL;
    }


    @Override
    public void runStep() throws Throwable {
        System.out.println("Checking status of " + this.serviceURL);
        
        URLConnection connection = null;
        // connect to the url and validate a return
        try {
            System.out.println("Resetting transports");
            TransportUtil.resetUrlTransports();
            
            URL url = new URL(serviceURL);
            connection = url.openConnection();
            // willing to wait 5 seconds
            // connection.setConnectTimeout(5000);
            
            if (connection instanceof HttpsURLConnection) {
                System.out.println("HTTPS URL CONNECTION");
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                
                HostnameVerifier hv = new HostnameVerifier() {
                    public boolean verify(String urlHostName, SSLSession session) {
                        System.out.println("Allowing URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                        return true;
                    }
                };
                httpsConnection.setHostnameVerifier(hv);
                
                // Trust manager does not validate certificate chains:
                TrustManager[] trustAllCerts = new TrustManager[] {
                    new IgnorantTrustManager()
                };
                
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, null);
                httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }

        // open the connection
        try {
            System.out.println("----------");
            System.out.println("Trying to connect: " + serviceURL);
            connection.connect();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            fail("The URL specified was unable to connect. Connection Timed out");
        }
        
        // print connection header
        int n = 1;
        String key;
        while ((key = connection.getHeaderFieldKey(n)) != null) {
            String value = connection.getHeaderField(n);
            System.out.println(key + ": " + value);
            n++;
        }

        // print connection information
        System.out.println("ContentLength: " + connection.getContentLength());
        System.out.println("ContentEncoding: " + connection.getContentEncoding());
        System.out.println("Expiration: " + connection.getExpiration());
        System.out.println("LastModifed: " + connection.getLastModified());
        System.out.println("----------");

        InputStream connectionStream = connection.getInputStream();
        StringBuffer response = Utils.inputStreamToStringBuffer(connectionStream);
        connectionStream.close();
        System.out.println(response.toString());
        ((HttpURLConnection) connection).disconnect();
    }
}