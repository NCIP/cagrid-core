package gov.nci.nih.cagrid.tests.core.util;

import gov.nih.nci.cagrid.common.StreamGobbler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.axis.gsi.GSIConstants;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.jdom.Document;
import org.jdom.Element;
import org.oasis.wsrf.lifetime.Destroy;
import org.projectmobius.common.MobiusException;
import org.projectmobius.common.XMLUtilities;

import com.counter.CounterPortType;
import com.counter.CreateCounter;
import com.counter.CreateCounterResponse;
import com.counter.service.CounterServiceAddressingLocator;


public class GlobusHelper {

    // Time in seconds to wait for Globus to stop
    private static final int PROCESS_WAIT_TIME = 60;
    
    private boolean secure;
    private File securityDescriptor;
    private Integer port;
    private PortPreference portPrefs;

    private File tmpDir;
    private File tmpGlobusLocation;
    private Process globusProcess;
    private Throwable isGlobusRunningException;
	private List<String> vmargs;


    public GlobusHelper() {
        this(false, null, null);
    }


    public GlobusHelper(File tmpDir) {
        this(false, tmpDir, null);
    }


    public GlobusHelper(File tmpDir, PortPreference portPreference) {
        this(false, tmpDir, portPreference);
    }


    public GlobusHelper(boolean secure) {
        this(secure, null, null);
    }


    public GlobusHelper(boolean secure, PortPreference portPreference) {
        this(secure, null, portPreference);
    }


    public GlobusHelper(boolean secure, File tmpDir, PortPreference portPreference) {
        super();
        this.secure = secure;
        this.tmpDir = tmpDir;
        this.portPrefs = portPreference;
    }

    /**
     * 
     * @param secure whether or not container is secure
     * @param tmpDir the directory where the container resides
     * @param portPreference the globus port for the container
     * @param vmargsParam java arguments for the vm. These are added to the java command BEFORE the class. As such, these should be VM arguments. e.g., -Xmx512m.
     * Note that globus arguments are specified in this constructor and with setters on this class, and are different from these arugments.
     */
    public GlobusHelper(boolean secure, File tmpDir, PortPreference portPreference, List<String> vmargsParam) {
        super();
        this.secure = secure;
        this.tmpDir = tmpDir;
        this.portPrefs = portPreference;
        this.vmargs = vmargsParam;
    }

    public synchronized void createTempGlobus() throws IOException {
        // get globus location
        String globusLocation = System.getenv("GLOBUS_LOCATION");
        if (globusLocation == null || globusLocation.equals("")) {
            throw new IllegalArgumentException("GLOBUS_LOCATION not set");
        }

        // create tmp globus location
        this.tmpGlobusLocation = FileUtils.createTempDir("Globus", "dir", this.tmpDir);

        // copy globus to tmp location
        FileUtils.copyRecursive(new File(globusLocation), this.tmpGlobusLocation, null);

        // remove security descriptors for insecure deployment (so we can run
        // shutdown)
        editShutdownServiceDescriptor();

    }


