/**
 * 
 */
package org.cagrid.installer.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.util.IOThread;
import org.cagrid.installer.util.InstallerUtils;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class AntExecutionTask extends BasicTask {

	private static final Log logger = LogFactory.getLog(AntExecutionTask.class);

	private String target;

	private Map<String, String> environment;

	private Properties systemProperties;
	
	private String buildFilePath;

	public AntExecutionTask(String name, String description, String buildFilePath, String target) {
		this(name, description, buildFilePath, target, new HashMap<String, String>(),
				new Properties());
	}

	public AntExecutionTask(String name, String description, String buildFilePath, String target,
			Map<String, String> environment, Properties systemProperties) {
		super(name, description);
		this.buildFilePath = buildFilePath;
		this.target = target;
		this.environment = environment;
		this.systemProperties = systemProperties;
	}

	protected Object internalExecute(CaGridInstallerModel model)
			throws Exception {
		
		String tempDirPath = model.getProperty(Constants.TEMP_DIR_PATH);

		try {
			File tempDir = new File(tempDirPath);
			tempDir.mkdirs();
			File propsFile = new File(tempDir.getAbsolutePath() + "/"
					+ Math.random() + ".properties");
			Properties props = new Properties();
			props.putAll(model.getStateMap());
			props.store(new FileOutputStream(propsFile),
					"Temporary Properties File");

			File buildFile = new File(buildFilePath);
			if (!buildFile.exists()) {
				throw new RuntimeException("Build file doesn't exist: "
						+ buildFilePath);
			}
			File baseDir = buildFile.getParentFile();
			Map<String, String> env = new HashMap<String, String>(System
					.getenv());

			if(this.environment == null){
				this.environment = new HashMap<String,String>();
			}
			if(!this.environment.containsKey("JAVA_HOME")){
				this.environment.put("JAVA_HOME", InstallerUtils.getJavaHomePath());
			}
			
			for (Iterator i = this.environment.entrySet().iterator(); i
					.hasNext();) {
				Entry entry = (Entry) i.next();
				if (entry.getKey() instanceof String
						&& entry.getValue() instanceof String) {
					env.put((String) entry.getKey(), (String) entry.getValue());
				}
			}
			
			Map<String, String> myEnv = new HashMap<String, String>(env);
			String[] envp = new String[myEnv.size()];
			int i = 0;
			for (String key : myEnv.keySet()) {
				envp[i++] = key + "=" + myEnv.get(key);
			}

			runAnt(model, baseDir, buildFilePath, this.target,
					this.systemProperties, envp, propsFile.getAbsolutePath());

		} catch (Exception ex) {
			throw new RuntimeException("Error encountered: " + ex.getMessage(),
					ex);
		}
		return null;
	}
	
	private String createClasspath(String ... elements){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < elements.length; i++){
			sb.append(elements[i]);
			if(i + 1 < elements.length){
				sb.append(InstallerUtils.isWindows() ? ";" : ":");
			}
		}
		return sb.toString();
	}

	protected void runAnt(CaGridInstallerModel model, File dir, String buildFile,
			String target, Properties sysProps, String[] envp,
			String propertiesFile) throws IOException, InterruptedException {

		// Check it tools.jar is available
		File toolsJar = new File(this.environment.get("JAVA_HOME")
				+ "/lib/tools.jar");
		if (!toolsJar.exists()) {
			logger.info("tools.jar not found at '" + toolsJar.getAbsolutePath()
					+ "'. Using packaged tools.jar");
			toolsJar = new File("lib/tools.jar");
		}

		// build command
		ArrayList<String> cmd = new ArrayList<String>();
		String antHome = model.getProperty(Constants.ANT_HOME);

		String java = "java";
		if (InstallerUtils.isWindows()) {
			java += ".exe";
		}
		cmd.add(InstallerUtils.getJavaHomePath() + "/bin/" + java);
		cmd.add("-classpath");
		

		String cp = createClasspath(toolsJar.getAbsolutePath(), antHome
				+ "/lib/ant-launcher.jar");
		
		cmd.add(cp);

		cmd.add("-Dant.home=" + antHome);

		// add system properties
		if (sysProps != null) {
			Enumeration keys = sysProps.keys();
			while (keys.hasMoreElements()) {
				String name = (String) keys.nextElement();
				String value = (String) sysProps.getProperty(name);
				if (!InstallerUtils.isWindows()) {
					value = value.replaceAll(" ", "\\\\ ");
				}
				cmd.add("-D" + name + "=" + value + "");
			}
		}

		cmd.add("org.apache.tools.ant.launch.Launcher");

		// add build file
		if (buildFile != null) {
			cmd.add("-buildfile");
			cmd.add(buildFile);
		}

		if (propertiesFile != null) {
			cmd.add("-propertyfile");
			cmd.add(propertiesFile);
		}

		// add target
		if (target != null) {
			cmd.add(target);
		}

		StringBuilder sb = new StringBuilder();
		for (String s : cmd) {
			sb.append(s).append(" ");
		}
		logger.info("Executing Ant: " + sb);

		// run ant
		Process p = Runtime.getRuntime().exec(cmd.toArray(new String[0]), envp,
				dir);
		// track stdout and stderr
		StringBuffer stdout = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		new IOThread(p.getInputStream(), System.out, stdout).start();
		new IOThread(p.getErrorStream(), System.err, stderr).start();

		// wait and return
		int code = p.waitFor();
		if (code != 0 ||
				stdout.indexOf("BUILD FAILED") != -1
				|| stderr.indexOf("BUILD FAILED") != -1
				|| stdout.indexOf("Build failed") != -1
				|| stderr.indexOf("Build failed") != -1) {
			
			logger.info("Code: " + code);
			logger.info("STDOUT: " + stdout);
			logger.info("STDERR: " + stderr);
			
			throw new IOException("ant command '" + target + "' failed");
		}
	}

}
