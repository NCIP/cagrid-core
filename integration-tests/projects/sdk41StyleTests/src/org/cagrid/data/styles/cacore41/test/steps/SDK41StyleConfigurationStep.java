package org.cagrid.data.styles.cacore41.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.sdkquery41.processor.SDK41QueryProcessor;
import org.cagrid.data.sdkquery41.style.wizard.config.APITypeConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.AbstractStyleConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.DomainModelConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.GeneralConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.LoginConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.SDK41InitialConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.SchemaMappingConfigStep;
import org.cagrid.data.sdkquery41.style.wizard.config.APITypeConfigurationStep.ApiType;
import org.cagrid.data.sdkquery41.style.wizard.config.DomainModelConfigurationStep.DomainModelConfigurationSource;

/** 
 *  SDK41StyleConfigurationStep
 *  Step to apply configuration to the 
 *  SDK 4.1 Data Service Style 
 * 
 * @author David Ervin
 * 
 * @created Jan 28, 2008 11:24:21 AM
 * @version $Id: SDK41StyleConfigurationStep.java,v 1.2 2009-01-08 22:11:19 dervin Exp $ 
 */
public class SDK41StyleConfigurationStep extends Step {
    public static final String SDK_41_TESTS_BASE_DIR_PROPERTY = "sdk41.tests.base.dir";    
    
    public static final String PROPERTY_SDK_UNPACK_DIR = "sdk.unpack.dir";
    public static final String PROPERTY_REMOTE_HOST_NAME = "remote.sdk.host.name";
    public static final String PROPERTY_REMOTE_HOST_PORT = "remote.sdk.host.port";
    
    public static final String DOMAIN_MODEL_FILE = File.separator + "resources" + File.separator + "example41_domainModel.xml";
    
    private static Log LOG = LogFactory.getLog(SDK41StyleConfigurationStep.class);
    
    private File serviceBaseDirectory = null;
    private ServiceInformation serviceInformation = null;

    public SDK41StyleConfigurationStep(File serviceBaseDirectory) {
        this.serviceBaseDirectory = serviceBaseDirectory;
    }


    public void runStep() throws Throwable {
        // run through the configuration steps
        getInitialConfiguration().applyConfiguration();
        getGeneralConfiguration().applyConfiguration();
        getApiTypeConfiguration().applyConfiguration();
        getLoginConfigurationStep().applyConfiguration();
        getDomainModelConfiguration().applyConfiguration();
        getSchemaMappingConfiguration().applyConfiguration();
        
        // persist the changes made by the configuration steps
        File serviceModelFile = new File(getServiceInformation().getBaseDirectory(), IntroduceConstants.INTRODUCE_XML_FILE);
        LOG.debug("Persisting changes to service model (" + serviceModelFile.getAbsolutePath() + ")");
        FileWriter writer = new FileWriter(serviceModelFile);
        Utils.serializeObject(getServiceInformation().getServiceDescriptor(), IntroduceConstants.INTRODUCE_SKELETON_QNAME, writer);
        writer.flush();
        writer.close();
    }
    
    
    private AbstractStyleConfigurationStep getInitialConfiguration() throws Exception {
        SDK41InitialConfigurationStep configuration = 
            new SDK41InitialConfigurationStep(getServiceInformation());
        configuration.setQueryProcessorClassName(SDK41QueryProcessor.class.getName());
        File styleLibDir = new File(ExtensionsLoader.getInstance().getExtensionsDir().getAbsolutePath(),
            "data" + File.separator + "styles" + File.separator + "cacore41" + File.separator + "lib");
        configuration.setStyleLibDirectory(styleLibDir);
        return configuration;
    }
    
    
    private AbstractStyleConfigurationStep getGeneralConfiguration() throws Exception {
        GeneralConfigurationStep configuration = 
            new GeneralConfigurationStep(getServiceInformation());
        String sdkUnpackDir = System.getProperty(PROPERTY_SDK_UNPACK_DIR);
        assertNotNull(sdkUnpackDir, PROPERTY_SDK_UNPACK_DIR + 
            " system property must point to the directory where the cacore sdk is unpacked");
        File sdkDir = new File(sdkUnpackDir, "SDK4");
        configuration.setSdkDirectory(sdkDir);
        try {
            configuration.validateSdkDirectory();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error validating the SDK directory: " + ex.getMessage());
        }
        return configuration;
    }
    
    
    private AbstractStyleConfigurationStep getApiTypeConfiguration() throws Exception {
        APITypeConfigurationStep configuration = 
            new APITypeConfigurationStep(getServiceInformation());
        configuration.setApiType(ApiType.REMOTE_API);
        String hostname = System.getProperty(PROPERTY_REMOTE_HOST_NAME);
        String port = System.getProperty(PROPERTY_REMOTE_HOST_PORT);
        assertNotNull(hostname, PROPERTY_REMOTE_HOST_NAME + 
            " system property must be the hostname of the remote cacore sdk application");
        assertNotNull(port, PROPERTY_REMOTE_HOST_PORT + 
            " system property must be the port number of the remote cacore sdk application");
        configuration.setHostname(hostname);
        configuration.setPortNumber(Integer.valueOf(port));
        configuration.setUseHttps(Boolean.FALSE);
        return configuration;
    }
    
    
    private AbstractStyleConfigurationStep getLoginConfigurationStep() throws Exception {
        LoginConfigurationStep configuration =
            new LoginConfigurationStep(getServiceInformation());
        configuration.setUseLogin(Boolean.FALSE);
        return configuration;
    }
    
    
    private AbstractStyleConfigurationStep getDomainModelConfiguration() throws Exception {
        DomainModelConfigurationStep configuration = 
            new DomainModelConfigurationStep(getServiceInformation());
        configuration.setDomainModelLocalFile(getDomainModelFile());
        configuration.setModelSource(DomainModelConfigurationSource.FILE_SYSTEM);
        return configuration;
    }
    
    
    private SchemaMappingConfigStep getSchemaMappingConfiguration() throws Exception {
        SchemaMappingConfigStep configuration = 
            new SchemaMappingConfigStep(getServiceInformation());
        // ok to use automagical schema mapping
        try {
            configuration.mapFromSdkGeneratedSchemas();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error automatically mapping schemas from the SDK: " + ex.getMessage());
        }
        return configuration;
    }
    
    
    private static File getDomainModelFile() {
        String basedir = System.getProperty(SDK_41_TESTS_BASE_DIR_PROPERTY);
        assertNotNull("System property " + SDK_41_TESTS_BASE_DIR_PROPERTY + " was not defined!", basedir);
        return new File(basedir, DOMAIN_MODEL_FILE);
    }
    
    
    private ServiceInformation getServiceInformation() throws Exception {
        if (serviceInformation == null) {
            serviceInformation = new ServiceInformation(serviceBaseDirectory);
        }
        return serviceInformation;
    }
}
