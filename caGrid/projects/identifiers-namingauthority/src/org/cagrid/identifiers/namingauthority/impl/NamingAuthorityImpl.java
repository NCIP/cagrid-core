package org.cagrid.identifiers.namingauthority.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cagrid.identifiers.namingauthority.*;
import org.cagrid.identifiers.namingauthority.IdentifierValues;
import org.cagrid.identifiers.namingauthority.util.Database;

import org.cagrid.identifiers.namingauthority.util.*;
import org.cagrid.identifiers.namingauthority.http.HttpProcessor;
import org.cagrid.identifiers.namingauthority.http.HttpServer;

public class NamingAuthorityImpl extends NamingAuthority {

	private HttpServer httpServer = null;
	private Database database;
	private HttpProcessor httpProcessor;
	
	public void initialize() {
		database.initialize();
		
		httpProcessor = new HttpProcessor(this);
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public void setDatabase( Database database ) {
		this.database = database;
	}
	
	// This starts a jetty http server for debugging/playing/whatever
	// purposes, not used in a live/production system
	public synchronized void startHttpServer() {
		if (httpServer == null) {
			httpServer = new HttpServer(this, 
					((NamingAuthorityConfigImpl)getConfiguration()).getHttpServerPort());
			httpServer.start();
		}
	}

	@Override
	public void processHttpRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		httpProcessor.processRequest(request, response);
	}

	@Override
	public String createIdentifier(IdentifierValues ivalues) throws Exception {
		IdentifierValuesImpl values = (IdentifierValuesImpl) ivalues;
		
		if (values == null)
			throw new Exception("Input IdentifierValues can't be null");
		
		String identifier = generateIdentifier();
		database.save(identifier, values);
        return IdentifierUtil.build(getConfiguration().getPrefix(), identifier);
	}

	@Override
	public IdentifierValuesImpl resolveIdentifier(Object identifier) {
		return database.getValues(
				IdentifierUtil.getLocalName( getConfiguration().getPrefix(), (String)identifier ));
	}
}
