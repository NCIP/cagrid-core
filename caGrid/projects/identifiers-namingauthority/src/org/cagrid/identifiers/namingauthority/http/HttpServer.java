package org.cagrid.identifiers.namingauthority.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cagrid.identifiers.namingauthority.HttpProcessor;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;


public class HttpServer implements Runnable {

    private HttpProcessor processor;

    private int _port;


    public HttpServer(HttpProcessor processor, int port) {
        this.processor = processor;
        _port = port;
    }


    public void start() {
        new Thread(this).start();
    }


    public void run() {
        Handler handler = new AbstractHandler() {
            public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
                throws java.io.IOException, javax.servlet.ServletException {
                processor.process(request, response);
                ((Request) request).setHandled(true);
            }
        };

        Server server = new Server(_port);
        server.setHandler(handler);
        try {
            server.start();

            System.out.println("HTTP Jetty Server Started on port " + _port);
            server.join();
        } catch (Exception e) {
            System.out.println("Failed to start Jetty HTTP Server: " + e);
            e.printStackTrace();
        }
    }
}
