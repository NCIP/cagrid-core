package org.cagrid.identifiers.resolver;

import gov.nih.nci.cagrid.identifiers.TypeValues;
import gov.nih.nci.cagrid.identifiers.TypeValuesMap;
import gov.nih.nci.cagrid.identifiers.Values;
import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;

import java.beans.XMLDecoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.rmi.RemoteException;

import javax.net.ssl.SSLContext;

import org.apache.axis.types.URI.MalformedURIException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cagrid.identifiers.namingauthority.http.NamingAuthorityConfig;
import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;


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
	
	private static Object httpGet(String url, String errMsg) throws HttpException, IOException {
		
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
		
	public static IdentifierValuesImpl convert( TypeValues[] tvsArr ) {
		if (tvsArr == null)
			return null;
		
		IdentifierValuesImpl ivs = new IdentifierValuesImpl();
		
		for( TypeValues tvs : tvsArr ) {
			String type = tvs.getType();
			Values values = tvs.getValues();
			for( String value : values.getValue() ) {
				ivs.add(type, value);
			}
		}
		
		return ivs;
	}
	
	public static IdentifierValuesImpl resolveGrid( String identifier ) throws HttpException, IOException {
		
		//
		// Retrieve Naming Authority Configuration
		//
		String configUrl = identifier + "?config";
		NamingAuthorityConfig config = (NamingAuthorityConfig)
			httpGet(configUrl, "Unable to retrieve naming authority configuration from " + configUrl);
				
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( config.getGridSvcUrl() );
		
		//System.out.println("Connecting to " + config.getGridSvcUrl() + " to retrieve values for identifier " + identifier);
		return gov.nih.nci.cagrid.identifiers.common.MappingUtil.toIdentifierValues(
				client.getTypeValues(identifier) );
	}
	
	public static IdentifierValuesImpl resolveHttp( String identifier ) throws HttpException, IOException {
		
		return (IdentifierValuesImpl)
			httpGet(identifier, "Identifier [" + identifier + "] failed resolution");
	}
}
