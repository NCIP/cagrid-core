package org.cagrid.identifiers.test.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import namingauthority.IdentifierValues;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;


public class IdentifiersTestInfo {
	public static final String WEBAPP_PROJ_DIR = "../../../caGrid/projects/identifiers-namingauthority";
    public static final String WEBAPP_TMP_DIR = "tmp/TempWEB";
    public static final String WEBAPP_NA_PROPERTIES = WEBAPP_TMP_DIR + "/WebContent/WEB-INF/na.properties";
    public static final String WEBAPP_URL_PATH = "/namingauthority/NamingAuthorityService/";
    
    public static final String GRIDSVC_PROJ_DIR = "../../../caGrid/projects/identifiers-namingauthority-gridsvc";
    public static final String GRIDSVC_TMP_DIR = "tmp/TempSVC";
    public static final String GRIDSVC_NA_PROPERTIES = GRIDSVC_TMP_DIR + "/etc/na.properties";
    public static final String GRIDSVC_URL_PATH = "cagrid/IdentifiersNAService";
        
	private List<URI> identifiers = null;
	private List<IdentifierValues> identifierValues = null;
	private ServiceContainer webAppContainer = null;
	private ServiceContainer gridSvcContainer = null;
	
	public List<URI> getIdentifiers() { 
		return this.identifiers; 
	}
	
	public List<IdentifierValues> getIdentifierValues() {
		return this.identifierValues;
	}
	
	public void addIdentifier(URI identifier, IdentifierValues values) {
		if (this.identifiers == null) {
			this.identifiers = new ArrayList<URI>();
			this.identifierValues = new ArrayList<IdentifierValues>();
		}
		
		this.identifiers.add(identifier);
		this.identifierValues.add(values);
	}
	
	public ServiceContainer createWebAppContainer() throws IOException {
		webAppContainer = createContainer();
		return webAppContainer;
	}
	
	public ServiceContainer createGridSvcContainer() throws IOException {
		gridSvcContainer = createContainer();
		return gridSvcContainer;
	}
	
	public ServiceContainer getWebAppContainer() {
		return this.webAppContainer;
	}
	
	public ServiceContainer getGridSvcContainer() {
		return this.gridSvcContainer;
	}
	
	public EndpointReferenceType getGridSvcEPR() throws MalformedURIException {
		return this.gridSvcContainer.getServiceEPR(GRIDSVC_URL_PATH);
	}
	
	public String getNAPrefix() throws MalformedURIException {
		URI baseURI = this.webAppContainer.getContainerBaseURI();
		return "http://" + baseURI.getHost() + ":" + baseURI.getPort() + WEBAPP_URL_PATH;
	}

	public String getGridSvcURL() throws MalformedURIException {
		return this.gridSvcContainer.getContainerBaseURI().toString() + GRIDSVC_URL_PATH;
	}
	
	//
	// Private Stuff
	//
	private ServiceContainer createContainer() throws IOException {
		return ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER);
	}
}
