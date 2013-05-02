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
