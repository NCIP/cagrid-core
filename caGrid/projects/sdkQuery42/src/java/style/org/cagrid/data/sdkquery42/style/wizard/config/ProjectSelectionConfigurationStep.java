package org.cagrid.data.sdkquery42.style.wizard.config;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
        // TODO Auto-generated method stub

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