    protected void editShutdownServiceDescriptor() throws IOException {
        // if (this.secure) {
        // return;
        // }
        File coreWSDD = new File(this.tmpGlobusLocation, "etc/globus_wsrf_core/server-config.wsdd");
        Document coreWSDDDoc = null;
        try {
            coreWSDDDoc = XMLUtilities.fileNameToDocument(coreWSDD.getAbsolutePath());
        } catch (MobiusException e) {
            e.printStackTrace();
            throw new IOException("Problem loading WSRF Core Service config (" + coreWSDD.getAbsolutePath() + "):"
                + e.getMessage());
        }

        List serviceEls = coreWSDDDoc.getRootElement().getChildren("service",
            coreWSDDDoc.getRootElement().getNamespace());
        for (int serviceI = 0; serviceI < serviceEls.size(); serviceI++) {
            Element serviceEl = (Element) serviceEls.get(serviceI);
            String serviceName = serviceEl.getAttributeValue("name");
            if (serviceName.equals("ShutdownService")) {
                List servParamElms = serviceEl.getChildren("parameter", serviceEl.getNamespace());
                for (int serviceParamsI = 0; serviceParamsI < servParamElms.size(); serviceParamsI++) {
                    Element serviceParam = (Element) servParamElms.get(serviceParamsI);
                    if (serviceParam.getAttributeValue("name").equals("securityDescriptor")) {
                        servParamElms.remove(serviceParamsI);
                    }
                }
            }
        }

        try {
            FileWriter fw = new FileWriter(coreWSDD);
            fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(coreWSDDDoc)));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Problem writting out config:" + coreWSDD.getAbsolutePath());

        }
    }


    public synchronized void deployService(File serviceDir) throws IOException, InterruptedException {
        deployService(serviceDir, null);
    }


    public synchronized void deployService(File serviceDir, List<String> args) throws IOException, InterruptedException {
        deployService(serviceDir, "deployGlobus", args);
    }


    public synchronized void deployService(File serviceDir, String target, List<String> args) throws IOException,
        InterruptedException {
        String antHome = System.getenv("ANT_HOME");
        if (antHome == null || antHome.equals("")) {
            throw new IllegalArgumentException("ANT_HOME not set");
        }
        File ant = new File(antHome, "bin" + File.separator + "ant");

        List<String> command = new ArrayList<String>();

        // executable to call
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command.add("cmd");
            command.add("/c");
            command.add(ant + ".bat");
        } else {
            command.add(ant.toString());
        }

        // any arguments
        if (args != null && args.size() > 0) {
            command.addAll(args);
        }

        // target to execute
        command.add(target);

        String[] envp = new String[]{"GLOBUS_LOCATION=" + this.tmpGlobusLocation.toString(),};
        envp = EnvUtils.overrideEnv(envp);

        String[] commandArray = command.toArray(new String[command.size()]);
        Process p = Runtime.getRuntime().exec(commandArray, envp, serviceDir);
        new StreamGobbler(p.getInputStream(), StreamGobbler.TYPE_OUT, System.out).start();
        new StreamGobbler(p.getErrorStream(), StreamGobbler.TYPE_ERR, System.err).start();
        p.waitFor();

        if (p.exitValue() != 0) {
            throw new IOException("deployService ant command failed: " + p.exitValue());
        }
    }


    public synchronized void startGlobus() throws IOException {
        ArrayList<String> opts = new ArrayList<String>();
        if (getPort() != null) {
            opts.add("-p");
            opts.add(String.valueOf(getPort()));
            System.out.println("Starting Globus on port:" + getPort());
        }
        if (this.secure && getSecurityDescriptor() != null) {
            opts.add("-containerDesc");
            opts.add(getSecurityDescriptor().toString());
        }
        if (!this.secure) {
            opts.add("-nosec");
        }
        this.globusProcess = runGlobusCommand("org.globus.wsrf.container.ServiceContainer", opts);

        // make sure it is running
        this.isGlobusRunningException = null;
        sleep(2000);
        for (int i = 0; i < 10; i++) {
            if (isGlobusRunning()) {
                return;
            }
            sleep(1000);
        }
        this.isGlobusRunningException.printStackTrace();
        throw new IOException("could not start Globus");
    }

    private synchronized Process runGlobusCommand(String clName, List<String> options) throws IOException {
        // create globus startup params
        // %_RUNJAVA% -Dlog4j.configuration=container-log4j.properties
        // %LOCAL_OPTS% %GLOBUS_OPTIONS% -classpath %LOCALCLASSPATH%
        // org.globus.bootstrap.Bootstrap
        // org.globus.wsrf.container.ServiceContainer %CMD_LINE_ARGS%
        // C:\Globus4.0.1\bin>"C:\jdk1.5.0_03\bin\java"
        // -Dlog4j.configuration=container-log
        // 4j.properties -DGLOBUS_LOCATION="C:\Globus4.0.1"
        // -Djava.endorsed.dirs="C:\Globus
        // 4.0.1\endorsed" -classpath
        // "C:\Globus4.0.1\lib\bootstrap.jar";"C:\Globus4.0.1\l
        // ib\cog-url.jar";"C:\Globus4.0.1\lib\axis-url.jar"
        // org.globus.bootstrap.Bootstrap
        // org.globus.wsrf.container.ServiceContainer -nosec -debug
        File java = new File(System.getProperty("java.home"), "bin" + File.separator + "java");
        File lib = new File(this.tmpGlobusLocation, "lib");
        String classpath = lib + File.separator + "bootstrap.jar";
        classpath += File.pathSeparator + lib + File.separator + "cog-url.jar";
        classpath += File.pathSeparator + lib + File.separator + "axis-url.jar";

        // build command
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(java.toString());
        cmd.add("-Dlog4j.configuration=container-log4j.properties");
        cmd.add("-DGLOBUS_LOCATION=" + this.tmpGlobusLocation);
        cmd.add("-Djava.endorsed.dirs=" + this.tmpGlobusLocation + File.separator + "endorsed");
        if (this.vmargs != null) {
        	cmd.addAll(this.vmargs);
        }
        cmd.add("-classpath");
        cmd.add(classpath);
        cmd.add("org.globus.bootstrap.Bootstrap");
        cmd.add(clName);
        cmd.add("-debug");

        // add provided options
        cmd.addAll(options);

        // build environment
        String[] envp = new String[]{"GLOBUS_LOCATION=" + this.tmpGlobusLocation};
        envp = EnvUtils.overrideEnv(envp);

        // start globus
        Process p = Runtime.getRuntime().exec(cmd.toArray(new String[0]), envp, this.tmpGlobusLocation);
        new StreamGobbler(p.getInputStream(), StreamGobbler.TYPE_OUT, System.out).start();
        new StreamGobbler(p.getErrorStream(), StreamGobbler.TYPE_ERR, System.err).start();
        return p;
    }


    public synchronized boolean isGlobusRunning() {
        return isGlobusRunningCounter();
    }


    public synchronized URI getContainerBaseURI() throws MalformedURIException {
        URI url = null;
        try {
            url = new URI("http://localhost:" + getPort() + "/wsrf/services/");
            if (this.secure) {
                url = new URI("https://localhost:" + getPort() + "/wsrf/services/");
            }
        } catch (NoAvailablePortException e) {
            throw new MalformedURIException("Problem getting port:" + e.getMessage());
        }
        return url;
    }


    public synchronized EndpointReferenceType getServiceEPR(String servicePath) throws MalformedURIException {
        EndpointReferenceType epr = null;

        String url = getContainerBaseURI().toString() + servicePath;
        epr = new EndpointReferenceType(new Address(url));

        return epr;
    }


    protected synchronized boolean isGlobusRunningCounter() {
        try {
            org.globus.axis.util.Util.registerTransport();
            CounterServiceAddressingLocator locator = new CounterServiceAddressingLocator();
            EngineConfiguration engineConfig = new FileProvider(System.getenv("GLOBUS_LOCATION") + File.separator
                + "client-config.wsdd");
            locator.setEngine(new AxisClient(engineConfig));

            String url = getContainerBaseURI().toString() + "CounterService";

            CounterPortType counter = locator.getCounterPortTypePort(new EndpointReferenceType(new Address(url)));
            setAnonymous((Stub) counter);

            CreateCounterResponse response = counter.createCounter(new CreateCounter());
            EndpointReferenceType endpoint = response.getEndpointReference();
            counter = locator.getCounterPortTypePort(endpoint);
            setAnonymous((Stub) counter);
            counter.add(0);
            counter.destroy(new Destroy());
            return true;
        } catch (IOException e) {
            this.isGlobusRunningException = e;
            return false;
        } catch (ServiceException e) {
            this.isGlobusRunningException = e;
            return false;
        }
    }


    private static void setAnonymous(Stub stub) {
        stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
        stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, NoAuthorization.getInstance());
        stub._setProperty(GSIConstants.GSI_AUTHORIZATION, org.globus.gsi.gssapi.auth.NoAuthorization.getInstance());
    }


    public synchronized boolean stopGlobus() throws IOException {
        if (this.globusProcess == null) {
            return true;
        }

        ArrayList<String> opts = new ArrayList<String>();
        if (this.secure && getSecurityDescriptor() != null) {
            opts.add("-f");
            opts.add(getSecurityDescriptor().toString());
        } else {
            // anonymous
            opts.add("-a");
        }

        // no auth (is the default)
        opts.add("-z");
        opts.add("none");

        String shutdown = getServiceEPR("ShutdownService").getAddress().toString();
        opts.add("-s");
        System.out.println("Contacting shutown service:" + shutdown);
        opts.add(shutdown);

        // force a JVM kill
        opts.add("hard");

        boolean success = false;
        final Process process = runGlobusCommand("org.globus.wsrf.container.ShutdownClient", opts);

        // create a Future to get the boolean success status
        FutureTask<Boolean> future = new FutureTask<Boolean>(new Callable<Boolean>() {
            public Boolean call() {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    // this may happen if we timeout
                }
                // return true if the status is 0
                return Boolean.valueOf(process.exitValue() == 0);
            }
        });

        // execute the task of waiting for completion and getting the status
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(future);
        
        try {
            // try to get the status
            success = future.get(PROCESS_WAIT_TIME, TimeUnit.SECONDS).booleanValue();
        } catch (Exception e) {
            System.err.println("Globus process failed to stop in allowed time (" 
                + PROCESS_WAIT_TIME + " " + TimeUnit.SECONDS + ")");
            e.printStackTrace();
        }

        // destroy it for saftey sake
        this.globusProcess.destroy();
        this.globusProcess = null;

        return success;
    }


    public synchronized void cleanupTempGlobus() {
        if (this.tmpGlobusLocation != null) {
            FileUtils.deleteRecursive(this.tmpGlobusLocation);
        }
    }


    public synchronized File getTempGlobusLocation() {
        return this.tmpGlobusLocation;
    }


    public synchronized File getSecurityDescriptor() {
        return this.securityDescriptor;
    }


    public synchronized void setSecurityDescriptor(File securityDescriptor) {
        this.securityDescriptor = securityDescriptor;
    }


    private static void sleep(long ms) {
        Object sleep = new Object();
        try {
            synchronized (sleep) {
                sleep.wait(ms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @return Returns the secure.
     */
    public boolean isSecure() {
        return this.secure;
    }


    /**
     * @param secure
     *            The secure to set.
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }


    /**
     * @param port
     *            The port to set.
     */
    public void setPort(Integer port) {
        this.port = port;
    }


    /**
     * @return the port
     * @throws NoAvailablePortException
     */
    public synchronized Integer getPort() throws NoAvailablePortException {
        if (this.port == null) {
            initializePort();
        }
        return this.port;
    }


    private void initializePort() throws NoAvailablePortException {
        if (this.portPrefs == null) {
            String portProp = System.getProperty("test.globus.port");
            if (portProp != null) {
                this.portPrefs = new PortPreference(new Integer(portProp));
            } else {
                this.portPrefs = new PortPreference(getDefaultPortRangeMinimum(), getDefaultPortRangeMaximum(), null);
            }

        }

        this.port = this.portPrefs.getPort();
    }


    public static Integer getDefaultPortRangeMaximum() {
        return new Integer(System.getProperty(GlobusHelper.class.getName() + ".portrange.max", "10100"));
    }


    public static Integer getDefaultPortRangeMinimum() {
        return new Integer(System.getProperty(GlobusHelper.class.getName() + ".portrange.min", "10000"));
    }
}
