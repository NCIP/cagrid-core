package org.cagrid.gaards.websso.test.system.steps;

import java.io.File;
import java.util.List;

import org.cagrid.gaards.websso.test.system.WebSSOSystemTest;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;



import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.haste.Step;

/**
 * Add certificates and key file as Filter param values in web.xml.
 * @author garmillas
 *
 */
public class WebSSOClientCertificatesStep extends Step {
	
	private File tempWebSSOClientService;
	private String hostCertificate;
	private String hostCertificateKey;

	public WebSSOClientCertificatesStep(File tempWebSSOClientService,
			String hostCertificate, String hostCertificateKey) {
		this.tempWebSSOClientService = tempWebSSOClientService;
		this.hostCertificate=hostCertificate;
		this.hostCertificateKey=hostCertificateKey;
	}

	@Override
	public void runStep() throws Throwable {
		modifyCASJasigWebXML();
		modifyCASAcegiApplicationContextXML();
	}
	
	private void modifyCASAcegiApplicationContextXML() {
	}

	@SuppressWarnings("unchecked")
	private void modifyCASJasigWebXML() throws Exception {
		File webssopropertiesfile = new File(tempWebSSOClientService
				.getCanonicalPath(), "ext" + File.separator
				+ "dependencies-xml-resources" + File.separator + "xmls"
				+ File.separator + "web-template-jasig-"+WebSSOSystemTest.getProjectVersion()+".xml");
		
	    Document confDocument = null;
	    try {
	        confDocument = XMLUtilities.fileNameToDocument(webssopropertiesfile.getCanonicalPath());
	    } catch (Exception ex) {
	        throw new ContainerException("Error loading websso server properties document for editing: " + ex.getMessage(), ex);
	    }
	    // locate the param name  certificate-file-path and key-file-path
	    Element webXMLElement= confDocument.getRootElement();
	    Namespace namespace=Namespace.getNamespace("", "http://java.sun.com/xml/ns/j2ee");
	    List<Element> filters=(List<Element>)webXMLElement.getChildren("filter",namespace);
		for (Element filter : filters) {
			Element filterName=filter.getChild("filter-name",namespace);
			if (filterName.getValue().equals("caGRID WebSSO Delegation Lookup Filter")) {
				List<Element> initParams = (List<Element>) filter.getChildren("init-param",namespace);
				for (Element initParam : initParams) {
					Element paramName = initParam.getChild("param-name",namespace);
					if (paramName.getValue().equals("certificate-file-path")) {
						Element paramValue = initParam.getChild("param-value",namespace);
						paramValue.removeContent();
						paramValue.addContent(hostCertificate);
					}
					if (paramName.getValue().equals("key-file-path")) {
						Element paramValue = initParam.getChild("param-value",namespace);
						paramValue.removeContent();
						paramValue.addContent(hostCertificateKey);
					}
				}
			}			
		}
	    // write the webssoproperties.xml back to disk
	    String confXml = XMLUtilities.documentToString(confDocument);
	    try {
	    	System.out.println(webssopropertiesfile.getCanonicalPath());
	        Utils.stringBufferToFile(new StringBuffer(confXml), webssopropertiesfile.getCanonicalPath());
	    } catch (Exception ex) {
	        throw new ContainerException("Error writing server configuration file back to disk: " + ex.getMessage(), ex);
	    }
	}

	public static void main(String[] args) throws Throwable {
		File tempWebSSOService = new File("C:/devroot/caGrid/cagrid-1-0/tests/projects/websso/tmp/websso-client-example");
		WebSSOClientCertificatesStep step = new WebSSOClientCertificatesStep(tempWebSSOService,"xxxxxxxx","yyyyyyyy");
		step.runStep();
	}
}
