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
package org.cagrid.websso.client.acegi.logout;

import java.io.IOException;
import java.util.Properties;

import org.cagrid.websso.common.WebSSOClientHelper;

import org.springframework.core.io.Resource;

public class SingleSignoutHelper{
	
	private Resource casClientResource;
	
	public SingleSignoutHelper(Resource casClientResource) {
		this.casClientResource = casClientResource;
	}
	
	public String getLogoutURL() {
		Properties properties = new Properties();
		try {
			properties.load(casClientResource.getInputStream());
			return WebSSOClientHelper.getLogoutURL(properties);			
		} catch (IOException e) {
			throw new RuntimeException("error occured handling logout " + e);
		}
	}	
}
