package org.cagrid.identifiers.namingauthority.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cagrid.identifiers.namingauthority.HttpProcessor;


public class NamingAuthorityService extends HttpServlet {
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
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("getPathInfo[" + request.getPathInfo() + "]");
        System.out.println("getQueryString[" + request.getQueryString() + "]");
        System.out.println("getRequestURI[" + request.getRequestURI() + "]");
        System.out.println("getRequestURL[" + request.getRequestURL() + "]");
        System.out.println("getServerName[" + request.getServerName() + "]");
        System.out.println("getServerPort[" + request.getServerPort() + "]");
        System.out.println("getServletPath[" + request.getServletPath() + "]");

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
