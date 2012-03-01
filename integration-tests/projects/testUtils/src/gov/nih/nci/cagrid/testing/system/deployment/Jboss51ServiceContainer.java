package gov.nih.nci.cagrid.testing.system.deployment;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.StreamGobbler.LogPriority;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
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
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.counter.CounterPortType;
import com.counter.CreateCounter;
import com.counter.CreateCounterResponse;
import com.counter.service.CounterServiceAddressingLocator;

/**
 * Jboss51ServiceContainer 
 * Service container implementation for JBoss 5.1
 * 
 * @author David Ervin
 */
public class Jboss51ServiceContainer extends ServiceContainer {

	private static final Log LOG = LogFactory.getLog(ServiceContainer.class);

	public static final int DEFAULT_STARTUP_WAIT_TIME = 60 * 5; // 5 minutes, expressed in seconds
	public static final int DEFAULT_SHUTDOWN_WAIT_TIME = 60; // seconds

	public static final String ENV_ANT_HOME = "ANT_HOME";
	public static final String ENV_JBOSS_HOME = "JBOSS_HOME";
	public static final String ENV_CATALINA_OPTS = "CATALINA_OPTS";
	public static final String ENV_GLOBUS_LOCATION = "GLOBUS_LOCATION";
    public static final String ENV_JAVA_OPTS = "JAVA_OPTS";
    
    public static final String CACERTS_DIR_PROPERTY = "X509_CERT_DIR";

	public static final String DEPLOY_ANT_TARGET = "deployJBoss";
	
	public static final String J2EE_SCHEMA_NAME = "http://java.sun.com/xml/ns/j2ee";

	private Process jbossProcess;

