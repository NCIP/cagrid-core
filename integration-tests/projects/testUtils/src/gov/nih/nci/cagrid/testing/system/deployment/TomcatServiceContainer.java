package gov.nih.nci.cagrid.testing.system.deployment;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.common.StreamGobbler.LogPriority;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.axis.gsi.GSIConstants;
import org.globus.common.CoGProperties;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.jdom.Element;
import org.oasis.wsrf.lifetime.Destroy;

import com.counter.CounterPortType;
import com.counter.CreateCounter;
import com.counter.CreateCounterResponse;
import com.counter.service.CounterServiceAddressingLocator;

/**
 * TomcatServiceContainer Service container implementation for tomcat
 * 
 * @author David Ervin
 * @created Oct 19, 2007 12:01:22 PM
 * @version $Id: TomcatServiceContainer.java,v 1.4 2007/11/05 16:19:58 dervin
 *          Exp $
 */
public class TomcatServiceContainer extends ServiceContainer {

	private static final Log LOG = LogFactory.getLog(ServiceContainer.class);

	public static final int DEFAULT_STARTUP_WAIT_TIME = 60; // seconds
	public static final int DEFAULT_SHUTDOWN_WAIT_TIME = 60; // seconds

	public static final String ENV_ANT_HOME = "ANT_HOME";
	public static final String ENV_CATALINA_HOME = "CATALINA_HOME";
	public static final String ENV_CATALINA_OPTS = "CATALINA_OPTS";
	public static final String ENV_GLOBUS_LOCATION = "GLOBUS_LOCATION";
    public static final String ENV_JAVA_OPTS = "JAVA_OPTS";
    
    public static final String CACERTS_DIR_PROPERTY = "X509_CERT_DIR";

	public static final String DEPLOY_ANT_TARGET = "deployTomcat";

	private Process catalinaProcess;

