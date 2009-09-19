package gov.nih.nci.cagrid.sdkinstall;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/** 
 *  JBossTwiddler
 *  Pings JBoss's Twiddle utility to extract (hopefully) useful information
 * 
 * @author David Ervin
 * 
 * @created Jun 19, 2007 1:57:14 PM
 * @version $Id: JBossTwiddler.java,v 1.2 2007-06-20 16:16:47 dervin Exp $ 
 */
public class JBossTwiddler {
    public static final String CHECK_JBOSS_STARTED = "get \"jboss.system:type=Server\" Started";
    public static final String JBOSS_STARTED_VALUE = "Started=true";
    
    private File jbossDir;

    public JBossTwiddler(File jbossDir) {
        this.jbossDir = jbossDir;
    }
    
    
    public boolean isJBossRunning() throws Exception {
        String command = getTwiddlerBaseCommand();
        command += " " + CHECK_JBOSS_STARTED;
        final ProcessExecutor executor = new ProcessExecutor(command, 
            new File(jbossDir.getAbsolutePath() + File.separator + "bin"));
        Callable processCallable = new Callable() {
            public Boolean call() throws IOException, InterruptedException {
                executor.exec();
                executor.waitForProcess();
                String stdOut = executor.getStdOut().trim();
                return Boolean.valueOf(stdOut.equals(JBOSS_STARTED_VALUE));
            }
        };
        FutureTask<Boolean> processTask = new FutureTask(processCallable);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(processTask);
        return processTask.get().booleanValue();
    }
    
    
    private String getTwiddlerBaseCommand() {
        File jbossBinDir = new File(jbossDir.getAbsolutePath() + File.separator + "bin");
        String baseCommand = jbossBinDir.getAbsolutePath() + File.separator + "twiddle";
        if (isWindows()) {
            return baseCommand + ".bat";
        }
        return baseCommand + ".sh";
    }
    
    
    private boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().indexOf("windows") != -1;
    }
}
