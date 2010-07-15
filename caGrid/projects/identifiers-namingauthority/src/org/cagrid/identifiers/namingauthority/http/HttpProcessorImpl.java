package org.cagrid.identifiers.namingauthority.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.namingauthority.HttpProcessor;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.impl.SecurityInfoImpl;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.globus.axis.gsi.GSIConstants;


public class HttpProcessorImpl implements HttpProcessor {

	protected static Log LOG = LogFactory.getLog(HttpProcessorImpl.class.getName());
    private NamingAuthority namingAuthority;
    private Marshaller serializer;

    public static String ERR_HDR = "There was an error processing your request.";
    
    public static String HTTP_ACCEPT_HDR = "Accept";
    public static String HTTP_ACCEPT_HTML = "text/html";
    public static String HTTP_ACCEPT_XML = "application/xml";
    public static String HTTP_ACCEPT_ANY = "*/*";

    public void setNamingAuthority(NamingAuthority na) {
        this.namingAuthority = na;
    }
    
    public NamingAuthority getNamingAuthority() {
    	return namingAuthority;
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


    public String htmlResponse(URI uri, IdentifierData ivs) throws NamingAuthorityConfigurationException {
        StringBuffer msg = new StringBuffer();

        if (ivs == null) {
            msg.append("<h2>Local identifier [" + uri + "] could not be found</h2>\n");
        } else {
            msg.append("<h3>" + IdentifierUtil.build(namingAuthority.getConfiguration().getNaPrefixURI(), uri)
                + "</h3>\n<hr>\n");

            for (String key : ivs.getKeys()) {
                msg.append("<b>Key: &nbsp;</b>" + key + "<br>\n");
                KeyData kd = ivs.getValues(key);
                if (kd.getPolicyIdentifier() != null) {
                	msg.append("<b>Policy Identifier: &nbsp;</b>");
                	msg.append(kd.getPolicyIdentifier().normalize().toString());
                }
                msg.append("<br>\n");
                
                if (kd.getValues() != null) {
                	for (String value : kd.getValues()) {
                		msg.append("<b>Key Data: &nbsp;</b>" + escape(value) + "<br>\n");
                	}
                }
                msg.append("<hr>\n");
            }
        }

        return msg.toString();
    }

    protected String makeErrStr(String msg, Throwable t) {
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(ERR_HDR);
    	
    	if (t != null) {
    		sb.append("\n")
    			.append(IdentifierUtil.getStackTrace(t))
    			.append("\n");
    	}
    	
    	return sb.toString();
    }
    
    protected String makeHtmlErrStr(String msg, Throwable t) {
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append("<H2>").append(ERR_HDR).append("</H2>")
    		.append("<H3>").append(msg).append("</H3>");
    	
    	if (t != null) {
    		sb.append("<hr>")
    			.append("<br>")
    			.append(t.toString())
    			.append("<br>")
    			.append(IdentifierUtil.getStackTrace(t));
    	}
    	
    	String outStr = sb.toString().replace("\n", "<br>");
    	return outStr;
    }
    
    protected String makeErrStr( boolean xml, String msg, Throwable t ) {
    	if (xml) {
    		return makeErrStr(msg, t);
    	}
    	return makeHtmlErrStr(msg, t);
    }
    
    protected String makeErrStr( boolean xml, String msg ) {
    	if (xml) {
    		return makeErrStr(msg, null);
    	}
    	return makeHtmlErrStr(msg, null);
    }
    
    protected String serialize( Object ivs ) throws IOException, MarshalException, ValidationException {
		Writer out = new StringWriter();
        serializer.setWriter(out);
        serializer.marshal(ivs);
        
        return out.toString();
	}

    protected URI getServletURI(HttpServletRequest req) throws URISyntaxException {
    	String servletURL = req.getRequestURL().toString();
    	String servletPath = req.getServletPath();
    	
    	return new URI(servletURL.substring(0, 
    			servletURL.indexOf(servletPath) + servletPath.length()) 
    			+ "/");
    }
    
    protected String serializeNAConfiguration(URI servletURI) 
    	throws MarshalException, ValidationException, IOException {
    	
    	namingAuthority.getConfiguration().setNaBaseURI(servletURI);
    	return serialize(namingAuthority.getConfiguration());
    }
    
    protected String htmlNAConfiguration(URI servletURI) 
		throws MarshalException, ValidationException, IOException {
	
    	namingAuthority.getConfiguration().setNaBaseURI(servletURI);
    	
    	StringBuilder sb = new StringBuilder("<DL>");
    	sb.append("<DT><STRONG>Naming Authority</STRONG></DT>\n<DD>")
    		.append(namingAuthority.getConfiguration().getNaBaseURI())
    		.append("</DD><BR>\n<DT><STRONG>Naming Authority Grid Service</STRONG></DT>\n<DD>" )
    		.append(namingAuthority.getConfiguration().getNaGridSvcURI().toString())
    		.append("</DD><BR>\n<DT><STRONG>Naming Authority Prefix</STRONG></DT>\n<DD>" )
    		.append(namingAuthority.getConfiguration().getNaPrefixURI().toString())
    		.append("</DD></DL>");
    				
    	return sb.toString();
    }
    
    public void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuffer msg = new StringBuffer();
        int responseStatus = HttpServletResponse.SC_OK;
        
        SecurityInfoImpl secInfo = new SecurityInfoImpl(
        		(String) request.getAttribute(GSIConstants.GSI_USER_DN));
        
        //
        // ?config causes to ignore resolution and
        // return naming authority configuration
        // instead
        //
        String config = request.getParameter("config");
        if (config != null) {
        	try {
        		if (xmlResponseRequired(request)) {
        			msg.append(serializeNAConfiguration(getServletURI(request)));
        			response.setContentType(HTTP_ACCEPT_XML);
        		} else {
        			msg.append(htmlNAConfiguration(getServletURI(request)));
        			response.setContentType(HTTP_ACCEPT_HTML);
        		}
        	} catch( Exception e ) {
        		msg.append(makeErrStr("Server error while serializing naming authority's configuration", e));
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
                msg.append(makeErrStr(xmlResponse, "No identifier provided"));
                responseStatus = HttpServletResponse.SC_BAD_REQUEST;
            } else {

                IdentifierData ivs = null;
                try {
                    ivs = (IdentifierData) namingAuthority.resolveIdentifier(secInfo, IdentifierUtil.build(namingAuthority
                        .getConfiguration().getNaPrefixURI(), uri));
                    
                    if (xmlResponse) {
                        msg.append(serialize(ivs));
                        response.setContentType(HTTP_ACCEPT_XML);
                    } else {
                        msg.append(htmlResponse(uri, ivs));
                        response.setContentType(HTTP_ACCEPT_HTML);
                    }
                } catch (InvalidIdentifierException e) {
                	responseStatus = HttpServletResponse.SC_NOT_FOUND;
                	String error = makeErrStr(xmlResponse, "Invalid Identifier Exception", e);
                    LOG.error(error);
                    msg.append(error);
                    
                } catch (NamingAuthorityConfigurationException e) {
                	responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                	String error = makeErrStr(xmlResponse, "Naming Authority Configuration Exception", e);
                	LOG.error(error);
                    msg.append(error);
                    
                } catch (NamingAuthoritySecurityException e) {
                	responseStatus = HttpServletResponse.SC_FORBIDDEN;
                	String error = makeErrStr(xmlResponse, "Naming Authority Security Exception", e);
                	LOG.error(error);
                    msg.append(error);
                    
                } catch (Exception e) {
                	responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                	String error = makeErrStr(xmlResponse, "Unexpected system error", e);
                	LOG.error(error);
                	msg.append(error);
                	
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
