package org.cagrid.identifiers.resolver;

import gov.nih.nci.cagrid.identifiers.TypeValues;
import gov.nih.nci.cagrid.identifiers.TypeValuesMap;
import gov.nih.nci.cagrid.identifiers.Values;
import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.cagrid.identifiers.namingauthority.http.NamingAuthorityConfig;
import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;


public class ResolverUtil {

	//
	// Given the naming authority URL, it returns the naming
	// authority configuration object.
	//
	public static NamingAuthorityConfig getNamingAuthorityConfig(String url) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod( url );
		method.setFollowRedirects(true);
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    NamingAuthorityConfig config = null;
	    
	    try {
	    	System.out.println("Connecting to " + url);
	    	
	    	int statusCode = client.executeMethod(method);
	    	
	    	if (statusCode != HttpStatus.SC_OK)
	    		throw new HttpException("Unable to retrieve naming authority config from " 
	    				+ url + " [" + statusCode + ":" + method.getStatusLine() + "]");
	    	
	    	Header ctHeader = method.getResponseHeader("Content-Type");
	    	if (ctHeader == null || ctHeader.getValue() == null || 
	    			ctHeader.getValue().indexOf("application/xml") == -1) {
	    		throw new HttpException("Unable to retrieve naming authority config from " 
	    				+ url + ". Response has no XML content.");
	    	}
	   
	    	String response = method.getResponseBodyAsString();

	    	// Deserialize response
			XMLDecoder decoder = new XMLDecoder(new StringBufferInputStream(
					response));
		    
		    config = (NamingAuthorityConfig)decoder.readObject();
		    decoder.close();
		    
		    if (config == null) {
		    	System.out.println("No data found for specified identifier");
		    	return null;
		    }
	    } finally {
	        // Release the connection.
	        method.releaseConnection();
	    }  
	    
	    return config;
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
		//String url = getNamingAuthorityURL( identifier );
		String configUrl = identifier + "?config";
		NamingAuthorityConfig config = getNamingAuthorityConfig( configUrl );
		if (config == null) {
			throw new HttpException("Unable to retrieve naming authority configuration from " + configUrl);
		}
		
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( config.getGridSvcUrl() );
		
		System.out.println("Connecting to " + config.getGridSvcUrl() + " to retrieve values for identifier " + identifier);
		return gov.nih.nci.cagrid.identifiers.common.MappingUtil.toIdentifierValues(
				client.getTypeValues(identifier) );
	}
	
	public static IdentifierValuesImpl resolveHttp( String identifier ) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		
		HttpMethod method = new GetMethod( identifier );
		
		method.setFollowRedirects(true);
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    method.addRequestHeader(new Header("Accept", "application/xml"));
	    
	    IdentifierValuesImpl ivs = null;
	    
	    try {
	    	System.out.println("Connecting to " + identifier);
	    	
	    	int statusCode = client.executeMethod(method);
	    	
	    	if (statusCode != HttpStatus.SC_OK) {
	    		throw new HttpException(identifier + " returned [" + statusCode + ":" + method.getStatusLine() + "]");
	        }
	    	
	    	Header ctHeader = method.getResponseHeader("Content-Type");
	    	if (ctHeader == null || ctHeader.getValue() == null || 
	    			ctHeader.getValue().indexOf("application/xml") == -1) {
	    		throw new HttpException(identifier + " returned no XML content (Content-Type: "
	    				+ (ctHeader != null ? ctHeader.getValue() : "null") + ")");
	    	}
	   
	    	String response = method.getResponseBodyAsString();
	    	System.out.println(response);
	    	// Deserialize response
			XMLDecoder decoder = new XMLDecoder(new StringBufferInputStream(
					response));
		    
		    ivs = (IdentifierValuesImpl)decoder.readObject();
		    decoder.close();
		    
		    if (ivs == null) {
		    	System.out.println("No data found for specified identifier");
		    	return null;
		    }
	    } finally {
	        // Release the connection.
	        method.releaseConnection();
	    }  
	    
	    return ivs;
	}
}
