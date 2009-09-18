package org.cagrid.gaards.websso.test.system.steps;

import java.io.File;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.haste.Step;

/**
 * modify websso-properties.xml in websso project : add dorian url,cds url and host identity for websso-clients
 * @author garmillas
 *
 */
public class ChangeWebSSOPropertiesStep extends Step {

	private String hostCertificate;
	private String hostCertificateKey;
	private String dorianServiceURL;
	private String cdsServiceURL;
	private String cdsServiceIdentity;
	private String dorianServiceIdentity;
	private String delegatedApplicationHostIdentity;
	private File tempWebSSOService;

	public ChangeWebSSOPropertiesStep(File tempWebSSOService,
			String hostCertificate,String hostCertificateKey, String dorianServiceURL, String cdsServiceURL,
			String delegatedApplicationHostIdentity,String cdsServiceIdentity,String dorianServiceIdentity) {
		this.hostCertificate = hostCertificate;
		this.hostCertificateKey = hostCertificateKey;
		this.dorianServiceURL = dorianServiceURL;
		this.cdsServiceURL = cdsServiceURL;
		this.delegatedApplicationHostIdentity = delegatedApplicationHostIdentity;
		this.tempWebSSOService=tempWebSSOService;
		this.cdsServiceIdentity=cdsServiceIdentity;
		this.dorianServiceIdentity=dorianServiceIdentity;
	}

	@Override
	public void runStep() throws Throwable {

	    // edit the src/resources/websso-properties-template.xml to set the key, cert, and CA dirs for the https connector
		File webssopropertiesfile = new File(tempWebSSOService
				.getCanonicalPath(), "ext" + File.separator + "target_grid"
				+ File.separator + "websso-properties.xml");
		File targetWebSSOPropertiesfile = new File(tempWebSSOService
				.getCanonicalPath(), "src" + File.separator + "resources"
				+ File.separator + "websso-properties.xml");
		
	    Document confDocument = null;
	    try {
	        confDocument = XMLUtilities.fileNameToDocument(webssopropertiesfile.getCanonicalPath());
	    } catch (Exception ex) {
	        throw new ContainerException("Error loading websso server properties document for editing: " + ex.getMessage(), ex);
	    }
	    // locate the https connector element
	    Element webSSOPropertiesElement = confDocument.getRootElement();
	    
	    editWebSSOProperties(webSSOPropertiesElement);
	    
	    // write the webssoproperties.xml back to disk
	    String confXml = XMLUtilities.documentToString(confDocument);
	    try {
	    	Utils.copyFile(webssopropertiesfile, targetWebSSOPropertiesfile);
	        Utils.stringBufferToFile(new StringBuffer(confXml),targetWebSSOPropertiesfile.getAbsolutePath());
	    } catch (Exception ex) {
	        throw new ContainerException("Error writing server configuration file back to disk: " + ex.getMessage(), ex);
	    }
	}
	
	@SuppressWarnings("unchecked")
	private void editWebSSOProperties(Element webSSOPropertiesElement) {
		
		try {
			XPath hostCertificatePath = XPath
					.newInstance("/websso-properties/websso-server-information/host-credential-certificate-file-path");
			Element certificateElement = (Element) hostCertificatePath
					.selectSingleNode(webSSOPropertiesElement);
			certificateElement.removeContent();
			certificateElement.addContent(this.hostCertificate);

			XPath keyCertificatePath = XPath
					.newInstance("/websso-properties/websso-server-information/host-credential-key-file-path");
			Element keyCertificate = (Element) keyCertificatePath
					.selectSingleNode(webSSOPropertiesElement);
			keyCertificate.removeContent();
			keyCertificate.addContent(this.hostCertificateKey);

			XPath cdsServiceURLPath = XPath
					.newInstance("/websso-properties/credential-delegation-service-information/service-url");
			Element cdsServiceURL = (Element) cdsServiceURLPath
					.selectSingleNode(webSSOPropertiesElement);
			cdsServiceURL.removeContent();
			cdsServiceURL.addContent(this.cdsServiceURL);
			
			XPath cdsServiceIdentityPath = XPath.newInstance("/websso-properties/credential-delegation-service-information/service-identity");
			Element cdsServiceIdentity = (Element) cdsServiceIdentityPath.selectSingleNode(webSSOPropertiesElement);
			cdsServiceIdentity.removeContent();
			cdsServiceIdentity.addContent(this.cdsServiceIdentity);

			XPath dorianServiceURLPath = XPath
					.newInstance("/websso-properties/dorian-services-information/dorian-service-descriptor/service-url");
			Element dorianServiceURL = (Element) dorianServiceURLPath
					.selectSingleNode(webSSOPropertiesElement);
			dorianServiceURL.removeContent();
			dorianServiceURL.addContent(this.dorianServiceURL);

			XPath dorianServiceIdentityPath = XPath.newInstance("/websso-properties/dorian-services-information/dorian-service-descriptor/service-identity");
			Element dorianServiceIdentity = (Element) dorianServiceIdentityPath.selectSingleNode(webSSOPropertiesElement);
			dorianServiceIdentity.removeContent();
			dorianServiceIdentity.addContent(this.dorianServiceIdentity);
			
			XPath hostIdentityPath = XPath
					.newInstance("/websso-properties/delegated-applications-group/delegated-application-list/delegated-application/host-identity");
			List<Element> hostIdentities = (List<Element>)hostIdentityPath.selectNodes(webSSOPropertiesElement);
			int i=1;
			for (Element hostIdentity : hostIdentities) {
				hostIdentity.removeContent();
				hostIdentity.addContent(delegatedApplicationHostIdentity + i);
				i++;
			}	
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Throwable {

		File tempWebSSOService = new File("C:/devroot/caGrid/cagrid-1-0/tests/projects/websso/tmp/websso");
		ChangeWebSSOPropertiesStep step = new ChangeWebSSOPropertiesStep(
				tempWebSSOService, null, null, null, null, "/C=US/O=abc/OU=xyz/OU=caGrid/OU=Services/CN=webssoclient",null,null);
		step.runStep();
	}
}
