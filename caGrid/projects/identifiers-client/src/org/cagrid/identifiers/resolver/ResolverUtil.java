package org.cagrid.identifiers.resolver;

import gov.nih.nci.cagrid.identifiers.KeyValues;
import gov.nih.nci.cagrid.identifiers.Values;
import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;

import java.beans.XMLDecoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.cagrid.identifiers.namingauthority.IdentifierValues;
import org.cagrid.identifiers.namingauthority.http.NamingAuthorityConfig;


public class ResolverUtil {

	private static String getResponseString( HttpResponse response ) throws IOException {
		
		StringBuffer responseStr = new StringBuffer();
		
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader( entity.getContent() ));
			try {
				String line;
				while ( (line = reader.readLine()) != null ) {
					responseStr.append(line).append("\n");
				}
			} finally {
				reader.close();
			}
		}
		
		return responseStr.toString();
	}
	
	private static DefaultHttpClient getHttpClient() {
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		// I believe DefaultHttpClient handles redirects by default anyway
		client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS,
				Boolean.TRUE);
		
		client.addRequestInterceptor(new HttpRequestInterceptor() {   
			public void process(
					final HttpRequest request, 
                    final HttpContext context) throws HttpException, IOException {
				request.addHeader("Accept", "application/xml");
			}
		});
		
		SSLSocketFactory.getSocketFactory().setHostnameVerifier( SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
		
		return client;
	}
	
	private static void checkXMLResponse(HttpResponse response, String errMsg) throws HttpException {
		int statusCode = response.getStatusLine().getStatusCode();
    	if (statusCode != HttpStatus.SC_OK) {
    		throw new HttpException(errMsg + " [" + statusCode + ":" + response.getStatusLine().toString() + "]");
    	}
    	
    	Header ctHeader = response.getFirstHeader("Content-Type");
    	if (ctHeader == null || ctHeader.getValue() == null ||
    			ctHeader.getValue().indexOf("application/xml") == -1) {
    		throw new HttpException("Response has no XML content (Content-Type: "
    				+ (ctHeader != null ? ctHeader.getValue() : "null") + 
    				"). " + errMsg);
    	}
	}
	
	private static Object httpGet(URI url, String errMsg) throws HttpException, IOException {
		
		Object dataObject;
		
		DefaultHttpClient client = getHttpClient();

		HttpGet method = new HttpGet( url );
	      
	    NamingAuthorityConfig config = null;
	    
	    try {
	    	//System.out.println("Connecting to " + url);
	    	
	    	HttpResponse response = client.execute( method );
	    	
	    	checkXMLResponse(response, errMsg);
	    	
	    	// Deserialize response
			XMLDecoder decoder = new XMLDecoder(new StringBufferInputStream(
					getResponseString(response)));
					
		    dataObject = decoder.readObject();
		    if (dataObject == null) {
		    	throw new HttpException(errMsg + " [Deserialized object is null]");
		    }
		    decoder.close();

	    } finally {
	         // Release the connection.
	         method.abort();
	         client.getConnectionManager().shutdown();
	    }  
	    
	    return dataObject;
	}
		
	public static IdentifierValues convert( KeyValues[] tvsArr ) {
		if (tvsArr == null)
			return null;
		
		IdentifierValues ivs = new IdentifierValues();
		
		for( KeyValues tvs : tvsArr ) {
			String key = tvs.getKey();
			Values values = tvs.getValues();
			for( String value : values.getValue() ) {
				ivs.add(key, value);
			}
		}
		
		return ivs;
	}
	
	public static IdentifierValues resolveGrid( URI identifier ) throws Exception {//HttpException, IOException {
		
		//
		// Retrieve Naming Authority Configuration
		//
		URI configUrl = new URI(identifier.toString() + "?config");
		NamingAuthorityConfig config = (NamingAuthorityConfig)
			httpGet(configUrl, "Unable to retrieve naming authority configuration from " + configUrl);
				
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( config.getGridSvcUrl() );
		
		//System.out.println("Connecting to " + config.getGridSvcUrl() + " to retrieve values for identifier " + identifier);
		return gov.nih.nci.cagrid.identifiers.common.MappingUtil.toIdentifierValues(
				client.resolveIdentifier(new org.apache.axis.types.URI(identifier.toString())) );
	}
	
	public static IdentifierValues resolveHttp( URI identifier ) throws HttpException, IOException {
		
		return (IdentifierValues)
			httpGet(identifier, "Identifier [" + identifier + "] failed resolution");
	}
}
