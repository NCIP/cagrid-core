package gov.nih.nci.cagrid.introduce;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Constants used in introduce
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin </A>
 */
public abstract class IntroduceConstants {

	// container properties
	public static final String GLOBUS = "GLOBUS_LOCATION";

	public static final String TOMCAT = "CATALINA_HOME";

	public static final String JBOSS = "JBOSS_HOME";

	// introduce specific constants
	
	public static final String INTRODUCE_CONFIGURATION_FILE = "introduce.grape.configuration.file";
	
	public static final String INTRODUCE_VERSION_PROPERTY = "introduce.version";

	public static final String INTRODUCE_PATCH_VERSION_PROPERTY = "introduce.patch.version";

	public static final String INTRODUCE_PROPERTIES = "conf/introduce.properties";

	public static final String GLOBUS_LOCATION = "Globus Location";

	public static final String SERVICE_SECURITY_METADATA_METHOD = "getServiceSecurityMetadata";

	// service skeleton properties
	public static final String INTRODUCE_PROPERTIES_FILE = "introduce.properties";

	public static final String INTRODUCE_SKELETON_DESTINATION_DIR = "introduce.skeleton.destination.dir";

	public static final String INTRODUCE_SKELETON_SERVICE_NAME = "introduce.skeleton.service.name";

	public static final String INTRODUCE_SKELETON_EXTENSIONS = "introduce.skeleton.extensions";

	public static final String INTRODUCE_SKELETON_RESOURCE_OPTIONS = "introduce.skeleton.resource.options";

	public static final String NAMESPACE2PACKAGE_MAPPINGS_FILE = "namespace2package.mappings";

	public static final String INTRODUCE_SKELETON_TIMESTAMP = "introduce.skeleton.timestamp";

	public static final String INTRODUCE_SKELETON_SERVICES_LIST = "introduce.skeleton.services.list";

	public static final String INTRODUCE_SKELETON_NAMESPACE_DOMAIN = "introduce.skeleton.namespace.domain";

	public static final String INTRODUCE_SKELETON_PACKAGE = "introduce.skeleton.package";

	public static final String INTRODUCE_SERVICE_PROPERTIES = "service.properties";

	public static final String INTRODUCE_NS_EXCLUDES = "introduce.ns.excludes";

	public static final String INTRODUCE_SB_EXCLUDES = "introduce.soap.binding.excludes";

	public static final QName INTRODUCE_SKELETON_QNAME = new QName(
			"gme://gov.nih.nci.cagrid/1/Introduce", "ServiceDescription");

	// resource options
	public static final String INTRODUCE_SINGLETON_RESOURCE = "singleton";

	public static final String INTRODUCE_MAIN_RESOURCE = "main";

	public static final String INTRODUCE_LIFETIME_RESOURCE = "lifetime";

	public static final String INTRODUCE_PERSISTENT_RESOURCE = "persistent";

	public static final String INTRODUCE_NOTIFICATION_RESOURCE = "notification";

	public static final String INTRODUCE_RESOURCEPROPETIES_RESOURCE = "resourcePropertyManagement";

	public static final String INTRODUCE_CUSTOM_RESOURCE = "custom";

	public static final String INTRODUCE_IDENTIFIABLE_RESOURCE = "identifiable";

	public static final String INTRODUCE_SECURE_RESOURCE = "secure";

	// deployment properties
	public static final String DEPLOY_PROPERTIES_FILE = "deploy.properties";

	public static final String INTRODUCE_XML_FILE = "introduce.xml";

	public static final String INTRODUCE_XML_XSD_FILE = "ServiceDescription.xsd";
	
	public static final String INTRODUCE_DEPLOYMENT_PERFORM_REGISTRATION_PROPERTY = "perform.index.service.registration";
	public static final String INTRODUCE_DEPLOYMENT_INDEX_REFRESH_PROPERTY = "index.service.index.refresh_milliseconds";
	public static final String INTRODUCE_DEPLOYMENT_INDEX_SERVICE_URL_PROPERTY = "index.service.url";
	public static final String INTRODUCE_DEPLOYMENT_PREFIX_PROPERTY = "service.deployment.prefix";
	public static final String INTRODUCE_DEPLOYMENT_REFRESH_REGISTRATION_PROPERTY = "index.service.registration.refresh_seconds";
    
    // deployment task properties
    public static final String WEBAPP_DEPLOY_LOCATION = "webapp.deploy.dir";
    public static final String WEBAPP_DEPLOY_LIB_LOCATION = "webapp.deploy.lib.dir";
    public static final String WEBAPP_DEPLOY_SCHEMA_LOCATION = "webapp.deploy.schema.dir";
    public static final String WEBAPP_DEPLOY_ETC_LOCATION = "webapp.deploy.etc.dir";
    public static final String SERVICE_DEPLOYMENT_DIR_NAME = "service.deployment.dir.name";
    public static final String SERVICE_DEPLOYMENT_PREFIX = "service.deployment.prefix";
    public static final String SERVICE_DEPLOYMENT_NAME = "service.name";

	// w3c namespaces
	public static final String W3CNAMESPACE = "http://www.w3.org/2001/XMLSchema";

	public static final String WSDLAMESPACE = "http://schemas.xmlsoap.org/wsdl/";

	public static final String BASEFAULTS_NAMESPACE = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-BaseFaults-1.2-draft-01.xsd";

	public static final String W3CNAMESPACE_PREFIX = "xs";

	public static final String WSADDRESING_NAMESPACE = "http://schemas.xmlsoap.org/ws/2004/03/addressing";

	public static final String WSADDRESING_EPR_TYPE = "EndpointReference";

	public static final String WSADDRESSING_EPR_CLASSNAME = "org.apache.axis.message.addressing.EndpointReferenceType";

	public static final String WSADDRESING_LOCATION = ".." + File.separator
			+ "ws" + File.separator + "addressing" + File.separator
			+ "WS-Addressing.xsd";

	public static final List GLOBUS_NAMESPACES = new ArrayList(
			Arrays
					.asList(new String[] {
							WSADDRESING_NAMESPACE,
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-BaseFaults-1.2-draft-01.xsd",
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-BaseFaults-1.2-draft-01.wsdl",
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd",
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.wsdl",
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd",
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.wsdl",
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ServiceGroup-1.2-draft-01.xsd",
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ServiceGroup-1.2-draft-01.wsdl",
							"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
							"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.wsdl",
							"http://schemas.xmlsoap.org/ws/2004/04/trust",
							"http://schemas.xmlsoap.org/ws/2002/12/policy",
							"http://schemas.xmlsoap.org/ws/2002/07/utility",
							"http://schemas.xmlsoap.org/ws/2004/04/sc",
							"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
							"http://www.w3.org/2000/09/xmldsig#",
							"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" }));

	private IntroduceConstants() {
		// prevents instantiation
	}
}
