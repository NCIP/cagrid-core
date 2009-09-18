
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.opensaml.SAMLResponse;

import java.io.ByteArrayInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;



public class OSUTest {
	
	private final static String IDP_URL = "https://authdev.it.ohio-state.edu/shibboleth-grid/GRID?providerId=dorian";

	public static void main(String[] args) {
		try{
			String userId = args[0];
			String password = args[1];
			Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
			HttpClient client = new HttpClient();
			client.getState().setCredentials(new AuthScope(null, 443, null),
				new UsernamePasswordCredentials(userId, password));
			GetMethod get = new GetMethod(IDP_URL);

			get.setDoAuthentication(true);

			try {
				// execute the GET
				int status = client.executeMethod(get);
				if (status == 401) {
					throw new Exception("Invalid Login Specified!!!");
				} else if (status > 200) {
					throw new Exception("Error authenticating with server. (" + status + ")");
				}

				// print the status and response
				System.out.println(status + "\n" + get.getResponseBodyAsString());
				SAMLResponse response = new SAMLResponse(new ByteArrayInputStream(get.getResponseBodyAsString()
					.getBytes()));
				//SAMLAssertion saml = new SAMLAssertion();
			} finally {
				// release any connection resources used by the method
				get.releaseConnection();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
