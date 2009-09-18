package gov.nih.nci.cagrid.validator.steps.base;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.validator.steps.TransportUtil;

import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;

import org.xml.sax.InputSource;

/** 
 *  PullWsdlStep
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Mar 28, 2008 3:08:11 PM
 * @version $Id: PullWsdlStep.java,v 1.3 2008-03-31 20:04:23 dervin Exp $ 
 */
public class PullWsdlStep extends Step {
    
    private String rawUrl;
    
    public PullWsdlStep(String rawUrl) {
        this.rawUrl = rawUrl;
    }
    

    public void runStep() throws Throwable {
        TransportUtil.resetUrlTransports();
        
        URL wsdlUrl = null;
        try {
            wsdlUrl = new URL(rawUrl + "?wsdl");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            fail("Error creating WSDL URL: " + ex.getMessage());
        }
        
        try {
            System.out.println("Pulling WSDL from URL " + wsdlUrl.toString());
            WSDLLocator locator = new WSDLLocatorImpl(wsdlUrl.toString());
            WSDLFactory.newInstance().newWSDLReader().readWSDL(locator);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error parsing WSDL: " + ex.getMessage());
        }
    }
    
    
    private class WSDLLocatorImpl implements WSDLLocator {
        private String baseWsdlUrl = null;
        private String lastImportedUri = null;
        
        private HostnameVerifier simpleNameVerifier = null;
        
        public WSDLLocatorImpl(String baseWsdlUrl) {
            this.baseWsdlUrl = baseWsdlUrl;
            this.simpleNameVerifier = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    System.out.println("URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                    return true;
                }
            };
        }
        
        
        public InputSource getBaseInputSource() {
            System.out.println("Get base input source");
            return getInputSource(baseWsdlUrl.toString());
        }


        public String getBaseURI() {
            System.out.println("Get base URI");
            return baseWsdlUrl.toString();
        }


        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            System.out.println("Get import input source");
            System.out.println("\tParent: " + parentLocation);
            System.out.println("\tImport: " + importLocation);
            String url = "";
            if (!(importLocation.startsWith("http://") || importLocation.startsWith("https://"))) {
                // relative to parent
                int index = parentLocation.lastIndexOf('/');
                url = parentLocation.substring(0, index);
                url += "/" + importLocation;
            } else {
                url = importLocation;
            }
            try {
                URL tmp = new URL(url);
                lastImportedUri = tmp.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error creating import URL: " + ex.getMessage());
            }
            return getInputSource(lastImportedUri);
        }


        public String getLatestImportURI() {
            System.out.println("Get latest import URI");
            return lastImportedUri;
        }


        private InputSource getInputSource(String location) {
            InputSource source = null;
            URLConnection connection = getConnection(location);
            try {
                InputStream stream = connection.getInputStream();
                StringBuffer buffer = Utils.inputStreamToStringBuffer(stream);
                stream.close();
                source = new InputSource(new StringReader(buffer.toString()));
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error creating input source for location " + location);
            }
            return source;
        }


        private URLConnection getConnection(String location) {
            URLConnection connection = null;
            try {
                URL url = new URL(location);
                connection = url.openConnection();
                if (connection instanceof HttpsURLConnection) {
                    // use the simple hostname verifier
                    HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                    httpsConnection.setHostnameVerifier(simpleNameVerifier);
                    // Trust manager does not validate certificate chains:
                    javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
                    javax.net.ssl.TrustManager trustManager = new IgnorantTrustManager();
                    trustAllCerts[0] = trustManager;
                    javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("SSL");
                    sslContext.init(null, trustAllCerts, null);
                    httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("ERROR GETTING CONNECTION TO " + location + ": " + ex.getMessage());
            }
            return connection;
        }
    }
}
