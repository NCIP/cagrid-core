/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package org.cagrid.axis.transport;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.axis.MessageContext;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.SocketHolder;
import org.cagrid.proxy.BetterProxyPathValidator;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.proxy.ProxyPathValidatorException;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 * <BR><I>This code is based on Axis HTTPSender.java code.</I>
 */
public class HTTPSSender extends HTTPSender {

    protected void getSocket(SocketHolder sockHolder,
                             MessageContext msgContext,
                             String protocol,
                             String host, int port, int timeout, 
                             StringBuffer otherHeaders, 
                             BooleanHolder useFullURL)
        throws Exception {

        if (!protocol.equalsIgnoreCase("https")) {
	    throw new IOException("Invalid protocol");
	}

        int lport = (port == -1) ? 8443 : port;

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
        SSLContext.setDefault(ctx);
        
        SSLSocketFactory factory = ctx.getSocketFactory();
        Socket socket = factory.createSocket(host, lport);

        sockHolder.setSocket(socket);
    }
    
    private static class DefaultTrustManager implements X509TrustManager {
    	
    	TrustedCertificates trusted = null;
    	
    	public DefaultTrustManager() {
    		trusted = TrustedCertificates.getDefaultTrustedCertificates();	
    	}

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        	throw new CertificateException("Unimplemented Method");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        	BetterProxyPathValidator validator = new BetterProxyPathValidator();
			try {
				validator.validate(arg0, trusted.getCertificates(), null);
			} catch (ProxyPathValidatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//        	System.out.println("checkServerTrusted");        	
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
//        	System.out.println("getAcceptedIssuers");
        	return null;
        }

    }

}