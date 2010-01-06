package org.cagrid.identifiers.namingauthority.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.NamingAuthorityConfig;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;
import org.cagrid.identifiers.namingauthority.HttpProcessor;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;


import javax.servlet.http.*;


public class HttpProcessorImpl implements HttpProcessor {

    private NamingAuthority namingAuthority;
    private Marshaller serializer;

    public static String HTTP_ACCEPT_HDR = "Accept";
    public static String HTTP_ACCEPT_HTML = "text/html";
    public static String HTTP_ACCEPT_XML = "application/xml";
    public static String HTTP_ACCEPT_ANY = "*/*";

    public void setNamingAuthority(NamingAuthority na) {
        this.namingAuthority = na;
    }
    
    public void setSerializer( Marshaller aSerializer ) {
    	this.serializer = aSerializer;
    }

    public boolean xmlResponseRequired(HttpServletRequest req) {
        boolean htmlOk = false;
        boolean xmlOk = false;
        boolean anyOk = false;

        String resType = req.getHeader(HTTP_ACCEPT_HDR);

        System.out.println("ACCEPT[" + resType + "]");

        if (resType != null) {

            // I found that each browser specifies Accept header
            // differently (see below). For example, Safari specifies
            // application/xml as the first accepted format, so
            // obviously, if we just take into account the first
            // format, we'd always return XML to Safari, which is
            // undesired. So what we'll do is that we'll return
            // HTML unless it is not listed as one of the accepted
            // formats. "*/*" will also override XML. Client programs
            // other than browsers wishing to retrieve XML should list
            // XML only.
            //
            // Safari
            // application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
            //
            // Firefox
            // text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
            //
            // IE
            // */*

            String[] resTypeArr = resType.split(",");
            for (String type : resTypeArr) {
                if (type.contains(HTTP_ACCEPT_HTML))
                    htmlOk = true;
                else if (type.contains(HTTP_ACCEPT_ANY))
                    anyOk = true;
                else if (type.contains(HTTP_ACCEPT_XML))
                    xmlOk = true;
            }

            if (!htmlOk && !anyOk && xmlOk)
                return true;
        }

        return false;
    }


    public String htmlResponse(URI uri, IdentifierValues ivs) throws NamingAuthorityConfigurationException {
        StringBuffer msg = new StringBuffer();

        if (ivs == null) {
            msg.append("<h2>Local identifier [" + uri + "] could not be found</h2>\n");
        } else {
            msg.append("<h3>" + IdentifierUtil.build(namingAuthority.getConfiguration().getPrefix(), uri)
                + "</h3>\n<hr>\n");

            for (String key : ivs.getKeys()) {
                msg.append("<b>Type: &nbsp;</b>" + key + "<br>\n");
                for (String value : ivs.getValues(key)) {
                    msg.append("<b>Data: &nbsp;</b>" + escape(value) + "<br>\n");
                }
                msg.append("<hr>\n");
            }
        }

        return msg.toString();
    }

    protected String prepHtmlError( String msg, Exception e) {
    	StringBuffer sb = new StringBuffer( "<h2>There was an error processing your request</h2>");
    	sb.append("<h3>")
    		.append(msg)
    		.append("</h3>");
    	
    	if (e != null) {
    		StringWriter sw = new StringWriter();
    		e.printStackTrace(new PrintWriter(sw));
    		sb.append("<hr>")
    			.append("<br>")
    			.append(e.toString())
    			.append("<br>")
    			.append(sw.toString());
    	}
    	
    	String outStr = sb.toString().replace("\n", "<br>");
    	return outStr;
    }
    
    protected String prepHtmlError( String msg ) {
    	return prepHtmlError( msg, null );
    }
    
    protected String serialize( Object ivs ) throws IOException, MarshalException, ValidationException {
		Writer out = new StringWriter();
        serializer.setWriter(out);
        serializer.marshal(ivs);
        
        return out.toString();
	}

