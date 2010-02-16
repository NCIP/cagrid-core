package org.cagrid.data.sdkquery42.style.wizard.config;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.sdkquery42.processor.SDK42QueryProcessor;
import org.cagrid.data.sdkquery42.processor2.SDK42CQL2QueryProcessor;
import org.cagrid.grape.utils.CompositeErrorDialog;

/**
 * ProjectSelectionConfigurationStep
 * Configures basic aspects of the service such as application name, local / remote
 * API and directories, and service URL
 * 
 * @author David
 */
public class ProjectSelectionConfigurationStep extends AbstractStyleConfigurationStep {
    
    public static final String[] excludeSdkLibs = {
        "axis-1.4.jar", "caGrid-BulkDataHandler-client-1.3.jar", "caGrid-BulkDataHandler-common-1.3.jar",
        "caGrid-BulkDataHandler-stubs-1.3.jar", "caGrid-CQL-cql.1.0-1.3.jar", "caGrid-ServiceSecurityProvider-client-1.3.jar",
        "caGrid-ServiceSecurityProvider-common-1.3.jar", "caGrid-ServiceSecurityProvider-service-1.3.jar", 
        "caGrid-ServiceSecurityProvider-stubs-1.3.jar", "caGrid-core-1.3.jar", "caGrid-data-common-1.3.jar",
        "caGrid-data-cql-1.3.jar", "caGrid-data-service-1.3.jar", "caGrid-data-stubs-1.3.jar", "caGrid-data-utils-1.3.jar",
        "caGrid-data-validation-1.3.jar", "caGrid-metadata-common-1.3.jar", "caGrid-metadata-data-1.3.jar",
        "caGrid-metadata-security-1.3.jar", "caGrid-metadatautils-1.3.jar", "caGrid-wsEnum-1.3.jar", "caGrid-wsEnum-stubs-1.3.jar",
        "cog-jglobus-1.2.jar"
    };
    
    private static Log LOG = LogFactory.getLog(ProjectSelectionConfigurationStep.class);
    
    private String applicationName = null;
    private boolean isLocalApi = false;
    private String localClientDir = null;
    private String remoteClientDir = null;
    private String applicationHostname = null;
    private Integer applicationPort = null;
    private boolean useHttps = false;

