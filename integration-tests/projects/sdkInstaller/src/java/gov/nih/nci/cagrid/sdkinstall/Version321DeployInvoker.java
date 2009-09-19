package gov.nih.nci.cagrid.sdkinstall;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.sdkinstall.description.InstallationDescription;

import java.io.File;

/** 
 *  Version321DeployInvoker
 *  Utility to invoke the deployment process for the 3.2.1 SDK
 * 
 * @author David Ervin
 * 
 * @created Jun 18, 2007 12:09:57 PM
 * @version $Id: Version321DeployInvoker.java,v 1.4 2007-10-30 17:33:34 hastings Exp $ 
 */
public class Version321DeployInvoker extends DeployInvoker {
    public static final String DEPLOY_COMMAND = "deployWS";

    public Version321DeployInvoker(InstallationDescription description, File sdkDir) {
        super(description, sdkDir);
    }


    public void invokeDeployProcess() throws DeployInvocationException {
        String antCommand = null;
        try {
            antCommand = AntTools.getAntCommand(DEPLOY_COMMAND, getSdkDir().getAbsolutePath());
        } catch (Exception ex) {
            throw new DeployInvocationException("Error generating ant command: " + ex.getMessage(), ex);
        }
        
        // exec the ant process
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(antCommand);
        } catch (Exception ex) {
            throw new DeployInvocationException("Error creating ant deployment process: " + ex.getMessage(), ex);
        }
        new StreamGobbler(proc.getInputStream(), 
            StreamGobbler.TYPE_OUT).start();
        new StreamGobbler(proc.getErrorStream(), 
            StreamGobbler.TYPE_ERR).start();
        int exitCode = 0;
        InterruptedException iex = null;
        try {
            exitCode = proc.waitFor();
        } catch (InterruptedException ex) {
            iex = ex;
        }
        if (iex != null || exitCode != 0) {
            throw new DeployInvocationException(
                "Error executing deployment process: " + iex == null ? 
                    " exit status " + exitCode : iex.getMessage(), iex);
        }
    }
}
