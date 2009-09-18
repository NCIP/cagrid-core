package org.cagrid.identifiers.namingauthority.http;

import org.cagrid.identifiers.namingauthority.NamingAuthority;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.http.*;

public class HttpServer implements Runnable {
	
	private NamingAuthority namingAuthority;
	

	private int _port;
	
	public HttpServer(NamingAuthority na, int port) {
		this.namingAuthority = na;
		_port = port;
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	public void run() {
		Handler handler=new AbstractHandler()
		{
		    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
		        throws java.io.IOException, javax.servlet.ServletException
		    {
		    	namingAuthority.processHttpRequest(request, response);
		        ((Request)request).setHandled(true);
		    }
		};
		
		Server server = new Server( _port );
		server.setHandler(handler);
		try {
			server.start();
			
			System.out.println("HTTP Jetty Server Started on port " + _port);
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to start Jetty HTTP Server: " + e);
			e.printStackTrace();
		}
	}
}
