package org.cagrid.identifiers.namingauthority.http;

import java.io.IOException;

import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;
import org.cagrid.identifiers.namingauthority.HttpProcessor;

import javax.servlet.http.*;

public class HttpProcessorImpl implements HttpProcessor {
	
	private NamingAuthority namingAuthority;
	
	public static String HTTP_ACCEPT_HDR = "Accept";
	public static String HTTP_ACCEPT_HTML = "text/html";
	public static String HTTP_ACCEPT_XML = "application/xml";
	public static String HTTP_ACCEPT_ANY = "*/*";
		
	public void setNamingAuthority( NamingAuthority na ) {
		this.namingAuthority = na;
	}
	
	public boolean xmlResponseRequired(HttpServletRequest req) {
		boolean htmlOk = false;
		boolean xmlOk = false;
		boolean anyOk = false;
		
		String resType = req.getHeader(HTTP_ACCEPT_HDR);
		
		System.out.println("ACCEPT["+resType+"]");
		
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
			for( String type : resTypeArr ) {
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
	
	
	public String htmlResponse(String idStr, IdentifierValuesImpl ivs) {
		StringBuffer msg = new StringBuffer();
		
		if (ivs == null)
		{
			msg.append("<h2>Local identifier [" + idStr + "] could not be found</h2>\n");
		}
		else {
    		msg.append("<h3>" + IdentifierUtil.build(namingAuthority.getConfiguration().getPrefix(), idStr) + "</h3>\n<hr>\n");
    		
    		for( String type : ivs.getTypes()) {
    			msg.append("<b>Type: &nbsp;</b>" + type + "<br>\n");
            	for( String value : ivs.getValues(type) ) {
            		msg.append("<b>Data: &nbsp;</b>" + escape(value) + "<br>\n");
            	}
            	msg.append("<hr>\n");
            }
		}
		
		return msg.toString();
	}
	
	public String xmlResponse(IdentifierValuesImpl ivs) {
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(baos);
        encoder.writeObject(ivs);
        encoder.close();
      
        return baos.toString();
	}

	public String xmlConfigResponse() {
		NamingAuthorityConfig publicConfig = new NamingAuthorityConfig( namingAuthority.getConfiguration() );
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(baos);
        encoder.writeObject(publicConfig);
        encoder.close();
      
        return baos.toString();
	}
	
	public void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		StringBuffer msg = new StringBuffer();

		//
		// ?config causes to ignore resolution and
		// return naming authority configuration
		// instead
		//
		String config = request.getParameter("config");
		if (config != null) {
			msg.append(xmlConfigResponse());
			response.setContentType(HTTP_ACCEPT_XML);
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
			boolean noErrors = true;

			String uri = request.getPathInfo();
			System.out.println("URI["+uri+"]");
			String idStr = null;
			if (uri == null || uri.length() <= 1 || !uri.startsWith("/")) {
				msg.append("<h1>No identifier provided</h1>");
				noErrors = false;
				response.setContentType(HTTP_ACCEPT_HTML);
			} else {
				idStr = uri.substring(1);
			}

			if (noErrors) {

				IdentifierValuesImpl ivs = (IdentifierValuesImpl)namingAuthority.resolveIdentifier(idStr);

				if (xmlResponse) {
					msg.append(xmlResponse(ivs));
					response.setContentType(HTTP_ACCEPT_XML);
				} else {
					msg.append(htmlResponse(idStr, ivs));
					response.setContentType(HTTP_ACCEPT_HTML);
				}
			}
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(msg.toString());
	};
	
	private String escape(String inStr) {
		String outStr = inStr.replaceAll("<", "&lt;");
		outStr = outStr.replaceAll(">", "&gt;");
		return outStr;
	}
}
