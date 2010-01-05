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




public class CreatePurlDomainStep extends Step {
	
	private IdentifiersTestInfo testInfo;
	
	public CreatePurlDomainStep(IdentifiersTestInfo info) {
		this.testInfo = info;
		
	}
	
	@Override
	public void runStep() throws Exception {

		DefaultHttpClient client = new DefaultHttpClient();

		client.getCookieStore().addCookie(testInfo.getPurlzLoginCookie());

		URI url = new URI("http://localhost:" + 
				testInfo.getPurlzPort() + 
				"/admin/domain" +
				IdentifiersTestInfo.PURLZ_TESTDOMAIN_ID);
		
		HttpPost method = new HttpPost(url);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", IdentifiersTestInfo.PURLZ_TESTDOMAIN_NAME));
		params.add(new BasicNameValuePair("public", Boolean.toString(true)));
		params.add(new BasicNameValuePair("maintainers", IdentifiersTestInfo.PURLZ_USER));
		params.add(new BasicNameValuePair("writers", IdentifiersTestInfo.PURLZ_USER));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		method.setEntity(entity);

		try {
			System.out.println("CreatePurlDomainStep::runStep connecting to " + url);
			
			HttpResponse response = client.execute( method );
			int statusCode = response.getStatusLine().getStatusCode();
			
			System.out.println("CreatePurlDomainStep: HTTP Status code: " + statusCode);
			
			if (statusCode != HttpStatus.SC_OK) {
				throw new HttpException("CreatePurlDomainStep failed [" + 
						statusCode + ":" + response.getStatusLine().toString() + 
						"] Response [" + IdentifiersTestInfo.getResponseString(response) + "]");
			}


			String responseStr = IdentifiersTestInfo.getResponseString(response);
			System.out.println("RESPONSE===============["+responseStr+"]");
		} finally {
			// Release the connection.
			method.abort();
			client.getConnectionManager().shutdown();
		}  
	}
}