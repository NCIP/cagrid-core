package gov.nih.nci.cagrid.sdkinstall;

import java.io.File;

import gov.nih.nci.cagrid.sdkinstall.description.InstallationDescription;

/** 
 *  BuildInvoker
 *  Base class for utilities which invoke the SDK's build process
 * 
 * @author David Ervin
 * 
 * @created Jun 18, 2007 10:28:22 AM
 * @version $Id: BuildInvoker.java,v 1.1 2007-06-18 15:29:40 dervin Exp $ 
 */
public abstract class BuildInvoker {
    
    private InstallationDescription description;
    private File sdkDir;

    public BuildInvoker(InstallationDescription description, File sdkDir) {
        this.description = description;
        this.sdkDir = sdkDir;
    }
    
    
    protected InstallationDescription getInstallationDescription() {
        return this.description;
    }
    
    
    protected File getSdkDir() {
        return sdkDir;
    }
    
    
    public abstract void invokeBuildProcess() throws BuildInvocationException;
}
