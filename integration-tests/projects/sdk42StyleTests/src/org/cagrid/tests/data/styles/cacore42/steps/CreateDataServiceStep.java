package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.data.extension.ServiceStyle;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.metadata.xmi.XmiFileType;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.sdkquery42.style.wizard.config.AbstractStyleConfigurationStep;
import org.cagrid.data.sdkquery42.style.wizard.config.DomainModelConfigurationStep;
import org.cagrid.data.sdkquery42.style.wizard.config.ProjectSelectionConfigurationStep;
import org.cagrid.data.sdkquery42.style.wizard.config.SchemaMappingConfigStep;
import org.cagrid.data.sdkquery42.style.wizard.config.SecurityConfigurationStep;
import org.cagrid.data.sdkquery42.style.wizard.config.DomainModelConfigurationStep.DomainModelConfigurationSource;
import org.cagrid.data.test.creation.CreationStep;
import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.tests.data.styles.cacore42.ExampleProjectInfo;
import org.cagrid.tests.data.styles.cacore42.SDK42ServiceStyleSystemTestConstants;

public class CreateDataServiceStep extends CreationStep {
    
    public static final String OUTPUT_PACKAGE_PATH = "target" + File.separator + "dist" 
        + File.separator + "exploded" + File.separator + "output" 
        + File.separator + "example" + File.separator + "package";
    public static final String REMOTE_CLIENT_PATH = OUTPUT_PACKAGE_PATH + File.separator + "remote-client";
    public static final String LOCAL_CLIENT_PATH = OUTPUT_PACKAGE_PATH + File.separator + "local-client";

    private static Log LOG = LogFactory.getLog(CreateDataServiceStep.class);
    
    private ServiceInformation serviceInformation = null;
    private ServiceContainer remoteSdkApplicationContainer = null;

    public CreateDataServiceStep(DataTestCaseInfo testInfo, String introduceDir) {
        this(testInfo, introduceDir, null);
    }
    
    
    public CreateDataServiceStep(DataTestCaseInfo testInfo, String introduceDir, ServiceContainer sdkContainer) {
        super(testInfo, introduceDir);
        this.remoteSdkApplicationContainer = sdkContainer;
    }
    
    
    /**
     * Extended to turn on and configure the caCORE SDK 4.2 style in the service model
     */
    protected void postSkeletonCreation() throws Throwable {
        setServiceStyle();
        configureStyle();
        persistModelChanges();
    }
    
    
    private void setServiceStyle() throws Throwable {
        Data extensionData = getExtensionData();
        ServiceFeatures features = extensionData.getServiceFeatures();
        if (features == null) {
            features = new ServiceFeatures();
            extensionData.setServiceFeatures(features);
        }
        features.setServiceStyle(new ServiceStyle(
            SDK42ServiceStyleSystemTestConstants.STYLE_NAME, SDK42ServiceStyleSystemTestConstants.STYLE_VERSION));
        storeExtensionData(extensionData);
    }
    
    
    private void configureStyle() throws Throwable {
        getProjectSelectionConfiguration().applyConfiguration();
        getSecurityConfiguration().applyConfiguration();
        getDomainModelConfiguration().applyConfiguration();
        getSchemaMappingConfiguration().applyConfiguration();
    }
    
    
    private void persistModelChanges() throws Throwable {
        // persist the changes made by the configuration steps
        File serviceModelFile = new File(getServiceInformation().getBaseDirectory(), IntroduceConstants.INTRODUCE_XML_FILE);
        LOG.debug("Persisting changes to service model (" + serviceModelFile.getAbsolutePath() + ")");
        FileWriter writer = new FileWriter(serviceModelFile);
        Utils.serializeObject(getServiceInformation().getServiceDescriptor(), IntroduceConstants.INTRODUCE_SKELETON_QNAME, writer);
        writer.flush();
        writer.close();
    }
    
    
    private AbstractStyleConfigurationStep getProjectSelectionConfiguration() throws Exception {
        ProjectSelectionConfigurationStep config = new ProjectSelectionConfigurationStep(getServiceInformation());
        config.setApplicationName("example");
        // if an SDK application container is available, use the remote API, else local
        if (remoteSdkApplicationContainer == null) {
            config.setLocalApi(true);
            config.setLocalClientDir(getExampleProjectLocalClientDir().getAbsolutePath());
        } else {
            config.setLocalApi(false);
            config.setRemoteClientDir(getExampleProjectRemoteClientDir().getAbsolutePath());
            config.setApplicationHostname(
                remoteSdkApplicationContainer.getContainerBaseURI().getHost());
            config.setApplicationPort(
                Integer.valueOf(remoteSdkApplicationContainer.getContainerBaseURI().getPort()));
        }
        return config;
    }
    
    
    private AbstractStyleConfigurationStep getSecurityConfiguration() throws Exception {
        SecurityConfigurationStep config = new SecurityConfigurationStep(getServiceInformation());
        // TODO: make this configurable to turn on CSM authz
        config.setUseCsmGridIdent(false);
        config.setUseStaticLogin(false);
        return config;
    }
    
    
    private AbstractStyleConfigurationStep getDomainModelConfiguration() throws Exception {
        DomainModelConfigurationStep config = new DomainModelConfigurationStep(getServiceInformation());
        config.setModelSource(DomainModelConfigurationSource.XMI);
        config.setXmiType(XmiFileType.SDK_40_EA);
        config.setXmiFile(getDomainModelXmiFile());
        config.setProjectShortName(ExampleProjectInfo.EXAMPLE_PROJECT_NAME);
        config.setProjectVersion(ExampleProjectInfo.EXAMPLE_PROJECT_VERSION);
        return config;
    }
    
    
    private AbstractStyleConfigurationStep getSchemaMappingConfiguration() throws Exception {
        SchemaMappingConfigStep config = new SchemaMappingConfigStep(getServiceInformation());
        config.mapFromSdkGeneratedSchemas();
        return config;
    }
    
    
    private static File getDomainModelXmiFile() {
        File modelFile = new File(ExampleProjectInfo.getExampleProjectDir(), "models" + File.separator + "sdk.xmi");
        assertTrue("Domain model XMI file " + modelFile.getAbsolutePath() + " not found!", modelFile.exists());
        return modelFile;
    }
    
    
    private Data getExtensionData() throws Throwable {
        ServiceDescription serviceDesc = getServiceInformation().getServiceDescriptor();
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        assertNotNull("Data service extension was not found in the service model", dataExtension);
        Data extensionData = ExtensionDataUtils.getExtensionData(dataExtension.getExtensionData());
        return extensionData;
    }
    
    
    private void storeExtensionData(Data data) throws Throwable {
        File serviceModelFile = new File(serviceInfo.getDir() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE);
        ServiceDescription serviceDesc = getServiceInformation().getServiceDescriptor();
        
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        assertNotNull("Data service extension was not found in the service model", dataExtension);
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        ExtensionDataUtils.storeExtensionData(dataExtension.getExtensionData(), data);
        Utils.serializeDocument(serviceModelFile.getAbsolutePath(), serviceDesc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
    
    
    private ServiceInformation getServiceInformation() throws Exception {
        if (serviceInformation == null) {
            serviceInformation = new ServiceInformation(new File(serviceInfo.getDir()));
        }
        return serviceInformation;
    }
    
    
    private File getExampleProjectRemoteClientDir() {
        return new File(ExampleProjectInfo.getExampleProjectDir(), REMOTE_CLIENT_PATH);
    }
    
    
    private File getExampleProjectLocalClientDir() {
        return new File(ExampleProjectInfo.getExampleProjectDir(), LOCAL_CLIENT_PATH);
    }
}