	public Jboss51ServiceContainer(ContainerProperties properties) {
		super(properties);
	}

    
	@Override
	public void unpackContainer() throws ContainerException {
		super.unpackContainer();
		if (!isWindows()) {
            // make files in /bin directory executable if not on windows platform
		    List<String> command = new ArrayList<String>();
		    command.add("chmod");
		    command.add("a+rwx");
		    command.add("run.sh");
		    command.add("shutdown.sh");

			String[] commandArray = command.toArray(new String[command.size()]);
			Process chmodProcess = null;
			try {
				chmodProcess = Runtime.getRuntime().exec(
						commandArray, null,
						new File(getProperties().getContainerDirectory(), "bin"));
				new StreamGobbler(chmodProcess.getInputStream(),
						StreamGobbler.TYPE_OUT, System.out).start();
				new StreamGobbler(chmodProcess.getErrorStream(),
						StreamGobbler.TYPE_OUT, System.err).start();
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
		if (isWindows()) {
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
        
        // set jboss home
        additionalEnvironment.add(ENV_JBOSS_HOME + "="
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
		if (jbossProcess == null) {
			// no jboss, no problem
			return;
		}
		
		String shutdown = getProperties().getContainerDirectory().getAbsolutePath()
            + File.separator + "bin" + File.separator + "shutdown";

		List<String> command = new ArrayList<String>();

		// executable to call
		if (isWindows()) {
		    command.add("cmd");
		    command.add("/c");
		    command.add(shutdown + ".bat");
		} else {
		    command.add(shutdown + ".sh");
		}
		// make JBoss listen on any and every available interface
		// https://community.jboss.org/wiki/ConfigureServerPorts
		command.add("-s");
		command.add("jnp://localhost:" + getProperties().getPortPreference().getShutdownPort().toString());
    
		// set JBoss home
		List<String> additionalEnvironment = new ArrayList<String>();
		additionalEnvironment.add(ENV_JBOSS_HOME + "="
		    + getProperties().getContainerDirectory().getAbsolutePath());
		final String[] editedEnvironment = editEnvironment(additionalEnvironment);

		LOG.debug("Command environment:\n");
		System.out.println("Command environment:\n");
		for (String e : editedEnvironment) {
		    LOG.debug(e);
		    System.out.println(e);
		}

		Process shutdownProcess = null;
		String[] commandArray = command.toArray(new String[command.size()]);
		try {
		    shutdownProcess = Runtime.getRuntime().exec(
		        commandArray, editedEnvironment, getProperties().getContainerDirectory());
		    new StreamGobbler(shutdownProcess.getInputStream(),
                StreamGobbler.TYPE_OUT, LOG, LogPriority.DEBUG).start();
		    new StreamGobbler(shutdownProcess.getErrorStream(),
                StreamGobbler.TYPE_OUT, LOG, LogPriority.ERROR).start();
		} catch (IOException ex) {
		    throw new ContainerException("Error invoking shutdown process: " + ex.getMessage(), ex);
		}
		// wait for the shutdown process to complete
        final Process finalShutdownProcess = shutdownProcess;
        FutureTask<Boolean> future = new FutureTask<Boolean>(
            new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    LOG.debug("Waiting for shutdown process to complete");
                    finalShutdownProcess.waitFor();
                    LOG.debug("Waiting for JBoss process to terminate");
                    jbossProcess.waitFor();
                    LOG.debug("Done waiting for JBoss process to terminate");

                    return Boolean.valueOf(finalShutdownProcess.exitValue() == 0);
                }
            });

        ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            
            public Thread newThread(Runnable r) {
                Thread th = Executors.defaultThreadFactory().newThread(r);
                th.setDaemon(true);
                return th;
            }
        });
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
            jbossProcess.destroy();
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

		String startup = getProperties().getContainerDirectory().getAbsolutePath()
				+ File.separator + "bin" + File.separator + "run";

		List<String> command = new ArrayList<String>();

		// executable to call
		if (isWindows()) {
			command.add("cmd");
			command.add("/c");
			command.add(startup + ".bat");
		} else {
			command.add(startup + ".sh");
		}
		// make JBoss listen on any and every available interface
		// https://community.jboss.org/wiki/ConfigureServerPorts
		command.add("-b");
		command.add("0.0.0.0");
        
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

		// set JBoss home
        additionalEnvironment.add(ENV_JBOSS_HOME + "="
			+ getProperties().getContainerDirectory().getAbsolutePath());
        
        // set heap size
		if (getProperties().getHeapSizeInMegabytes() != null) {
		    // TODO: check this -- are catalina opts picked up by Tomcat inside JBoss?
			String currentCatalinaOpts = System.getenv(ENV_CATALINA_OPTS);
			if (currentCatalinaOpts != null) {
                additionalEnvironment.add(ENV_CATALINA_OPTS + "=" + currentCatalinaOpts
						+ " -Xmx" + getProperties().getHeapSizeInMegabytes() + "m");
			} else {
                additionalEnvironment.add(ENV_CATALINA_OPTS + "=-Xmx"
						+ getProperties().getHeapSizeInMegabytes() + "m");
			}
			String currentJavaOpts = System.getenv(ENV_JAVA_OPTS);
			additionalEnvironment.add(ENV_JAVA_OPTS + "=" + (currentJavaOpts != null ? currentJavaOpts + " " : "")
			    + "-Xmx" + getProperties().getHeapSizeInMegabytes() + "m" +
		         // the secure version seems to require a LOT more perm gen space to load classes into on the fly
			    (getProperties().isSecure() ? " -XX:MaxPermSize=256m" : ""));
		}
		final String[] editedEnvironment = editEnvironment(additionalEnvironment);

		LOG.debug("Command environment:\n");
        System.out.println("Command environment:\n");
		for (String e : editedEnvironment) {
			LOG.debug(e);
			System.out.println(e);
		}
        
		String[] commandArray = command.toArray(new String[command.size()]);
		// using a Semaphore to prevent "touching" the JBoss container before everything is started.
		// for reasons unknown, invoking anything on the container prematurely causes it to fail
		// in "interesting" ways later
		final Semaphore jbossReady = new Semaphore(1, false);
		try {
		    jbossReady.acquire();
            jbossProcess = Runtime.getRuntime().exec(
                commandArray, editedEnvironment, getProperties().getContainerDirectory());
        } catch (IOException ex) {
            throw new ContainerException("Error starting JBoss process: " + ex.getMessage(), ex);
        } catch (InterruptedException e) {
            throw new ContainerException("Error locking JBoss startup resource: " + e.getMessage(), e);
        }
		// follow this log, watch for the "JBoss started in..." message, THEN hit it with isGlobusRunningCounter()
		final String lookFor1 = "[ServerImpl] JBoss (Microcontainer) ";
		final String lookFor2 = "Started in";
		BufferedOutputStream searchingOutputStream = new BufferedOutputStream(System.out) {
		    @Override
		    public synchronized void write(byte[] b, int off, int len) throws IOException {
		        super.write(b, off, len);
		        String line = new String(b, off, len);
		        if (line.contains(lookFor1) && line.contains(lookFor2)) {
		            System.out.println("Found the line I needed for proper startup");
		            jbossReady.release();
		        }
		    };
		};
		new StreamGobbler(jbossProcess.getInputStream(),
		    StreamGobbler.TYPE_OUT, searchingOutputStream).start();
		new StreamGobbler(jbossProcess.getErrorStream(),
		    StreamGobbler.TYPE_OUT, System.err).start();
		
		// start checking for running
		int wait = DEFAULT_STARTUP_WAIT_TIME;
        if (getProperties().getMaxStartupWaitTime() != null) {
            wait = getProperties().getMaxStartupWaitTime().intValue();
        }
        long waitMs = wait * 1000;
        boolean jbossStarted = false;
		try {
            jbossStarted = jbossReady.tryAcquire(waitMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
		if (!jbossStarted) {
		    throw new ContainerException("JBoss did not start in the allowed time");
		}
		Exception testException = null;
		sleep(2000);
		boolean running = false;
		long totalTime = 0;
        int attempt = 1;
        while (!running && totalTime < waitMs) {
            long start = System.currentTimeMillis();
			System.out.println("Connection attempt " + (attempt));
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
				throw new ContainerException("Error starting JBoss: "
						+ testException.getMessage(), testException);
			} else {
				throw new ContainerException("JBoss non responsive after "
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
		System.out.println("Connecting to counter at " + url);

		CounterPortType counter = locator.getCounterPortTypePort(
		    new EndpointReferenceType(new Address(url)));
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

		CreateCounterResponse response = counter.createCounter(new CreateCounter());
		EndpointReferenceType endpoint = response.getEndpointReference();
		counter = locator.getCounterPortTypePort(endpoint);
		setAnonymous((Stub) counter);
		((Stub) counter).setTimeout(1000);
		LOG.debug("--->trying the counter");
		counter.add(0);
		LOG.debug("--->destroying");
		counter.destroy(new Destroy());
		LOG.debug("--->woo!");
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
            getProperties().getContainerDirectory(), "server/default/deploy/jbossweb.sar/server.xml");
		
        // root config element
		Element configRoot = XMLUtilities.fileNameToDocument(
			serverConfigFile.getAbsolutePath()).getRootElement();
        // locate connector and set the port
		// find the "Service" element
		Iterator serviceElementIterator = configRoot.getChildren("Service",
			configRoot.getNamespace()).iterator();
		while (serviceElementIterator.hasNext()) {
			Element serviceElement = (Element) serviceElementIterator.next();
			// find the jboss.web service
			if (serviceElement.getAttributeValue("name").equals("jboss.web")) {
			    // get the defined connectors out of the Service element
			    Iterator connectorElementIterator = serviceElement.getChildren(
					"Connector", configRoot.getNamespace()).iterator();
				while (connectorElementIterator.hasNext()) {
					Element connectorElement = (Element) connectorElementIterator.next();
					boolean connectorFound = false;
					if (getProperties().isSecure()) {
					    // container is secure -- look for connector on port 8443
						if (connectorElement.getAttributeValue("port").equals("8443")) {
							connectorFound = true;
						}
					} else {
					    // not secure -- look for the connector on port 8080
						if (connectorElement.getAttributeValue("port").equals("8080")) {
							connectorFound = true;
						}
					}
					if (connectorFound) {
					    // set the port number on the connector
						connectorElement.setAttribute("port", port.toString());
						break;
					}
				}
				break;
			}
		}
		// write the config back to disk
		String xml = XMLUtilities.formatXML(
		    XMLUtilities.elementToString(configRoot));
		Utils.stringBufferToFile(new StringBuffer(xml), 
		    serverConfigFile.getAbsolutePath());

		// need to adjust the web.xml to also me consistent with this custom
		// ports...
		File webappConfigFile = new File(getProperties().getContainerDirectory(), 
		    "server/default/deploy/wsrf.war/WEB-INF/web.xml");
		FileInputStream configInput = new FileInputStream(webappConfigFile);
		Element webappConfigRoot = XMLUtilities.streamToDocument(configInput, new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                InputSource source = null;
                int lastSlash = systemId.lastIndexOf('/');
                String filename = systemId.substring(lastSlash);
                LOG.debug("Trying to resolve resource " + systemId + " as " + filename);
                InputStream stream = getClass().getResourceAsStream(filename);
                if (stream != null) {
                    LOG.debug("Resource succesfully resolved");
                    source = new InputSource(stream);
                }
                return source;
            }
        }).getRootElement();
		configInput.close();
		// dig in to the servlet and set the init-param for the protocol
		Element servletEl = webappConfigRoot.getChild("servlet");
		boolean needPortNumber = true;
		boolean needProtocol = true;
		Iterator initParamElementIter = servletEl.getChildren("init-param").iterator();
		while (initParamElementIter.hasNext()) {
		    Element initParamElement = (Element) initParamElementIter.next();
		    Element paramNameElement = initParamElement.getChild("param-name");
		    Element paramValueElement = initParamElement.getChild("param-value");
		    if (paramNameElement.getValue().equals("defaultProtocol")) {
		        needProtocol = false;
		        // edit the protocol
		        paramValueElement.setText(getProperties().isSecure() ? "https" : "http");
		    } else if (paramNameElement.getValue().equals("defaultPort")) {
		        needPortNumber = false;
		        // edit the port number
		        paramValueElement.setText(getProperties().getPortPreference().getPort().toString());
		    }
		}
		if (needProtocol) {
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
		}

		if (needPortNumber) {
		    // now add an init-param for the port number
		    Element initEl2 = new Element("init-param");
		    Element paramName2 = new Element("param-name");
		    paramName2.setText("defaultPort");
		    Element paramValue2 = new Element("param-value");
		    paramValue2.setText(String.valueOf(getProperties().getPortPreference().getPort()));
		    initEl2.addContent(paramName2);
		    initEl2.addContent(paramValue2);
		    servletEl.addContent(servletEl.getChildren().size() - 1, initEl2);
		}

		String webappxml = XMLUtilities.formatXML(XMLUtilities.elementToString(webappConfigRoot));
		Utils.stringBufferToFile(new StringBuffer(webappxml), webappConfigFile.getAbsolutePath());
		
		// change the JNDI listening port to the shutdown port
		File bindingsConfigFile = new File(getProperties().getContainerDirectory(), 
		    "server/default/conf/bindingservice.beans/META-INF/bindings-jboss-beans.xml");
		StringBuffer bindingsConfig = Utils.fileToStringBuffer(bindingsConfigFile);
		String editedBindings = bindingsConfig.toString().replace(
		    "1099", getProperties().getPortPreference().getShutdownPort().toString());
		Utils.stringBufferToFile(new StringBuffer(editedBindings), bindingsConfigFile);
	}


    public StringBuffer getErrorLogs() {
        // TODO: does JBoss put errors in a specific log file, or just all together??
        return getOutLogs();
    }


    public StringBuffer getOutLogs() {
        StringBuffer out = new StringBuffer();
        File logsDir = new File(getProperties().getContainerDirectory(), "server/default/logs");
        File[] files = logsDir.listFiles();
        Comparator<File> logSorter = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Arrays.sort(files, logSorter);
        for (File log : files) {
            out.append("-------------------\n");
            out.append(log.getName()).append("\n");
            out.append("-------------------\n");
            try {
                FileInputStream in = new FileInputStream(log);
                out.append(Utils.inputStreamToStringBuffer(in));
                in.close();
                out.append("\n");
            } catch (Exception ex) {
                // just printing stack here, since we're only trying to 
                // get debug information
                ex.printStackTrace();
            }
        }
        return out;
    }
    
    
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
