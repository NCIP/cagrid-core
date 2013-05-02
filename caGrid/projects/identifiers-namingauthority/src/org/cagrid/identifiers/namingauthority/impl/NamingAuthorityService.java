/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.identifiers.namingauthority.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.namingauthority.HttpProcessor;
import org.cagrid.identifiers.namingauthority.dao.IdentifierMetadataDao;
import org.globus.axis.gsi.GSIConstants;


public class NamingAuthorityService extends HttpServlet {
	protected static Log LOG = LogFactory.getLog(NamingAuthorityService.class.getName());
    private static final long serialVersionUID = 1L;
    private HttpProcessor processor;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public NamingAuthorityService() {
        super();
    }


    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        org.springframework.web.context.WebApplicationContext context = 
        	org.springframework.web.context.support.WebApplicationContextUtils
        		.getRequiredWebApplicationContext(getServletContext());
        
        this.processor = (HttpProcessor) context.getBean("httpProcessor", 
        		HttpProcessor.class);
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	LOG.debug("getPathInfo[" + request.getPathInfo() + "]");
        LOG.debug("getQueryString[" + request.getQueryString() + "]");
        LOG.debug("getRequestURI[" + request.getRequestURI() + "]");
        LOG.debug("getRequestURL[" + request.getRequestURL() + "]");
        LOG.debug("getServerName[" + request.getServerName() + "]");
        LOG.debug("getServerPort[" + request.getServerPort() + "]");
        LOG.debug("getServletPath[" + request.getServletPath() + "]");
        LOG.debug("User Identity[" + request.getAttribute(GSIConstants.GSI_USER_DN));

        processor.process(request, response);
    }


    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {

        System.out.println("doPost not implemented...");
    }

}