    public ProjectSelectionConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }


    public void applyConfiguration() throws Exception {
        // set the query processor class name for the data service
        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
            QueryProcessorConstants.QUERY_PROCESSOR_CLASS_PROPERTY, SDK42QueryProcessor.class.getName(), false);
        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
            QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY, SDK42CQL2QueryProcessor.class.getName(), false);
        
        // set service properties required by the query processor
        setCql1ProcessorProperty(SDK42QueryProcessor.PROPERTY_APPLICATION_NAME, getApplicationName(), false);
        setCql1ProcessorProperty(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API, String.valueOf(isLocalApi()), false);
        setCql1ProcessorProperty(SDK42QueryProcessor.PROPERTY_HOST_NAME, 
            getApplicationHostname() != null ? getApplicationHostname() : "", false);
        setCql1ProcessorProperty(SDK42QueryProcessor.PROPERTY_HOST_PORT, 
            getApplicationPort() != null ? String.valueOf(getApplicationPort()) : "", false);
        setCql1ProcessorProperty(SDK42QueryProcessor.PROPERTY_HOST_HTTPS, String.valueOf(isUseHttps()), false);
        
        // set service properties required by the CQL 2 query processor
        setCql2ProcessorProperty(SDK42CQL2QueryProcessor.PROPERTY_APPLICATION_NAME, getApplicationName(), false);
        setCql2ProcessorProperty(SDK42CQL2QueryProcessor.PROPERTY_USE_LOCAL_API, String.valueOf(isLocalApi()), false);
        setCql2ProcessorProperty(SDK42CQL2QueryProcessor.PROPERTY_HOST_NAME, 
            getApplicationHostname() != null ? getApplicationHostname() : "", false);
        setCql2ProcessorProperty(SDK42CQL2QueryProcessor.PROPERTY_HOST_PORT, 
            getApplicationPort() != null ? String.valueOf(getApplicationPort()) : "", false);
        setCql2ProcessorProperty(SDK42CQL2QueryProcessor.PROPERTY_HOST_HTTPS, String.valueOf(isUseHttps()), false);
        
        // store the information about the local and remote client dirs
        setStyleProperty(StyleProperties.SDK_REMOTE_CLIENT_DIR, getRemoteClientDir() != null ? getRemoteClientDir() : "");
        setStyleProperty(StyleProperties.SDK_LOCAL_CLIENT_DIR, getLocalClientDir() != null ? getLocalClientDir() : "");
        
        // roll up the local or remote configs as a jar file
        File sdkConfigDir = null;
        File sdkLibDir = null;
        if (isLocalApi()) {
            sdkConfigDir = new File(getLocalClientDir(), "conf");
            sdkLibDir = new File(getLocalClientDir(), "lib");
        } else {
            sdkConfigDir = new File(getRemoteClientDir(), "conf");
            sdkLibDir = new File(getRemoteClientDir(), "lib");
        }
        File serviceLibDir = new File(getServiceInformation().getBaseDirectory(), "lib");
        File configJarFile = new File(serviceLibDir, getApplicationName() + "-config.jar");
        JarUtilities.jarDirectory(sdkConfigDir, configJarFile);
        LOG.debug("Packaged " + sdkConfigDir.getAbsolutePath() + " as " + configJarFile.getAbsolutePath());
        
        // grab the castor marshaling and unmarshaling xml mapping files
        // from the schemas jar and copy them into the service's package structure
        try {
            File schemasJar = new File(sdkLibDir, getApplicationName() + "-schema.jar");
            StringBuffer marshaling = JarUtilities.getFileContents(
                new JarFile(schemasJar), CastorMappingUtil.CASTOR_MARSHALLING_MAPPING_FILE);
            StringBuffer unmarshalling = JarUtilities.getFileContents(
                new JarFile(schemasJar), CastorMappingUtil.CASTOR_UNMARSHALLING_MAPPING_FILE);
            // copy the mapping files to the service's source dir + base package name
            String marshallOut = CastorMappingUtil.getMarshallingCastorMappingFileName(getServiceInformation());
            String unmarshallOut = CastorMappingUtil.getUnmarshallingCastorMappingFileName(getServiceInformation());
            Utils.stringBufferToFile(marshaling, marshallOut);
            Utils.stringBufferToFile(unmarshalling, unmarshallOut);
            LOG.debug("Extracted castor mapping files into service package structure");
        } catch (IOException ex) {
            String message = "Error extracting castor mapping files";
            LOG.error(message, ex);
            CompositeErrorDialog.showErrorDialog(message, ex.getMessage(), ex);
        }
        
        // copy SDK libs to the service
        // have to be selective here since there's LOTS of conflicts with things
        // cagrid and globus already provide and depend on
        Set<String> excludeNames = new HashSet<String>();
        Collections.addAll(excludeNames, excludeSdkLibs);
        File[] sdkLibs = sdkLibDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".jar");
            }
        });
        for (File sdkLib : sdkLibs) {
            if (!excludeNames.contains(sdkLib.getName())) {
                File serviceLib = new File(serviceLibDir, sdkLib.getName());
                Utils.copyFile(sdkLib, serviceLib);
                LOG.debug("Copied SDK library " + sdkLib.getName() + " into service");
            } else {
                LOG.debug("SDK library " + sdkLib.getName() + " excluded from service");
            }
        }
    }
    
    
    public boolean isLocalClientDirValid() {
        boolean valid = false;
        File dir = new File(getLocalClientDir());
        if (dir.exists() && dir.isDirectory()) {
            File confDir = new File(dir, "conf");
            File libDir = new File(dir, "lib");
            boolean confValid = false;
            boolean libValid = false;
            if (confDir.exists() && confDir.isDirectory()) {
                File[] confFiles = confDir.listFiles();
                Set<String> filesMustExist = new HashSet<String>();
                Collections.addAll(filesMustExist, SdkProjectExpectedFiles.getExpectedLocalClientConfFiles());
                for (File f : confFiles) {
                    filesMustExist.remove(f.getName());
                }
                confValid = filesMustExist.size() == 0;
            }
            if (libDir.exists() && libDir.isDirectory()) {
                File[] libFiles = libDir.listFiles();
                Set<String> filesMustExist = new HashSet<String>();
                Collections.addAll(filesMustExist, SdkProjectExpectedFiles.getExpectedLocalClientLibFiles(getApplicationName()));
                for (File f : libFiles) {
                    filesMustExist.remove(f.getName());
                }
                libValid = filesMustExist.size() == 0;
            }
            valid = confValid && libValid;
        }
        return valid;
    }
    
    
    public boolean isRemoteClientDirValid() {
        boolean valid = false;
        File dir = new File(getRemoteClientDir());
        if (dir.exists() && dir.isDirectory()) {
            File confDir = new File(dir, "conf");
            File libDir = new File(dir, "lib");
            boolean confValid = false;
            boolean libValid = false;
            if (confDir.exists() && confDir.isDirectory()) {
                File[] confFiles = confDir.listFiles();
                Set<String> filesMustExist = new HashSet<String>();
                Collections.addAll(filesMustExist, SdkProjectExpectedFiles.getExpectedRemoteClientConfFiles());
                for (File f : confFiles) {
                    filesMustExist.remove(f.getName());
                }
                confValid = filesMustExist.size() == 0;
            }
            if (libDir.exists() && libDir.isDirectory()) {
                File[] libFiles = libDir.listFiles();
                Set<String> filesMustExist = new HashSet<String>();
                Collections.addAll(filesMustExist, SdkProjectExpectedFiles.getExpectedRemoteClientLibFiles(getApplicationName()));
                for (File f : libFiles) {
                    filesMustExist.remove(f.getName());
                }
                libValid = filesMustExist.size() == 0;
            }
            valid = confValid && libValid;
        }
        return valid;
    }


    public String getApplicationName() {
        return applicationName;
    }


    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }


    public boolean isLocalApi() {
        return isLocalApi;
    }


    public void setLocalApi(boolean isLocalApi) {
        this.isLocalApi = isLocalApi;
    }


    public String getLocalClientDir() {
        return localClientDir;
    }


    public void setLocalClientDir(String localClientDir) {
        this.localClientDir = localClientDir;
    }


    public String getRemoteClientDir() {
        return remoteClientDir;
    }


    public void setRemoteClientDir(String remoteClientDir) {
        this.remoteClientDir = remoteClientDir;
    }


    public String getApplicationHostname() {
        return applicationHostname;
    }


    public void setApplicationHostname(String applicationHostname) {
        this.applicationHostname = applicationHostname;
    }


    public Integer getApplicationPort() {
        return applicationPort;
    }


    public void setApplicationPort(Integer applicationPort) {
        this.applicationPort = applicationPort;
    }


    public boolean isUseHttps() {
        return useHttps;
    }


    public void setUseHttps(boolean useHttps) {
        this.useHttps = useHttps;
    }
}
