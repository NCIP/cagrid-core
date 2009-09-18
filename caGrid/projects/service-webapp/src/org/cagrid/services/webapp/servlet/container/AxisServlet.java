package org.cagrid.services.webapp.servlet.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisEngine;
import org.apache.axis.configuration.DirProvider;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.handlers.soap.SOAPService;

/*
 * This is an overiding AxisServlet class of the globus servlet which 
 * overrides the axis servlet.  This servlet enables plain http requests to the 
 * AxisServlet which are directed at a service but not containing soap actions or 
 * axis specific service requests such as ?wsdl command or a query command
 * to be directed to a servlet designated by the service.  This enables the service
 * to also provide a web application with the service.
 * 
 * To designate a servlet for the service a service parameter needs to be added to the 
 * .wsdd file for the particular service.  Adding the servletClass parameter and setting
 * it's value to be the fully qualified classname of the servlet class you wish to be 
 * loaded and used for plain http get and posts to your services url.
 * 
 */
public class AxisServlet extends org.globus.wsrf.container.AxisServlet {

    private Map servletsMap = new HashMap();


    /*
     * Overloads the AxisServlet doGet to enable direct http requests to the service
     * to be forwared to a services servlet if one is provided.  This enable the service
     * developer to also deploy a web application with the service instead of always 
     * getting back the generic html page delivered back from the AxisServlet.  If no 
     * servlet is provided in the service than the request will be forward on to the AxisServlet
     * as normal.
     * 
     * (non-Javadoc)
     * @see org.apache.axis.transport.http.AxisServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        AxisEngine engine = getEngine();
        ServletContext servletContext = getServletConfig().getServletContext();

        String queryString = request.getQueryString();

        String pathInfo = request.getPathInfo();
        String realpath = servletContext.getRealPath(request.getServletPath());
        if (realpath == null) {
            realpath = request.getServletPath();
        }

        // make sure this is a web based call that is not asking for wsdl
        if (realpath != null && pathInfo != null && (queryString == null || !queryString.equals("wsdl"))) {

            String serviceName;
            if (pathInfo.startsWith("/")) {
                serviceName = pathInfo.substring(1);
            } else {
                serviceName = pathInfo;
            }

            // look up the service and make sure it exists
            SOAPService s = engine.getService(serviceName);

            if (s != null) {
                // check to see if this servlet has already been created.
                HttpServlet servlet = null;
                if (servletsMap.containsKey((serviceName))) {
                    servlet = (HttpServlet) servletsMap.get(serviceName);
                } else {
                    WSDDService[] services = ((DirProvider) engine.getConfig()).getDeployment().getServices();
                    WSDDService service = null;
                    for (int i = 0; i < services.length; i++) {
                        if (services[i].getQName().getLocalPart().equals(serviceName)) {
                            service = services[i];
                            break;
                        }
                    }

                    if (service != null) {
                        String servletClass = service.getParameter("servletClass");
                        if (servletClass != null) {
                            try {
                                servlet = (HttpServlet) Class.forName(servletClass).newInstance();
                                servlet.init(getServletConfig());
                                servlet.init();
                                servletsMap.put(serviceName, servlet);
                            } catch (Exception e) {
                                throw new ServletException(e.getMessage(), e);
                            }
                        }
                    }
                }

                // if a servlet exists for this service than invoke it
                if (servlet != null) {
                    servlet.service(request, response);
                } else {
                    response.getWriter().append("You have reached the " + serviceName + " service");

                }

                return;
            }
        }

        // else let the axis engine handle this request
        super.doGet(request, response);

    }


    /*
     * Overloads the doPost of the AxisServlet to forward posts to the servicesServlet if
     * so specified by having the url attribute of postToServiceServlet set to true in the
     * url request.
     * (non-Javadoc)
     * @see org.apache.axis.transport.http.AxisServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String caGridPost = (String) request.getParameter("postToServiceServlet");
        // make sure this is a web based call and not a SOAP call
        if (caGridPost != null && caGridPost.equals("true")) {
            AxisEngine engine = getEngine();
            ServletContext servletContext = getServletConfig().getServletContext();

            String pathInfo = request.getPathInfo();
            String realpath = servletContext.getRealPath(request.getServletPath());
            if (realpath == null) {
                realpath = request.getServletPath();
            }

            // make sure this is a web based call
            if (realpath != null && pathInfo != null) {

                String serviceName;
                if (pathInfo.startsWith("/")) {
                    serviceName = pathInfo.substring(1);
                } else {
                    serviceName = pathInfo;
                }

                // look up the service and make sure it exists
                SOAPService s = engine.getService(serviceName);

                if (s != null) {
                    // check to see if this servlet has already been created.
                    HttpServlet servlet = null;
                    if (servletsMap.containsKey((serviceName))) {
                        servlet = (HttpServlet) servletsMap.get(serviceName);
                    } else {
                        WSDDService[] services = ((DirProvider) engine.getConfig()).getDeployment().getServices();
                        WSDDService service = null;
                        for (int i = 0; i < services.length; i++) {
                            if (services[i].getQName().getLocalPart().equals(serviceName)) {
                                service = services[i];
                                break;
                            }
                        }

                        if (service != null) {
                            String servletClass = service.getParameter("servletClass");
                            if (servletClass != null) {
                                try {
                                    servlet = (HttpServlet) Class.forName(servletClass).newInstance();
                                    servlet.init(getServletConfig());
                                    servlet.init();
                                    servletsMap.put(serviceName, servlet);
                                } catch (Exception e) {
                                    throw new ServletException(e.getMessage(), e);
                                }
                            }
                        }
                    }

                    // if a servlet exists for this service than invoke it
                    if (servlet != null) {
                        servlet.service(request, response);
                    }
                }

            }
        } else {
            super.doPost(request, response);
        }
    }

}
