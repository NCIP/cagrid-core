package org.cagrid.data.sdkquery42.style.wizard.config;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.cagrid.data.sdkquery42.processor.SDK42QueryProcessor;
import org.cagrid.grape.utils.CompositeErrorDialog;

/**
 * ProjectSelectionConfigurationStep
 * Configures basic aspects of the service such as application name, local / remote
 * API and directories, and service URL
 * 
 * @author David
 */
public class ProjectSelectionConfigurationStep extends AbstractStyleConfigurationStep {
    
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
        // set service properties required by the query processor
        setServiceProperty(SDK42QueryProcessor.PROPERTY_APPLICATION_NAME, getApplicationName(), false);
        setServiceProperty(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API, String.valueOf(isLocalApi()), false);
        setServiceProperty(SDK42QueryProcessor.PROPERTY_HOST_NAME, getApplicationHostname(), false);
        setServiceProperty(SDK42QueryProcessor.PROPERTY_HOST_PORT, 
            getApplicationPort() != null ? String.valueOf(getApplicationPort()) : "", false);
        setServiceProperty(SDK42QueryProcessor.PROPERTY_HOST_HTTPS, String.valueOf(isUseHttps()), false);
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
        } catch (IOException ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error extracting castor mapping files", ex.getMessage(), ex);
        }
        
        // copy SDK libs to the service
        Utils.copyDirectory(sdkLibDir, serviceLibDir);
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
