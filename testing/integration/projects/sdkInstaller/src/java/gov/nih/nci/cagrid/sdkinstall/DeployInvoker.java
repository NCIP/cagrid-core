package gov.nih.nci.cagrid.sdkinstall;

import gov.nih.nci.cagrid.sdkinstall.description.InstallationDescription;

import java.io.File;

/** 
 *  DeployInvoker
 *  Base class for utilities which invoke an SDK's deploy task
 * 
 * @author David Ervin
 * 
 * @created Jun 18, 2007 11:58:04 AM
 * @version $Id: DeployInvoker.java,v 1.1 2007-06-18 17:23:23 dervin Exp $ 
 */
public abstract class DeployInvoker {

    private InstallationDescription description;
    private File sdkDir;
    
    public DeployInvoker(InstallationDescription description, File sdkDir) {
        this.description = description;
        this.sdkDir = sdkDir;
    }
    
    
    protected InstallationDescription getInstallationDescription() {
        return this.description;
    }
    
    
    protected File getSdkDir() {
        return this.sdkDir;
    }
    
    
    public abstract void invokeDeployProcess() throws DeployInvocationException ;
}
