package gov.nih.nci.cagrid.sdkinstall;


import java.io.File;
import java.io.IOException;

/** 
 *  JBossShutdown
 *  Utility to shut down a jboss instance running locally
 * 
 * @author David Ervin
 * 
 * @created Jun 20, 2007 10:56:33 AM
 * @version $Id: JBossShutdown.java,v 1.1 2007-06-20 16:16:47 dervin Exp $ 
 */
public class JBossShutdown {
    private File jbossDir;

    public JBossShutdown(File jbossDir) {
        this.jbossDir = jbossDir;
    }
    
    
    public void shutdownLocalJboss() throws IOException {
        String command = getShutdownBaseCommand();
        command += " -S";
        ProcessExecutor exec = new ProcessExecutor(command, 
            new File(jbossDir.getAbsolutePath() + File.separator + "bin"));
        exec.exec();
    }
    
    
    private String getShutdownBaseCommand() {
        String command = jbossDir.getAbsolutePath() + File.separator + "bin" + File.separator + "shutdown";
        if (isWindows()) {
            command += ".bat";
        } else {
            command += ".sh";
        }
        return command;
    }
    
    
    private boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().indexOf("windows") != -1;
    }
}
