package org.cagrid.identifiers.resolver;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
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
import org.cagrid.identifiers.client.Util;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.UnexpectedIdentifiersException;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.NamingAuthorityConfig;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Resolver {
	
	private XMLContext xmlContext = null;
	private ApplicationContext appCtx = null;
    
	public Resolver() {
		init( new String[] {Util.DEFAULT_SPRING_CONTEXT_RESOURCE} );
	}
	
	public Resolver( String[] springCtxList ) {
		init( springCtxList );
	}
	
	private void init(String[] springCtxList) {
		appCtx = new ClassPathXmlApplicationContext( springCtxList );
        xmlContext = (XMLContext) appCtx.getBean(Util.CASTOR_CONTEXT_BEAN);	
	}

	private String getResponseString( HttpResponse response ) throws IOException {
		
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
	
	private DefaultHttpClient getHttpClient() {
		
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
	
	private void checkXMLResponse(HttpResponse response, String errMsg) throws HttpException {
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
	
	private String httpGet(URI url, String errMsg) throws HttpException, IOException {
	
		DefaultHttpClient client = getHttpClient();
		HttpGet method = new HttpGet( url );
     	    
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
		
	private NamingAuthorityConfig retrieveNamingAuthorityConfig( URI identifier ) 
		throws URISyntaxException, IOException, HttpException, MappingException, MarshalException, ValidationException {
		
		URI configUrl = new URI(identifier.toString() + "?config");
		
		String naConfigStr = httpGet(configUrl, "Unable to retrieve naming authority configuration from " + configUrl);
			
		// Deserialize response
		Unmarshaller unmarshaller = xmlContext.createUnmarshaller();
		unmarshaller.setClass(NamingAuthorityConfig.class);
		
		return (NamingAuthorityConfig) 
			unmarshaller.unmarshal(new StringReader(naConfigStr));
	}
	
	public IdentifierValues resolveGrid( URI identifier ) 
		throws 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			UnexpectedIdentifiersException {
		
		try {
			NamingAuthorityConfig config = retrieveNamingAuthorityConfig( identifier );

			IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( config.getGridSvcUrl() );

			return gov.nih.nci.cagrid.identifiers.common.IdentifiersNAUtil.map(
					client.resolveIdentifier(new org.apache.axis.types.URI(identifier.toString())) );

		} catch (NamingAuthorityConfigurationFault e) {
			throw new NamingAuthorityConfigurationException(e);
		} catch (InvalidIdentifierFault e) {
			throw new InvalidIdentifierException(e);
		} catch (NamingAuthoritySecurityFault e) {
			throw new NamingAuthoritySecurityException(e);
		} catch (Exception e) {
			throw new UnexpectedIdentifiersException(e);
		}
	}
	
	public IdentifierValues resolveHttp( URI identifier ) 
		throws HttpException, IOException, MarshalException, MappingException, ValidationException {
		
		//
		// Resolve identifier
		//
		String iValuesStr = httpGet(identifier, "Identifier [" + identifier + "] failed resolution");
		
		//Deserialize the response
		Unmarshaller unmarshaller = xmlContext.createUnmarshaller();
		unmarshaller.setClass(IdentifierValues.class);
		
		return (IdentifierValues) unmarshaller.unmarshal(new StringReader(iValuesStr));
	}
}
