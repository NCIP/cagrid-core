package org.cagrid.identifiers.test.system;

import gov.nih.nci.cagrid.testing.core.TestingConstants;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.TomcatSecureServiceContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import namingauthority.IdentifierValues;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;
import org.globus.gsi.GlobusCredential;


public class IdentifiersTestInfo {
	public static final String WEBAPP_PROJ_DIR = "../../../caGrid/projects/identifiers-namingauthority";
    public static final String WEBAPP_TMP_DIR = "tmp/TempWEB";
    public static final String WEBAPP_NA_PROPERTIES = WEBAPP_TMP_DIR + "/WebContent/WEB-INF/na.properties";
    public static final String WEBAPP_URL_PATH = "/namingauthority/NamingAuthorityService/";
    
    public static final String GRIDSVC_PROJ_DIR = "../../../caGrid/projects/identifiers-namingauthority-gridsvc";
    public static final String GRIDSVC_TMP_DIR = "tmp/TempSVC";
    public static final String GRIDSVC_NA_PROPERTIES = GRIDSVC_TMP_DIR + "/etc/na.properties";
    public static final String GRIDSVC_URL_PATH = "cagrid/IdentifiersNAService";
    
    public static final String PURLZ_ZIP = "resources/PURLZ-Server-1.6.1.zip";
    public static final String PURLZ_BOOTLOADER = "lib" + File.separator + "1060netkernel-2.8.5.jar";
    public static final String PURLZ_BOOTLOADER_CFG = "etc" + File.separator + "bootloader.cfg";
    public static final String PURLZ_DB = "purls";
    public static final String PURLZ_LOGIN_COOKIE = "NETKERNELSESSION";
    public static final String PURLZ_REST_LOGIN = "/admin/login/login-submit.bsh";
    public static final String PURLZ_WELCOME_MSG = "Welcome to your PURL administrator interface";
    public static final String PURLZ_USER = "admin";
    public static final String PURLZ_PASSWORD = "password";
    public static final String PURLZ_TESTDOMAIN_ID = "/localhost";
    public static final String PURLZ_TESTDOMAIN_NAME = "Local Domain";
    public static final String PURLZ_TRANSPORT_FILE = "/modules/mod-fulcrum-frontend/etc/TransportJettyConfig.xml";
        
	private List<URI> identifiers = null;
	private List<IdentifierValues> identifierValues = null;
	private ServiceContainer webAppContainer = null;
	private ServiceContainer gridSvcContainer = null;
	public Process purlzProcess = null;
	private File purlzDirectory = null;
	private Cookie purlzLoginCookie = null;
	private Integer purlzPort = null;
	
	private GlobusCredential sysAdminUser = null;
	private GlobusCredential userA = null;
	private GlobusCredential userB = null;
	private GlobusCredential userC = null;
	
	public IdentifiersTestInfo() throws IOException {
		this.purlzDirectory = genPurlzTempDirectory();
	}
	
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
	
	public ServiceContainer createGridSvcContainer(boolean isSecure) throws IOException {
		if (isSecure) {
			gridSvcContainer = createSecureContainer();
		} else {
			gridSvcContainer = createContainer();
		}
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
		String prefix = "http://localhost:" +
			this.purlzPort + 
			PURLZ_TESTDOMAIN_ID +
			"/";
		
		return prefix;
	}
	
	public URI getSystemIdentifier() throws MalformedURIException {
		return new URI(getNAPrefix() + SecurityUtil.LOCAL_SYSTEM_IDENTIFIER); 
	}

	public String getNamingAuthorityURI() throws MalformedURIException {
		URI baseURI = this.webAppContainer.getContainerBaseURI();
		return "http://" + baseURI.getHost() + ":" + baseURI.getPort() + WEBAPP_URL_PATH;
	}
	
	public String getGridSvcURL() throws MalformedURIException {
		return this.gridSvcContainer.getContainerBaseURI().toString() + GRIDSVC_URL_PATH;
	}
	
	public File getPurlzDirectory() {
		return this.purlzDirectory;
	}
	
	public void setPurlzPort(Integer port) {
		this.purlzPort = port;
	}
	
	public Integer getPurlzPort() {
		return this.purlzPort;
	}
	
	public void setPurlzLoginCookie( Cookie cookie ) {
		this.purlzLoginCookie = cookie;
	}
	
	public Cookie getPurlzLoginCookie() {
		return this.purlzLoginCookie;
	}
	
	public static String getResponseString( HttpResponse response ) throws IOException {

		StringBuffer responseStr = new StringBuffer();

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader( entity.getContent() ));
			try {
				String line;
				while ( (line = reader.readLine()) != null ) {
					responseStr.append(line).append("\n");
				}
			} finally {
				reader.close();
			}
		}

		return responseStr.toString();
	}
	
	public String getGridCertsPath() {
		if (this.gridSvcContainer instanceof TomcatSecureServiceContainer) {
			return ((TomcatSecureServiceContainer)this.gridSvcContainer)
				.getCertificatesDirectory().getAbsolutePath();
		}
		
		return null;
	}
	
	public void setSysAdminUser(GlobusCredential cred) {
		this.sysAdminUser = cred;	
	}
	
	public GlobusCredential getSysAdminUser() {
		return this.sysAdminUser;
	}
	
	public void setUserA(GlobusCredential cred) {
		this.userA = cred;	
	}
	
	public GlobusCredential getUserA() {
		return this.userA;
	}
	
	public void setUserB(GlobusCredential cred) {
		this.userB = cred;	
	}
	
	public GlobusCredential getUserB() {
		return this.userB;
	}
	
	public void setUserC(GlobusCredential cred) {
		this.userC = cred;	
	}
	
	public GlobusCredential getUserC() {
		return this.userC;
	}
	//
	// Private Stuff
	//
	private ServiceContainer createContainer() throws IOException {
		return ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER);
	}
	
	private ServiceContainer createSecureContainer() throws IOException {
		return ServiceContainerFactory.createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER);
	}
	
	private static File genPurlzTempDirectory() throws IOException {
        File tempDir = new File(TestingConstants.TEST_TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File tempContainerDir = File.createTempFile("PURLZ", "tmp", tempDir);
        // create a directory, not a file
        tempContainerDir.delete();
        tempContainerDir.mkdirs();
        return tempContainerDir;
    }


}
