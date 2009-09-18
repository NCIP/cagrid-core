package gov.nih.nci.cagrid.testing.system.deployment;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.common.StreamGobbler.LogPriority;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.axis.gsi.GSIConstants;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.jdom.Document;
import org.jdom.Element;
import org.oasis.wsrf.lifetime.Destroy;

import com.counter.CounterPortType;
import com.counter.CreateCounter;
import com.counter.CreateCounterResponse;
import com.counter.service.CounterServiceAddressingLocator;


/**
 * GlobusServiceContainer Service container implementation for globus 4.0.3
 * 
 * @author David Ervin
 * @created Oct 12, 2007 12:01:17 PM
 * @version $Id: GlobusServiceContainer.java,v 1.4 2007/11/05 16:19:58 dervin
 *          Exp $
 */
public class GlobusServiceContainer extends ServiceContainer {

    private static final Log LOG = LogFactory.getLog(ServiceContainer.class);

    public static final String GLOBUS_CONTAINER_CLASSNAME = "org.globus.wsrf.container.ServiceContainer";
    public static final String GLOBUS_SHUTDOWN_CLASSNAME = "org.globus.wsrf.container.ShutdownClient";

    public static final int DEFAULT_SHUTDOWN_WAIT_TIME = 60; // seconds
    public static final int DEFAULT_STARTUP_WAIT_TIME = 60; // seconds
    
    public static final int MAX_SHUTDOWN_RETRY = 20;

    public static final String ENV_ANT_HOME = "ANT_HOME";
    public static final String ENV_GLOBUS_LOCATION = "GLOBUS_LOCATION";

    public static final String DEPLOY_ANT_TARGET = "deployGlobus";

    private Process globusProcess;


    public GlobusServiceContainer(ContainerProperties properties) {
        super(properties);
    }


    public void unpackContainer() throws ContainerException {
        super.unpackContainer();
        // remove security descriptors for insecure deployment
        // (so we can run shutdown)
        try {
            editShutdownServiceDescriptor();
        } catch (IOException ex) {
            unpacked = false;
            throw new ContainerException("Error editing shutdown descriptor: " + ex.getMessage(), ex);
        }
    }


    protected synchronized void deploy(File serviceDir, List<String> deployArgs) throws ContainerException {
        String antHome = System.getenv(ENV_ANT_HOME);
        if (antHome == null || antHome.equals("")) {
            throw new ContainerException(ENV_ANT_HOME + " not set");
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
        if (deployArgs != null && deployArgs.size() != 0) {
            command.addAll(deployArgs);
        }

        // target to execute
        command.add(DEPLOY_ANT_TARGET);

        String[] locationEnvironment = new String[]{ENV_GLOBUS_LOCATION + "="
            + getProperties().getContainerDirectory().getAbsolutePath()};
        String[] editedEnvironment = editEnvironment(locationEnvironment);

        String[] commandArray = command.toArray(new String[command.size()]);
        Process deployProcess = null;
        try {
            deployProcess = Runtime.getRuntime().exec(commandArray, editedEnvironment, serviceDir);
            new StreamGobbler(deployProcess.getInputStream(), StreamGobbler.TYPE_OUT, LOG, LogPriority.DEBUG).start();
            new StreamGobbler(deployProcess.getErrorStream(), StreamGobbler.TYPE_ERR, LOG, LogPriority.ERROR).start();
            deployProcess.waitFor();
        } catch (Exception ex) {
            throw new ContainerException("Error invoking deploy process: " + ex.getMessage(), ex);
        }

        if (deployProcess.exitValue() != 0) {
            throw new ContainerException("deployService ant command failed: " + deployProcess.exitValue());
        }
    }


    private String[] editEnvironment(String[] edits) {
        Map<String, String> envm = new HashMap<String, String>(System.getenv());
        for (String element : edits) {
            String[] envVar = element.split("=");
            envm.put(envVar[0], envVar[1]);
        }
        String[] environment = new String[envm.size()];
        Iterator<String> keys = envm.keySet().iterator();
        int i = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            environment[i++] = key + "=" + envm.get(key);
        }
        return environment;
    }


    protected synchronized void shutdown() throws ContainerException {
        if (this.globusProcess == null) {
            // no globus, no problem
            System.out.println("No globus process, skipping shutdown");
            return;
        }

        final ArrayList<String> opts = new ArrayList<String>();

        // no auth (is the default)
        opts.add("-z");
        opts.add("none");

        String shutdownUrl = null;
        try {
            shutdownUrl = getContainerBaseURI().toString() + "ShutdownService";
        } catch (Exception ex) {
            throw new ContainerException("Error obtaining service URL: " + ex.getMessage(), ex);
        }

        opts.add("-s");
        System.out.println("Contacting shutown service:" + shutdownUrl);
        LOG.debug("Contacting shutown service:" + shutdownUrl);
        opts.add(shutdownUrl);

        // force a JVM kill
        opts.add("hard");

        Process proc = null;
        try {
            proc = runGlobusCommand(GLOBUS_SHUTDOWN_CLASSNAME, opts);
        } catch (IOException ex) {
            throw new ContainerException("Error executing shutdown client process: " + ex.getMessage(), ex);
        }

        int retval = 0;
        try {
            retval = proc.waitFor();
            try {
                int retryCount = 0;
                while (isGlobusRunningCounter() && retryCount < MAX_SHUTDOWN_RETRY) {
                    Thread.sleep(500);
                    retryCount++;
                }
            } catch (Exception ex) {
                // whatever
            }
        } catch (InterruptedException e) {
            throw new ContainerException("Exception error shutting down globus", e);
        }

        if (retval != 0) {
            throw new ContainerException("Unknown error shutting down globus");
        }
    }


