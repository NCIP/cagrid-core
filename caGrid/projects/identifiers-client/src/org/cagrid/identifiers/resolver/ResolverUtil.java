package org.cagrid.identifiers.resolver;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.http.NamingAuthorityConfig;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;


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
	
	private static Unmarshaller getDeserializer() throws IOException, MappingException {
		URL mappingResource = ResolverUtil.class.getClassLoader()
        	.getResource("/org/cagrid/identifiers/namingauthority/na-castor-mapping.xml");
		
		Mapping mapping = new Mapping();
		mapping.loadMapping(new InputSource(mappingResource.openStream()));
		
		XMLContext context = new XMLContext();
		context.addMapping(mapping);
		
		return context.createUnmarshaller();
	}
	
	private static String httpGet(URI url, String errMsg) throws HttpException, IOException {
		
		Object dataObject;
		
		DefaultHttpClient client = getHttpClient();

		HttpGet method = new HttpGet( url );
	      
	    NamingAuthorityConfig config = null;
	    
	    try {
	    	//System.out.println("Connecting to " + url);
	    	
	    	HttpResponse response = client.execute( method );
	    	
	    	checkXMLResponse(response, errMsg);
	    	
	    	return getResponseString(response);
					
	    } finally {
	         // Release the connection.
	         method.abort();
	         client.getConnectionManager().shutdown();
	    }  
	}
		
//	public static IdentifierValues convert( KeyValues[] tvsArr ) {
//		if (tvsArr == null)
//			return null;
//		
//		IdentifierValues ivs = new IdentifierValues();
//		
//		for( KeyValues tvs : tvsArr ) {
//			String key = tvs.getKey();
//			Values values = tvs.getValues();
//			for( String value : values.getValue() ) {
//				ivs.add(key, value);
//			}
//		}
//		
//		return ivs;
//	}
	
	private static NamingAuthorityConfig retrieveNamingAuthorityConfig( URI identifier ) 
		throws URISyntaxException, IOException, HttpException, MappingException, MarshalException, ValidationException {
		
		URI configUrl = new URI(identifier.toString() + "?config");
		
		String naConfigStr = httpGet(configUrl, "Unable to retrieve naming authority configuration from " + configUrl);
			
		// Deserialize response
		Unmarshaller unmarshaller = getDeserializer();
		unmarshaller.setClass(NamingAuthorityConfig.class);
		
		return (NamingAuthorityConfig) 
			unmarshaller.unmarshal(new StringReader(naConfigStr));
	}
	
	public static IdentifierValues resolveGrid( URI identifier ) throws Exception {
		
		NamingAuthorityConfig config = retrieveNamingAuthorityConfig( identifier );
		
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( config.getGridSvcUrl() );
		
		return gov.nih.nci.cagrid.identifiers.common.MappingUtil.map(
				client.resolveIdentifier(new org.apache.axis.types.URI(identifier.toString())) );
	}
	
	public static IdentifierValues resolveHttp( URI identifier ) 
		throws HttpException, IOException, MarshalException, MappingException, ValidationException {
		
		//
		// Resolve identifier
		//
		String iValuesStr = httpGet(identifier, "Identifier [" + identifier + "] failed resolution");
		
		//Deserialize the response
		Unmarshaller unmarshaller = getDeserializer();
		unmarshaller.setClass(IdentifierValues.class);
		
		return (IdentifierValues) unmarshaller.unmarshal(new StringReader(iValuesStr));
	}
}
