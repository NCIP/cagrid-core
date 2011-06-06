package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionsType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.property.ServiceProperties;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class ServiceInformation {

	private ServiceDescription introService;

	private Properties introduceServiceProperties;

	private Properties deploymentProperties;

	private File baseDirectory;

	private File introduceXMLFile;

	public ServiceInformation(File baseDirectory) throws Exception {
		setBaseDirectory(baseDirectory);
		load();

	}

	public ServiceInformation(ServiceDescription service, Properties properties, File baseDirectory) {
		this.introService = service;
		this.introduceServiceProperties = properties;
		setBaseDirectory(baseDirectory);
	}

	private void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
		introduceXMLFile = new File(baseDirectory, IntroduceConstants.INTRODUCE_XML_FILE);
	}

	public void load() throws Exception {
		if (!introduceXMLFile.exists() || !introduceXMLFile.canRead()) {
			throw new Exception("Unable to read the Introduce document:" + introduceXMLFile.getAbsolutePath());
		}

		try {
			SchemaValidator.validate(getIntroduceXSD(), introduceXMLFile);
		} catch (SchemaValidationException e) {
			throw new SchemaValidationException("The Introduce XML document does not adhere to the schema:\n" + e.getMessage(), e);
		}

		introService = Utils.deserializeDocument(introduceXMLFile.getAbsolutePath(), ServiceDescription.class);

		File servicePropertiesFile = new File(baseDirectory, IntroduceConstants.INTRODUCE_PROPERTIES_FILE);
		introduceServiceProperties = loadProperties(servicePropertiesFile);
		File deployPropertiesFile = new File(baseDirectory, IntroduceConstants.DEPLOY_PROPERTIES_FILE);
		deploymentProperties = loadProperties(deployPropertiesFile);
	}

	public Properties getIntroduceServiceProperties() {
		return introduceServiceProperties;
	}

	public void setIntroduceServiceProperties(Properties serviceProperties) {
		this.introduceServiceProperties = serviceProperties;
	}

	public Properties getDeploymentProperties() {
		return deploymentProperties;
	}

	public void setDeplymentProperties(Properties deploymentProperties) {
		this.deploymentProperties = deploymentProperties;
	}

	public ServicesType getServices() {
		return introService.getServices();
	}

	public void setServices(ServicesType services) {
		this.introService.setServices(services);
	}

	public NamespacesType getNamespaces() {
		return introService.getNamespaces();
	}

	public void setNamespaces(NamespacesType namespaces) {
		this.introService.setNamespaces(namespaces);
	}

	public ExtensionsType getExtensions() {
		return introService.getExtensions();
	}

	public void setExtensions(ExtensionsType extensions) {
		this.introService.setExtensions(extensions);
	}

	public ServiceProperties getServiceProperties() {
		return this.introService.getServiceProperties();
	}

	public void setServiceProperties(ServiceProperties serviceProperties) {
		this.introService.setServiceProperties(serviceProperties);
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getIntroduceXml() {
		return introduceXMLFile;
	}

	public ServiceDescription getServiceDescriptor() {
		return introService;
	}

	public void persistInformation() throws Exception {
		Utils.serializeDocument(introduceXMLFile.getAbsolutePath(), introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
		File servicePropertiesFile = new File(baseDirectory, IntroduceConstants.INTRODUCE_PROPERTIES_FILE);
		storeProperties(introduceServiceProperties, servicePropertiesFile, "Introduce Properties");
		File deploymentPropertiesFile = new File(baseDirectory.getAbsolutePath(), IntroduceConstants.DEPLOY_PROPERTIES_FILE);
		storeProperties(deploymentProperties, deploymentPropertiesFile, "Service Deployment Properties");
	}

	public void createArchive() throws Exception {
		// create the archive
		load();
		long id = System.currentTimeMillis();

		getIntroduceServiceProperties().setProperty(IntroduceConstants.INTRODUCE_SKELETON_TIMESTAMP, String.valueOf(id));
		storeProperties(getIntroduceServiceProperties(), new File(getBaseDirectory(), IntroduceConstants.INTRODUCE_PROPERTIES_FILE), "Introduce Properties");

		ResourceManager.createArchive(String.valueOf(id), getIntroduceServiceProperties().getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME),
				getBaseDirectory().getAbsolutePath());
	}

	/**
	 * TODO: requires running directory to be introduce's directory... need a
	 * better way
	 */
	private String getIntroduceXSD() {
		return new File("schema", IntroduceConstants.INTRODUCE_XML_XSD_FILE).getAbsolutePath();
	}

	private Properties loadProperties(File propsFile) throws IOException {
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(propsFile);
		props.load(fis);
		fis.close();
		return props;
	}

	private void storeProperties(Properties props, File propsFile, String comments) throws IOException {
		FileOutputStream fos = new FileOutputStream(propsFile);
		props.store(fos, comments);
		fos.close();
	}
}
