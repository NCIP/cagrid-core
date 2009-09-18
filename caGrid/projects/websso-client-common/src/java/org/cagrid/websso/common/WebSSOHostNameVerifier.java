package org.cagrid.websso.common;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 *The Host Certificates returned Dorian contains CN=host/localhost 
 *instead of CN=localhost as expected by spring. Hence we need to disable 
 *strict hostname checking.
 */

public class WebSSOHostNameVerifier implements HostnameVerifier {

	public boolean verify(String s, SSLSession sslsession) {
		return true;
	}
}
