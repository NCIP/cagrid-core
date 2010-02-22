package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;
import org.globus.gsi.GlobusCredential;

public class CreateSystemAdminStep extends Step {
    
	public static final String ENV_ANT_HOME = "ANT_HOME";
	
	private IdentifiersTestInfo testInfo;

	public CreateSystemAdminStep(IdentifiersTestInfo info) {
		this.testInfo = info;
	}
	
	@Override
	public void runStep() throws Exception {
		//
		// We use User A as the system administrator
		//
		runAntTarget(new File(IdentifiersTestInfo.WEBAPP_TMP_DIR), testInfo.getUserA().getIdentity());
		testInfo.setSysAdminUser(testInfo.getUserA());
	}
		
	protected void runAntTarget(File projectDir, String adminIdentity) throws Exception {
		
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

		// target to execute
		command.add("addAdmin");
		command.add("-Dadminuser.input=" + adminIdentity);

		// environment variables
		List<String> additionalEnvironment = new ArrayList<String>();
		String[] editedEnvironment = editEnvironment(additionalEnvironment);

		System.out.println("Command environment:\n");
		for (String e : editedEnvironment) {
			System.out.println(e);
		}

		String[] commandArray = command.toArray(new String[command.size()]);
		Process addAdminProcess = null;
		try {
			addAdminProcess = Runtime.getRuntime().exec(commandArray,
					editedEnvironment, projectDir);
			new StreamGobbler(addAdminProcess.getInputStream(),
					StreamGobbler.TYPE_OUT, System.out).start();
			new StreamGobbler(addAdminProcess.getErrorStream(),
					StreamGobbler.TYPE_OUT, System.err).start();
			addAdminProcess.waitFor();
		} catch (Exception ex) {
			throw new ContainerException("Error invoking deploy process: "
					+ ex.getMessage(), ex);
		}

		if (addAdminProcess.exitValue() != 0) {
			throw new Exception("Ant target addAdmin failed");
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
