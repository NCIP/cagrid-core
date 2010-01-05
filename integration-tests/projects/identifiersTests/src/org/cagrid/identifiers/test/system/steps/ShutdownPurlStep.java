package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.ConnectException;
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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;




public class ShutdownPurlStep extends Step {
    
	private IdentifiersTestInfo testInfo;
	private String shutdownFileName;
	private String shutdownFilePath;
	
	public ShutdownPurlStep(IdentifiersTestInfo info) {
		this.testInfo = info;
		this.shutdownFileName = "shutdown.sh";
		this.shutdownFilePath = testInfo.getPurlzDirectory().getAbsolutePath() + 
			File.separator + "bin" + File.separator + shutdownFileName;
	}
	
	@Override
	public void runStep() throws Exception {
				
		String purlDir = testInfo.getPurlzDirectory().getName();

		List<String> command = new ArrayList<String>();

		// executable to call
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			//TODO
			throw new Exception("No shutdown strategy for windows yet");
		} else {
			writeUnixShutdownScript();
			command.add(shutdownFilePath);
		}
        
		List<String> additionalEnvironment = new ArrayList<String>();
 		String[] editedEnvironment = editEnvironment(additionalEnvironment);
		String[] commandArray = command.toArray(new String[command.size()]);
		try {
			
			Process killProc = Runtime.getRuntime().exec(commandArray,
					editedEnvironment);
			new StreamGobbler(killProc.getInputStream(),
					StreamGobbler.TYPE_OUT, System.out).start();
			new StreamGobbler(killProc.getErrorStream(),
					StreamGobbler.TYPE_OUT, System.err).start();
		} catch (Exception ex) {
			throw new Exception("Error invoking startup process: "
					+ ex.getMessage(), ex);
		}

		// start checking for running
		Exception testException = null;
		sleep(2000);
		boolean running = true;
		int wait = 60; //seconds
        long waitMs = wait * 1000;
        long totalTime = 0;
        int attempt = 1;
        while (running && totalTime < waitMs) {
            long start = System.currentTimeMillis();
			System.out.println("Connection attempt " + (attempt));
			try {
				running = isPurlRunning();
			} catch(ConnectException ex ) {
				running = false;
			} catch (Exception ex) {
				testException = ex;
				//ex.printStackTrace();
			}
			sleep(5000);
            attempt++;
            totalTime += (System.currentTimeMillis() - start);
		}
		if (running) {
			if (testException != null) {
				throw new ContainerException("Unable to shutdown PURLZ: "
						+ testException.getMessage(), testException);
			} else {
				throw new ContainerException("Unable to shutdown PURLZ after "
						+ wait + " seconds attempting");
			}
		}
		
		System.out.println("PURLZ is now shutdown...");
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
	
	private void writeUnixShutdownScript() throws Exception {
		
		StringBuffer content = new StringBuffer("#!/bin/bash\n");
		content.append("kill -9 `ps ax | grep \"com\\.ten60\\.netkernel\\.bootloader\\.BootLoader\" | grep \"ten60.pid=1\" | grep ")
			.append(testInfo.getPurlzDirectory().getName()) 
			.append(" | grep -v PID | awk '{printf $1}'`");
		
		File shutdownFile = new File(shutdownFilePath);
		Writer output = new BufferedWriter(new FileWriter(shutdownFile));
    	output.write(content.toString());
    	output.close();
    	
    	//make it executable
    	List<String> command = new ArrayList<String>();
	    command.add("chmod");
	    command.add("a+rwx");
	    command.add(shutdownFileName);

		String[] commandArray = command.toArray(new String[command.size()]);
		Process chmodProcess = null;
		try {
			chmodProcess = Runtime.getRuntime().exec(
					commandArray,
					null,
					new File(testInfo.getPurlzDirectory().getAbsolutePath(), "bin"));
			new StreamGobbler(chmodProcess.getInputStream(),
					StreamGobbler.TYPE_OUT,System.out).start();
			new StreamGobbler(chmodProcess.getErrorStream(),
					StreamGobbler.TYPE_OUT,System.err).start();
			chmodProcess.waitFor();
		} catch (Exception ex) {
			throw new Exception("Error invoking chmod process: "
					+ ex.getMessage(), ex);
		}
	}
	
	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	private boolean isPurlRunning() throws URISyntaxException, HttpException, ClientProtocolException, IOException {

		DefaultHttpClient client = new DefaultHttpClient();

		URI url = new URI("http://localhost:" + testInfo.getPurlzPort() + IdentifiersTestInfo.PURLZ_REST_LOGIN);
		HttpPost method = new HttpPost(url);

		List<NameValuePair> loginParams = new ArrayList<NameValuePair>();
		loginParams.add(new BasicNameValuePair("id", IdentifiersTestInfo.PURLZ_USER));
		loginParams.add(new BasicNameValuePair("passwd", IdentifiersTestInfo.PURLZ_PASSWORD));
		loginParams.add(new BasicNameValuePair("referrer", "/docs/index.html"));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(loginParams, "UTF-8");
		method.setEntity(entity);

		try {
			System.out.println("isPurlRunning connecting to " + url);
			HttpResponse response = client.execute( method );

			int statusCode = response.getStatusLine().getStatusCode();

			System.out.println("PURL Login: HTTP Status code: " + statusCode);

			if (statusCode != HttpStatus.SC_OK) {
				throw new HttpException(" [" + statusCode + ":" + response.getStatusLine().toString() + "]");
			}

			String responseStr = IdentifiersTestInfo.getResponseString(response);
			if (!responseStr.contains(IdentifiersTestInfo.PURLZ_WELCOME_MSG)) {
				System.out.println("BAD RESPONSE FROM SERVER [" + responseStr + "]");
				return false;
			}
			
			System.out.println("Login to PURL successful...");

		} finally {
			// Release the connection.
			method.abort();
			client.getConnectionManager().shutdown();
		}  
		
		return false;
	}

}