    protected String serializeNAConfiguration() throws MarshalException, ValidationException, IOException {
    	NamingAuthorityConfig config = new NamingAuthorityConfig();
    	config.setGridSvcUrl(namingAuthority.getConfiguration().getGridSvcUrl());
    	return serialize(config);
    }
    
    public void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuffer msg = new StringBuffer();
        int responseStatus = HttpServletResponse.SC_OK;
        
//        TODO: Authentication/Authorization checks
//        There are a bunch of 403.x codes that relate to authentication stuff... 
//        we should just use 403 for everything, rather than trying to use 401 
//        with a custom www-authenticate method. 
//        403 Substatus Error Codes for IIS 
//
//        * 403.1 - Execute access forbidden. 
//        * 403.2 - Read access forbidden. 
//        * 403.3 - Write access forbidden. 
//        * 403.4 - SSL required. 
//        * 403.5 - SSL 128 required. 
//        * 403.6 - IP address rejected. 
//        * 403.7 - Client certificate required. 
//        * 403.8 - Site access denied. 
//        * 403.9 - Too many users. 
//        * 403.10 - Invalid configuration. 
//        * 403.11 - Password change. 
//        * 403.12 - Mapper denied access. 
//        * 403.13 - Client certificate revoked. 
//        * 403.14 - Directory listing denied. 
//        * 403.15 - Client Access Licenses exceeded. 
//        * 403.16 - Client certificate is untrusted or invalid. 
//        * 403.17 - Client certificate has expired or is not yet valid. 

        //
        // ?config causes to ignore resolution and
        // return naming authority configuration
        // instead
        //
        String config = request.getParameter("config");
        if (config != null) {
        	try {
        		msg.append(serializeNAConfiguration());
        		response.setContentType(HTTP_ACCEPT_XML);
        	} catch( Exception e ) {
        		msg.append(prepHtmlError("Server error while serializing naming authority's configuration", e));
        		responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        	}
        } else {
            //
            // Specifying ?xml in the URL forces XML output
            // (Usefull for a user debugging from a web
            // browser)
            //
            boolean forceXML = false;
            if (request.getParameter("xml") != null) {
                forceXML = true;
            }

            boolean xmlResponse = forceXML || xmlResponseRequired(request);
        
            URI uri = URI.create(request.getPathInfo());

            if (uri == null || uri.getPath().length() <= 1 || !uri.getPath().startsWith("/")) {
                msg.append(prepHtmlError("No identifier provided"));
                responseStatus = HttpServletResponse.SC_BAD_REQUEST;
            } else {

                IdentifierValues ivs = null;
                try {
                    ivs = (IdentifierValues) namingAuthority.resolveIdentifier(IdentifierUtil.build(namingAuthority
                        .getConfiguration().getPrefix(), uri));
                    
                    if (xmlResponse) {
                        msg.append(serialize(ivs));
                        response.setContentType(HTTP_ACCEPT_XML);
                    } else {
                        msg.append(htmlResponse(uri, ivs));
                        response.setContentType(HTTP_ACCEPT_HTML);
                    }
                } catch (InvalidIdentifierException e) {
                    e.printStackTrace();
                    msg.append(prepHtmlError("Input identifier was not found in the system", e));
                    responseStatus = HttpServletResponse.SC_NOT_FOUND;
                } catch (NamingAuthorityConfigurationException e) {
                    e.printStackTrace();
                    msg.append(prepHtmlError("A configuration error has been detected", e));
                    responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                } catch (Exception e) {
                	e.printStackTrace();
                	msg.append(prepHtmlError("Unexpected system error", e));
                	responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                }
            }
        }

        if ( responseStatus != HttpServletResponse.SC_OK ) {
        	response.setContentType(HTTP_ACCEPT_HTML);
        }
        
        response.setStatus(responseStatus);
        response.getWriter().println(msg.toString());
    };


    private String escape(String inStr) {
        String outStr = inStr.replaceAll("<", "&lt;");
        outStr = outStr.replaceAll(">", "&gt;");
        return outStr;
    }
}
