package gov.nih.nci.cagrid.sdkinstall;

import gov.nih.nci.cagrid.sdkinstall.description.InstallationDescription;

import java.io.File;

/** 
 *  DeployPropertiesManager
 *  Manages the deploy.properties file
 * 
 * @author David Ervin
 * 
 * @created Jun 13, 2007 2:13:14 PM
 * @version $Id: DeployPropertiesManager.java,v 1.2 2007-06-15 16:57:33 dervin Exp $ 
 */
public abstract class DeployPropertiesManager {
    private InstallationDescription description;
    private File sdkDirectory;
    
    public DeployPropertiesManager(InstallationDescription description, File sdkDir) {
        this.description = description;
        this.sdkDirectory = sdkDir;
    }
    
    
    protected InstallationDescription getInstallationDescription() {
        return this.description;
    }
    
    
    protected File getSdkDirectory() {
        return this.sdkDirectory;
    }
    

    /**
     * Subclasses should override this method to perform any modifications
     * which may be required on the SDK's configuration before
     * building
     */
    public abstract void configureDeployment() throws DeploymentConfigurationException;
}