	public TomcatServiceContainer(ContainerProperties properties) {
		super(properties);
	}

    
	@Override
	public void unpackContainer() throws ContainerException {
		super.unpackContainer();
		if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            // make files in /bin directory executable if not on windows platform
		    List<String> command = new ArrayList<String>();
		    command.add("chmod");
		    command.add("a+rwx");
		    command.add("catalina.50.sh");
		    command.add("digest.sh");
		    command.add("catalina.sh");
		    command.add("setclasspath.sh");
		    command.add("shutdown-using-launcher.sh");
		    command.add("shutdown.sh");
		    command.add("startup-using-launcher.sh");
		    command.add("startup.sh");
		    command.add("tool-wrapper-using-launcher.sh");
		    command.add("tool-wrapper.sh");
		    command.add("version.sh");

			String[] commandArray = command.toArray(new String[command.size()]);
			Process chmodProcess = null;
			try {
				chmodProcess = Runtime.getRuntime().exec(
						commandArray,
						null,
						new File(getProperties().getContainerDirectory(), "bin"));
				new StreamGobbler(chmodProcess.getInputStream(),
						StreamGobbler.TYPE_OUT,System.out).start();
				new StreamGobbler(chmodProcess.getErrorStream(),
						StreamGobbler.TYPE_OUT,System.err).start();
				chmodProcess.waitFor();
			} catch (Exception ex) {
				throw new ContainerException("Error invoking chmod process: "
						+ ex.getMessage(), ex);
			}
		}
	}
    

	protected void deploy(File serviceDir, List<String> deployArgs)
			throws ContainerException {
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
			command.add(ant.getAbsolutePath() + ".bat");
		} else {
			command.add(ant.getAbsolutePath());
		}

		// any arguments
		if (deployArgs != null && deployArgs.size() != 0) {
			command.addAll(deployArgs);
		}

		// target to execute
		command.add(DEPLOY_ANT_TARGET);

        // environment variables
        List<String> additionalEnvironment = new ArrayList<String>();
        
        // set catalina home
        additionalEnvironment.add(ENV_CATALINA_HOME + "="
            + getProperties().getContainerDirectory().getAbsolutePath());
		String[] editedEnvironment = editEnvironment(additionalEnvironment);
        

		LOG.debug("Command environment:\n");
		for (String e : editedEnvironment) {
			LOG.debug(e);
		}

		String[] commandArray = command.toArray(new String[command.size()]);
		Process deployProcess = null;
		try {
			deployProcess = Runtime.getRuntime().exec(commandArray,
					editedEnvironment, serviceDir);
			new StreamGobbler(deployProcess.getInputStream(),
					StreamGobbler.TYPE_OUT, LOG, LogPriority.DEBUG).start();
			new StreamGobbler(deployProcess.getErrorStream(),
					StreamGobbler.TYPE_OUT, LOG, LogPriority.ERROR).start();
			deployProcess.waitFor();
		} catch (Exception ex) {
			throw new ContainerException("Error invoking deploy process: "
					+ ex.getMessage(), ex);
		}

		if (deployProcess.exitValue() != 0) {
			throw new ContainerException("deployService ant command failed: "
					+ deployProcess.exitValue());
		}
	}

    
	protected void shutdown() throws ContainerException {
		if (catalinaProcess == null) {
			// no tomcat, no problem
			return;
		}

        // locate the shutdown script
		String shutdownExecutable = getProperties().getContainerDirectory()
				.getAbsolutePath()
				+ File.separator + "bin" + File.separator + "catalina";

        // build the command line to call
		List<String> command = new ArrayList<String>();
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			command.add("cmd");
			command.add("/c");
			command.add(shutdownExecutable + ".bat");
			command.add("stop");
		} else {
			command.add(shutdownExecutable + ".sh");
			command.add("stop");
		}

        // set up the environment variables
        List<String> additionalEnvironment = new ArrayList<String>();
		additionalEnvironment.add(ENV_CATALINA_HOME + "="
				+ getProperties().getContainerDirectory().getAbsolutePath());
		String[] editedEnvironment = editEnvironment(additionalEnvironment);

		LOG.debug("Command environment:\n");
		for (String e : editedEnvironment) {
			LOG.debug(e);
		}

        // fork and execute a process
		String[] commandArray = command.toArray(new String[command.size()]);
		Process shutdownProcess = null;
		try {
			shutdownProcess = Runtime.getRuntime().exec(commandArray,
					editedEnvironment, getProperties().getContainerDirectory());
			new StreamGobbler(shutdownProcess.getInputStream(),
					StreamGobbler.TYPE_OUT, LOG, LogPriority.DEBUG).start();
			new StreamGobbler(shutdownProcess.getErrorStream(),
					StreamGobbler.TYPE_OUT, LOG, LogPriority.ERROR).start();
		} catch (Exception ex) {
			throw new ContainerException("Error invoking shutdown process: "
					+ ex.getMessage(), ex);
		}

        // wait for the shutdown process to complete
		final Process finalShutdownProcess = shutdownProcess;
		FutureTask<Boolean> future = new FutureTask<Boolean>(
		    new Callable<Boolean>() {
		        public Boolean call() throws Exception {
		            LOG.debug("Waiting for shutdown process to complete");
		            finalShutdownProcess.waitFor();
		            LOG.debug("Waiting for catalina process to terminate");
		            catalinaProcess.waitFor();
		            LOG.debug("Done waiting for catalina process to terminate");

		            return Boolean.valueOf(finalShutdownProcess.exitValue() == 0);
		        }
		    });

		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(future);

		boolean success = false;
		try {
			int wait = DEFAULT_SHUTDOWN_WAIT_TIME;
			if (getProperties().getMaxShutdownWaitTime() != null) {
				wait = getProperties().getMaxShutdownWaitTime().intValue();
			}
            LOG.debug("WAITING " + wait + " seconds to shut down");
			success = future.get(wait, TimeUnit.SECONDS).booleanValue();
		} catch (Exception ex) {
			throw new ContainerException("Error shutting down container: "
					+ ex.getMessage(), ex);
		} finally {
            LOG.debug("Shutdown task complete, destroying processes");
			future.cancel(true);
			executor.shutdownNow();
			shutdownProcess.destroy();
			catalinaProcess.destroy();
            LOG.debug("Processes destroyed");
		}

		if (!success) {
			throw new ContainerException("Shutdown command failed: " +
			    "(process exited with value of " + 
                finalShutdownProcess.exitValue() + ")");
		}
	}
    

	protected void startup() throws ContainerException {
		try {
			setServerPort();
		} catch (Exception ex) {
			throw new ContainerException("Error setting server port: "
					+ ex.getMessage(), ex);
		}

		String startup = getProperties().getContainerDirectory()
				.getAbsolutePath()
				+ File.separator + "bin" + File.separator + "catalina";

		List<String> command = new ArrayList<String>();

		// executable to call
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			command.add("cmd");
			command.add("/c");
			command.add(startup + ".bat");
			command.add("run");
		} else {
			command.add(startup + ".sh");
			command.add("run");
		}
        
        // if container is secure, set the system property X509_CERT_DIR to the certificates directory
        List<String> additionalEnvironment = new ArrayList<String>();
        if (getProperties().isSecure()) {
            try {
                File certsDir = ((SecureContainer) this).getCertificatesDirectory();
                String caCertsDir = new File(certsDir, "ca").getCanonicalPath();
                String x509CertsEnv = ENV_JAVA_OPTS + "=-D" + CACERTS_DIR_PROPERTY + "=" + caCertsDir;
                additionalEnvironment.add(x509CertsEnv);
            } catch (Exception ex) {
                throw new ContainerException("Error setting ca certificates directory!");
            }
        }

		// set catalina home
        additionalEnvironment.add(ENV_CATALINA_HOME + "="
				+ getProperties().getContainerDirectory().getAbsolutePath());
        
        // set heap size
		if (getProperties().getHeapSizeInMegabytes() != null) {
			String currentCatalinaOpts = System.getenv(ENV_CATALINA_OPTS);
			if (currentCatalinaOpts != null) {
                additionalEnvironment.add(ENV_CATALINA_OPTS + "=\"" + currentCatalinaOpts
						+ " -Xmx" + getProperties().getHeapSizeInMegabytes()
						+ "m\"");
			} else {
                additionalEnvironment.add(ENV_CATALINA_OPTS + "=\"-Xmx"
						+ getProperties().getHeapSizeInMegabytes() + "m\"");
			}
		}
		String[] editedEnvironment = editEnvironment(additionalEnvironment);

		LOG.debug("Command environment:\n");
        System.out.println("Command environment:\n");
		for (String e : editedEnvironment) {
			LOG.debug(e);
			System.out.println(e);
		}
        
		String[] commandArray = command.toArray(new String[command.size()]);
		try {
			catalinaProcess = Runtime.getRuntime().exec(commandArray,
					editedEnvironment, getProperties().getContainerDirectory());
			new StreamGobbler(catalinaProcess.getInputStream(),
					StreamGobbler.TYPE_OUT, System.out).start();
			new StreamGobbler(catalinaProcess.getErrorStream(),
					StreamGobbler.TYPE_OUT, System.err).start();
		} catch (Exception ex) {
			throw new ContainerException("Error invoking startup process: "
					+ ex.getMessage(), ex);
		}

		// start checking for running
		Exception testException = null;
		sleep(2000);
		boolean running = false;
		int wait = DEFAULT_STARTUP_WAIT_TIME;
		if (getProperties().getMaxStartupWaitTime() != null) {
			wait = getProperties().getMaxStartupWaitTime().intValue();
		}
        long waitMs = wait * 1000;
        long totalTime = 0;
        int attempt = 1;
        while (!running && totalTime < waitMs) {
            long start = System.currentTimeMillis();
			LOG.debug("Connection attempt " + (attempt));
			try {
				running = isGlobusRunningCounter();
			} catch (Exception ex) {
				testException = ex;
				//ex.printStackTrace();
			}
			sleep(5000);
            attempt++;
            totalTime += (System.currentTimeMillis() - start);
		}
		if (!running) {
			if (testException != null) {
				throw new ContainerException("Error starting Tomcat: "
						+ testException.getMessage(), testException);
			} else {
				throw new ContainerException("Tomcat non responsive after "
						+ wait + " seconds attempting to connect");
			}
		}
	}
    

	// ---------------
	// Helpers
	// ---------------

    
	private String[] editEnvironment(List<String> edits) {
		Map<String, String> systemEnvironment = new HashMap<String, String>(System.getenv());
		for (String element : edits) {
            int splitIndex = element.indexOf('=');
            String[] envVar = new String[2];
            envVar[0] = element.substring(0, splitIndex);
            envVar[1] = element.substring(splitIndex + 1);
			systemEnvironment.put(envVar[0], envVar[1]);
		}
		String[] environment = new String[systemEnvironment.size()];
		Iterator<String> keys = systemEnvironment.keySet().iterator();
		int i = 0;
		while (keys.hasNext()) {
			String key = keys.next();
			environment[i++] = key + "=" + systemEnvironment.get(key);
		}
		return environment;
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
	protected synchronized boolean isGlobusRunningCounter() throws IOException,
			ServiceException, ContainerException {
		org.globus.axis.util.Util.registerTransport();
		CounterServiceAddressingLocator locator = new CounterServiceAddressingLocator();
		String globusLocation = System.getenv(ENV_GLOBUS_LOCATION);
		EngineConfiguration engineConfig = new FileProvider(globusLocation
				+ File.separator + "client-config.wsdd");
		// TODO: do we even need this?
		locator.setEngine(new AxisClient(engineConfig));

		String url = getContainerBaseURI().toString() + "CounterService";
		LOG.debug("Connecting to counter at " + url);

		CounterPortType counter = locator
				.getCounterPortTypePort(new EndpointReferenceType(new Address(
						url)));
		setAnonymous((Stub) counter);
		
        if (getProperties().isSecure()) {
            File caCertsDir = null;
            try {
                caCertsDir = new File(((SecureContainer) this).getCertificatesDirectory(), "ca");
            } catch (Exception ex) {
                throw new ContainerException("Error obtaining ca certs directory: " + ex.getMessage(), ex);
            }
            CoGProperties cogProperties = CoGProperties.getDefault();
            cogProperties.setCaCertLocations(caCertsDir.getAbsolutePath());
            CoGProperties.setDefault(cogProperties);
        }
		

		CreateCounterResponse response = counter
				.createCounter(new CreateCounter());
		EndpointReferenceType endpoint = response.getEndpointReference();
		counter = locator.getCounterPortTypePort(endpoint);
		setAnonymous((Stub) counter);
		((Stub) counter).setTimeout(1000);
		counter.add(0);
		counter.destroy(new Destroy());
		return true;
	}

    
	private static void setAnonymous(Stub stub) {
		stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS,
				Boolean.TRUE);
		stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION,
				NoAuthorization.getInstance());
		stub._setProperty(GSIConstants.GSI_AUTHORIZATION,
				org.globus.gsi.gssapi.auth.NoAuthorization.getInstance());
	}
    

	private void setServerPort() throws Exception {
        // get the server's listen port
		Integer port = getProperties().getPortPreference().getPort();
        // load the server config file
		File serverConfigFile = new File(
            getProperties().getContainerDirectory(), "conf" + File.separator + "server.xml");
        // root config element
		Element configRoot = XMLUtilities.fileNameToDocument(
				serverConfigFile.getAbsolutePath()).getRootElement();
        // set the shutdown port
		configRoot.setAttribute("port", String.valueOf(getProperties()
				.getPortPreference().getShutdownPort()));
        // locate catalina connector and set the port
		Iterator serviceElementIterator = configRoot.getChildren("Service",
				configRoot.getNamespace()).iterator();
		while (serviceElementIterator.hasNext()) {
			Element serviceElement = (Element) serviceElementIterator.next();
			if (serviceElement.getAttributeValue("name").equals("Catalina")) {
				Iterator connectorElementIterator = serviceElement.getChildren(
						"Connector", configRoot.getNamespace()).iterator();
				while (connectorElementIterator.hasNext()) {
					Element connectorElement = (Element) connectorElementIterator
							.next();
					boolean connectorFound = false;
					if (getProperties().isSecure()) {
						if (connectorElement.getAttributeValue("port").equals(
								"8443")
								&& connectorElement
										.getAttributeValue("className")
										.equals(
												"org.globus.tomcat.coyote.net.HTTPSConnector")) {
							connectorFound = true;
						}
					} else {
						if (connectorElement.getAttributeValue("port").equals(
								"8080")) {
							connectorFound = true;
						}
					}
					if (connectorFound) {
						connectorElement.setAttribute("port", port.toString());
						break;
					}
				}
				break;
			}
		}
		String xml = XMLUtilities.formatXML(XMLUtilities
				.elementToString(configRoot));
		Utils.stringBufferToFile(new StringBuffer(xml), serverConfigFile
				.getAbsolutePath());

		// need to adjust the web.xml to also me consistent with this custom
		// ports...
		File webappConfigFile = new File(getProperties()
				.getContainerDirectory(), "webapps" + File.separator + "wsrf"
				+ File.separator + "WEB-INF" + File.separator + "web.xml");
		Element webappConfigRoot = XMLUtilities.fileNameToDocument(
				webappConfigFile.getAbsolutePath()).getRootElement();
		Element servletEl = webappConfigRoot.getChild("servlet");
		Element initEl = new Element("init-param");
		Element paramName = new Element("param-name");
		paramName.setText("defaultProtocol");
		Element paramValue = new Element("param-value");
		if (getProperties().isSecure()) {
			paramValue.setText("https");
		} else {
			paramValue.setText("http");
		}
		initEl.addContent(paramName);
		initEl.addContent(paramValue);
		servletEl.addContent(servletEl.getChildren().size() - 1, initEl);

		Element initEl2 = new Element("init-param");
		Element paramName2 = new Element("param-name");
		paramName2.setText("defaultPort");
		Element paramValue2 = new Element("param-value");
		paramValue2.setText(String.valueOf(getProperties().getPortPreference()
				.getPort()));
		initEl2.addContent(paramName2);
		initEl2.addContent(paramValue2);
		servletEl.addContent(servletEl.getChildren().size() - 1, initEl2);

		String webappxml = XMLUtilities.formatXML(XMLUtilities
				.elementToString(webappConfigRoot));
		Utils.stringBufferToFile(new StringBuffer(webappxml), webappConfigFile
				.getAbsolutePath());
	}
}
