package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
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




public class StartPurlStep extends Step {
    
	private IdentifiersTestInfo testInfo;

	public StartPurlStep(IdentifiersTestInfo info) {
		this.testInfo = info;
	}
	
	@Override
	public void runStep() throws Exception {
		
		String startup = testInfo.getPurlzDirectory().getAbsolutePath()
				+ File.separator + "bin" + File.separator;

		List<String> command = new ArrayList<String>();

		// executable to call
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			command.add("cmd");
			command.add("/c");
			command.add(startup + "startup.bat");
			command.add("run");
		} else {
			command.add(startup + "start.sh");
			command.add("run");
		}
        
		List<String> additionalEnvironment = new ArrayList<String>();
        additionalEnvironment.add("PURLZ_BASE_PATH="
				+ testInfo.getPurlzDirectory().getAbsolutePath());
        
 		String[] editedEnvironment = editEnvironment(additionalEnvironment);

        System.out.println("Command environment:\n");
		for (String e : editedEnvironment) {
			System.out.println(e);
		}
        
		String[] commandArray = command.toArray(new String[command.size()]);
		try {
			testInfo.purlzProcess = Runtime.getRuntime().exec(commandArray,
					editedEnvironment, testInfo.getPurlzDirectory());
			new StreamGobbler(testInfo.purlzProcess.getInputStream(),
					StreamGobbler.TYPE_OUT, System.out).start();
			new StreamGobbler(testInfo.purlzProcess.getErrorStream(),
					StreamGobbler.TYPE_OUT, System.err).start();
		} catch (Exception ex) {
			throw new Exception("Error invoking startup process: "
					+ ex.getMessage(), ex);
		}

		// start checking for running
		Exception testException = null;
		sleep(2000);
		boolean running = false;
		int wait = 60; //seconds
        long waitMs = wait * 1000;
        long totalTime = 0;
        int attempt = 1;
        while (!running && totalTime < waitMs) {
            long start = System.currentTimeMillis();
			System.out.println("Connection attempt " + (attempt));
			try {
				running = isPurlRunning();
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
				throw new ContainerException("Error starting PURLZ: "
						+ testException.getMessage(), testException);
			} else {
				throw new ContainerException("PURLZ non responsive after "
						+ wait + " seconds attempting to connect");
			}
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

			CookieStore store = client.getCookieStore();

			for (Cookie cookie : store.getCookies()) {
				if (cookie.getName().equalsIgnoreCase(IdentifiersTestInfo.PURLZ_LOGIN_COOKIE)) {
					testInfo.setPurlzLoginCookie(cookie);
					return true;
				}
			}
		} finally {
			// Release the connection.
			method.abort();
			client.getConnectionManager().shutdown();
		}  
		
		return false;
	}

}