    protected synchronized void startup() throws ContainerException {
        // set globus options
        ArrayList<String> opts = new ArrayList<String>();
        Integer port = getProperties().getPortPreference().getPort();
        if (port != null) {
            opts.add("-p");
            opts.add(String.valueOf(port));
            System.out.println("Starting Globus on port: " + port);
            LOG.debug("Starting Globus on port: " + port);
        }

        opts.add("-nosec");

        // start the container
        try {
            globusProcess = runGlobusCommand(GLOBUS_CONTAINER_CLASSNAME, opts);
        } catch (IOException ex) {
            throw new ContainerException("Error executing globus command: " + ex.getMessage(), ex);
        }

        // make sure it is running
        Exception testException = null;
        sleep(2000);
        boolean running = false;
        int wait = DEFAULT_STARTUP_WAIT_TIME;
        if (getProperties().getMaxStartupWaitTime() != null) {
            wait = getProperties().getMaxStartupWaitTime().intValue();
        }
        for (int i = 0; !running && i < wait; i++) {
            LOG.debug("Connection attempt " + (i + 1));
            try {
                running = isGlobusRunningCounter();
            } catch (Exception ex) {
                testException = ex;
            }
            sleep(1000);
        }
        if (!running) {
            if (testException != null) {
                throw new ContainerException("Error starting globus: " + testException.getMessage(), testException);
            } else {
                throw new ContainerException("Globus non responsive after " + wait + " seconds attempting to connect");
            }
        }
    }


    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Checks that Globus is running by hitting the counter service
     * 
     * @return true if the container service could be contacted
     */
    protected synchronized boolean isGlobusRunningCounter() throws IOException, ServiceException {
        org.globus.axis.util.Util.registerTransport();
        CounterServiceAddressingLocator locator = new CounterServiceAddressingLocator();
        EngineConfiguration engineConfig = new FileProvider(getProperties().getContainerDirectory().getAbsolutePath()
            + File.separator + "client-config.wsdd");
        locator.setEngine(new AxisClient(engineConfig));

        String url = getContainerBaseURI().toString() + "CounterService";
        LOG.debug("Connecting to counter at " + url);

        CounterPortType counter = locator.getCounterPortTypePort(new EndpointReferenceType(new Address(url)));
        setAnonymous((Stub) counter);

        CreateCounterResponse response = counter.createCounter(new CreateCounter());
        EndpointReferenceType endpoint = response.getEndpointReference();
        counter = locator.getCounterPortTypePort(endpoint);
        setAnonymous((Stub) counter);
        counter.add(0);
        counter.destroy(new Destroy());
        return true;
    }


    private static void setAnonymous(Stub stub) {
        stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
        stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, NoAuthorization.getInstance());
        stub._setProperty(GSIConstants.GSI_AUTHORIZATION, org.globus.gsi.gssapi.auth.NoAuthorization.getInstance());
    }


    /**
     * @param clName
     * @param heapSizeInMegabytes
     *            null for default value. This must make sense for java command
     *            line. E.g., 64, 128, 256, etc.
     * @param options
     * @return The process created
     * @throws IOException
     */
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

        File containerDir = getProperties().getContainerDirectory();

        File java = new File(System.getProperty("java.home"), "bin" + File.separator + "java");
        File lib = new File(containerDir, "lib");
        String classpath = lib.getAbsolutePath() + File.separator + "bootstrap.jar";
        classpath += File.pathSeparator + lib.getAbsolutePath() + File.separator + "cog-url.jar";
        classpath += File.pathSeparator + lib.getAbsolutePath() + File.separator + "axis-url.jar";

        // build command
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(java.getAbsolutePath());
        if (getProperties().getHeapSizeInMegabytes() != null) {
            cmd.add("-Xmx" + getProperties().getHeapSizeInMegabytes().toString() + "m");
        }
        cmd.add("-Dlog4j.configuration=container-log4j.properties");
        cmd.add("-DGLOBUS_LOCATION=" + containerDir.getAbsolutePath());
        cmd.add("-Djava.endorsed.dirs=" + containerDir.getAbsolutePath() + File.separator + "endorsed");
        cmd.add("-classpath");
        cmd.add(classpath);
        cmd.add("org.globus.bootstrap.Bootstrap");
        cmd.add(clName);
        cmd.add("-debug");

        // add provided options
        cmd.addAll(options);

        // build environment
        String[] locationEnvEdits = new String[]{ENV_GLOBUS_LOCATION + "=" + containerDir.getAbsolutePath()};
        String[] editedEnvironment = editEnvironment(locationEnvEdits);
        LOG.debug("Environment:\n");
        for (String e : editedEnvironment) {
            LOG.debug(e);
        }

        // start the process
        Process proc = Runtime.getRuntime().exec(cmd.toArray(new String[0]), editedEnvironment, containerDir);
        new StreamGobbler(proc.getInputStream(), StreamGobbler.TYPE_OUT, LOG, LogPriority.DEBUG).start();
        new StreamGobbler(proc.getErrorStream(), StreamGobbler.TYPE_ERR, LOG, LogPriority.ERROR).start();
        return proc;
    }


    protected void editShutdownServiceDescriptor() throws IOException {
        // if (this.secure) {
        // return;
        // }
        File coreWSDD = new File(getProperties().getContainerDirectory(), "etc/globus_wsrf_core/server-config.wsdd");
        Document coreWSDDDoc = null;
        try {
            coreWSDDDoc = XMLUtilities.fileNameToDocument(coreWSDD.getAbsolutePath());
        } catch (Exception e) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("Problem writting out config:" + coreWSDD.getAbsolutePath());
        }
    }
}
