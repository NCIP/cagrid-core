package gov.nih.nci.cagrid.sdkinstall;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.sdkinstall.description.InstallationDescription;

import java.io.File;

/** 
 *  Version321BuildInvoker
 *  Invokes the build process for SDK version 3.2.1, assumes it's already configured
 * 
 * @author David Ervin
 * 
 * @created Jun 18, 2007 10:38:41 AM
 * @version $Id: Version321BuildInvoker.java,v 1.5 2007-10-30 17:33:34 hastings Exp $ 
 */
public class Version321BuildInvoker extends BuildInvoker {
    public static final String BUILD_COMMAND = "build-system";

    public Version321BuildInvoker(InstallationDescription description, File sdkDir) {
        super(description, sdkDir);
    }


    public void invokeBuildProcess() throws BuildInvocationException {
        String antCommand = null;
        try {
            antCommand = AntTools.getAntCommand(BUILD_COMMAND, getSdkDir().getAbsolutePath());
        } catch (Exception ex) {
            throw new BuildInvocationException("Error generating ant command: " + ex.getMessage(), ex);
        }
        
        // exec the ant process
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(antCommand);
        } catch (Exception ex) {
            throw new BuildInvocationException("Error creating ant build process: " + ex.getMessage(), ex);
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
            throw new BuildInvocationException(
                "Error executing build process: " + (iex == null ? 
                    " exit status " + exitCode : iex.getMessage()), iex);
        }
    }
}
