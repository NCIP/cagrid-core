package org.cagrid.gridgrouper.test.system.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.StreamGobbler.LogPriority;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GrouperInitStep extends Step {
	private static final Log LOG = LogFactory.getLog(ServiceContainer.class);
	public static final String ENV_ANT_HOME = "ANT_HOME";

	private File grouperDir;

	private ArrayList<String> antArgs = new ArrayList<String>();

	public GrouperInitStep(File grouperDir) {
		super();

		this.grouperDir = grouperDir;
	}

	public void runStep() throws Throwable {
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
		if (antArgs != null && antArgs.size() != 0) {
			command.addAll(antArgs);
		}

		// target to execute
		command.add("grouperInit");

		// environment variables
		List<String> additionalEnvironment = new ArrayList<String>();
		String[] editedEnvironment = editEnvironment(additionalEnvironment);

		String[] commandArray = command.toArray(new String[command.size()]);
		Process initProcess = null;
		try {
			initProcess = Runtime.getRuntime().exec(commandArray, editedEnvironment, grouperDir);
			new StreamGobbler(initProcess.getInputStream(), StreamGobbler.TYPE_OUT, LOG, LogPriority.DEBUG).start();
			new StreamGobbler(initProcess.getErrorStream(), StreamGobbler.TYPE_OUT, LOG, LogPriority.ERROR).start();
			initProcess.waitFor();
		} catch (Exception ex) {
			throw new ContainerException("Error invoking grid grouper init process: " + ex.getMessage(), ex);
		}

		if (initProcess.exitValue() != 0) {
			throw new ContainerException("grouperInit ant command failed: " + initProcess.exitValue());
		}

	}

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
}
