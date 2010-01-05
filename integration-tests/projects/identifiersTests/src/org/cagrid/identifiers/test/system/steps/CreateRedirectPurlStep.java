package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;




public class CreateRedirectPurlStep extends Step {
	
	private IdentifiersTestInfo testInfo;
	
	public CreateRedirectPurlStep(IdentifiersTestInfo info) {
		this.testInfo = info;
	}
	

	@Override
	public void runStep() throws Exception {

		DefaultHttpClient client = new DefaultHttpClient();
		
		client.getCookieStore().addCookie(testInfo.getPurlzLoginCookie());

		URI url = new URI("http://localhost:" + 
				testInfo.getPurlzPort() + 
				"/admin/purl" +
				IdentifiersTestInfo.PURLZ_TESTDOMAIN_ID);
		
		HttpPost method = new HttpPost(url);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("type", "partial"));
		params.add(new BasicNameValuePair("target", testInfo.getNamingAuthorityURI()));
		params.add(new BasicNameValuePair("maintainers", "admin"));
	
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		method.setEntity(entity);
 	    
	    try {
	    	System.out.println("CreateRedirectPurlStep::runStep connecting to " + url);
	    	
	    	HttpResponse response = client.execute( method );
	    	int statusCode = response.getStatusLine().getStatusCode();
	    	
	    	System.out.println("CreateRedirectPurlStep: HTTP Status code: " + statusCode);
	    
	    	if (statusCode != HttpStatus.SC_CREATED) {
	    		throw new HttpException("CreateRedirectPurlStep failed [" + 
	    				statusCode + ":" + response.getStatusLine().toString() + 
	    				"] Response [" + IdentifiersTestInfo.getResponseString(response) + "]");
	    	}
	    	
	    	String responseStr = IdentifiersTestInfo.getResponseString(response);
	    	System.out.println("CreateRedirectPurlStep RESPONSE===============["+responseStr+"]");
	    } finally {
	         // Release the connection.
	         method.abort();
	         client.getConnectionManager().shutdown();
	    }  
	}
}
