package org.cagrid.services.webapp.servlet.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisEngine;
import org.apache.axis.configuration.DirProvider;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.server.AxisServer;


public class DefaultServiceServlet extends HttpServlet {
    protected static final String ATTR_AXIS_ENGINE = "AxisEngine";


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AxisEngine engine = retrieveEngine(this);
        ServletContext servletContext = getServletConfig().getServletContext();

        String pathInfo = request.getPathInfo();
        String realpath = servletContext.getRealPath(request.getServletPath());
        if (realpath == null) {
            realpath = request.getServletPath();
        }
        String serviceName;
        if (pathInfo.startsWith("/")) {
            serviceName = pathInfo.substring(1);
        } else {
            serviceName = pathInfo;
        }

        // look up the service and make sure it exists
        SOAPService s = engine.getService(serviceName);
        

        WSDDService service = null;
        WSDDService[] services = ((DirProvider) engine.getConfig()).getDeployment().getServices();
        for (int i = 0; i < services.length; i++) {
            if (services[i].getQName().getLocalPart().equals(serviceName)) {
                service = services[i];
                break;
            }
        }

        PrintWriter writer = response.getWriter();
        response.setContentType("text/html; charset=utf-8");
        writer.println("<html>");
        writer.println("<h1>" + serviceName + "</h1>");

        // generate the html for all the operations
        List list = service.getServiceDesc().getOperations();
        Iterator it = list.iterator();

        writer.println("<h2>Operations</h2>");
        writer.println("<DL>");
        while (it.hasNext()) {

            OperationDesc desc = (OperationDesc) it.next();
            writer.print("<DT>");
            writer.print(desc.getName());
            writer.println("</DT>");
            writer.println("<DD>");
            writer.println("</DD>");
            
        }
        writer.println("</DL>");

        writer.println("</html>");
    }


    /**
     * Get an engine from the servlet context; robust againt serialization
     * issues of hot-updated webapps. Remember than if a webapp is marked as
     * distributed, there is more than 1 servlet context, hence more than one
     * AxisEngine instance
     * 
     * @param servlet
     * @return the engine or null if either the engine couldnt be found or the
     *         attribute wasnt of the right type
     */
    private static AxisServer retrieveEngine(HttpServlet servlet) {
        Object contextObject = servlet.getServletContext().getAttribute(servlet.getServletName() + ATTR_AXIS_ENGINE);
        if (contextObject == null) {
            // if AxisServer not found :
            // fall back to the "default" AxisEngine
            contextObject = servlet.getServletContext().getAttribute(ATTR_AXIS_ENGINE);
        }
        if (contextObject instanceof AxisServer) {
            AxisServer server = (AxisServer) contextObject;
            // if this is "our" Engine
            if (servlet.getServletName().equals(server.getName())) {
                return server;
            }
            return null;
        } else {
            return null;
        }
    }

}
