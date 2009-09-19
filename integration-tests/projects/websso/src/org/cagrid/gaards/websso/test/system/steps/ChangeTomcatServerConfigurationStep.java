package org.cagrid.gaards.websso.test.system.steps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Iterator;

import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerPorts;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerProperties;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class ChangeTomcatServerConfigurationStep extends Step{
	
	private ContainerProperties containerProperties;
	private String hostname;
	private int httpsPortNumber;
	private Integer httpPortNumber=null;
	public ChangeTomcatServerConfigurationStep(ContainerProperties containerProperties,String hostname,int httpsPortNumber) {
		this.containerProperties=containerProperties;
		this.hostname=hostname;
		this.httpsPortNumber=httpsPortNumber;
	}


	@Override
	public void runStep() throws Throwable {
		String	tomcatCertsDir =containerProperties.getContainerDirectory()+ File.separator + "certificates";

		// locate certificates
		File hostKey = new File(tomcatCertsDir, hostname + "-key.pem");
		File hostCert = new File(tomcatCertsDir, hostname + "-cert.pem");
		String keyStorePassword = "websso";

		File keyStoreLocation = new File(tomcatCertsDir, hostname);
		generateTomcatKeyStore(hostCert, hostKey, keyStorePassword,keyStoreLocation);

	    // edit the conf/server.xml to set the key, cert, and CA dirs for the https connector
	    File serverConfFile = new File(containerProperties.getContainerDirectory(), "conf" + File.separator + "server.xml");
	    Document confDocument = null;
	    try {
	        confDocument = XMLUtilities.fileNameToDocument(serverConfFile.getCanonicalPath());
	    } catch (Exception ex) {
	        throw new ContainerException("Error loading server configuration document for editing: " + ex.getMessage(), ex);
	    }
	    // locate the https connector element
	    Element serverElement = confDocument.getRootElement();
	    addHttpsConnector(keyStorePassword, keyStoreLocation, serverElement);
	    modifyConnectorPorts(serverElement);
	    // write the config back to disk
	    String confXml = XMLUtilities.documentToString(confDocument);
	    try {
	        Utils.stringBufferToFile(new StringBuffer(confXml), serverConfFile.getCanonicalPath());
	    } catch (Exception ex) {
	        throw new ContainerException("Error writing server configuration file back to disk: " + ex.getMessage(), ex);
	    }
		
	}

	public void modifyConnectorPorts(Element serverElement){
		Element service=(Element)serverElement.getChildren("Service").get(0);
        Element coyoteHttpElement=(Element)service.getChildren("Connector").get(0);
        coyoteHttpElement.removeAttribute("port");
        coyoteHttpElement.removeAttribute("redirectPort");
        coyoteHttpElement.setAttribute(new Attribute("port",""+containerProperties.getPortPreference().getPort()));
        coyoteHttpElement.setAttribute(new Attribute("redirectPort",""+httpsPortNumber));

        Element coyoteJk2Element=(Element)service.getChildren("Connector").get(1);
        coyoteJk2Element.removeAttribute("port");
        coyoteJk2Element.removeAttribute("redirectPort");
        coyoteJk2Element.setAttribute(new Attribute("port",""+(containerProperties.getPortPreference().getPort()+102)));
        coyoteJk2Element.setAttribute(new Attribute("redirectPort",""+httpsPortNumber));
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	private void addHttpsConnector(String keyStorePassword,
			File keyStoreLocation, Element serverElement) throws IOException,
			ContainerException {
		Iterator httpsConnectorElements = serverElement.getDescendants(new Filter() {
	        public boolean matches(Object o) {
	            if (o instanceof Element) {
	                Element e = (Element) o;
	                if (e.getName().equals("Connector")) {
	                	if(e.getAttribute("schema")!=null && "https".equals(e.getAttribute("schema").getValue())){
	                		return true;
	                	}
	                }
	            }
	            return false;
	        }
	    });
	    // verify there is only one connector
	    if (!httpsConnectorElements.hasNext()) {
	    	Element service=(Element)serverElement.getChildren("Service").get(0);

	    	Element connector = new Element("Connector");
		    connector.setAttribute(new Attribute("acceptCount", "100"));
		    connector.setAttribute(new Attribute("clientAuth", "false"));
		    connector.setAttribute(new Attribute("debug", "0"));
		    connector.setAttribute(new Attribute("disableUploadTimeout", "true"));
		    connector.setAttribute(new Attribute("enableLookups", "false"));
		    connector.setAttribute(new Attribute("keystoreFile", keyStoreLocation.getCanonicalPath()));
		    connector.setAttribute(new Attribute("keystorePass", keyStorePassword));
		    connector.setAttribute(new Attribute("maxHttpHeaderSize", "8192"));
		    connector.setAttribute(new Attribute("maxSpareThreads", "75"));
		    connector.setAttribute(new Attribute("maxThreads", "150"));
		    connector.setAttribute(new Attribute("minSpareThreads", "25"));
		    connector.setAttribute(new Attribute("port", ""+httpsPortNumber));
		    connector.setAttribute(new Attribute("schema", "https"));
		    connector.setAttribute(new Attribute("secure", "true"));
		    connector.setAttribute(new Attribute("sslProtocol", "TLS"));
		    
		    service.getChildren().add(connector);
	    }else{
	        throw new ContainerException("More than one HTTPS connector was found in the server configuration!");
	    }
	}
	
	public void generateTomcatKeyStore(File certFile,File keyFile,String keyPassword,File keystoreLocation){
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("jks");
			keyStore.load(null);
			java.security.cert.Certificate[] chain = { CertUtil
					.loadCertificate(certFile) };
			PrivateKey privateKey = KeyUtil
					.loadPrivateKey(keyFile, keyPassword);
			
			keyStore.setEntry("tomcat", new KeyStore.PrivateKeyEntry(
					privateKey, chain), new KeyStore.PasswordProtection(
							keyPassword.toCharArray()));
			
			FileOutputStream fos = new FileOutputStream(keystoreLocation);
			keyStore.store(fos, keyPassword.toCharArray());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//service container listening port
	public int getHttpPortNumber() {
		if(httpPortNumber==null)
			httpPortNumber=containerProperties.getPortPreference().getPort();
		return httpPortNumber;
	}
	
	public static void main(String[] args) throws Throwable {
		ContainerProperties containerProperties=new ContainerProperties();
		containerProperties.setContainerDirectory(new File("C:/devroot/caGrid/cagrid-1-0/tests/projects/websso/tmp/Tomcat17774tmp"));
		containerProperties.setPortPreference(new ContainerPorts(8111,111));
		ChangeTomcatServerConfigurationStep configurationStep=new ChangeTomcatServerConfigurationStep(containerProperties,"webssoserver",18443);
		configurationStep.runStep();
	}
}
