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
package org.cagrid.gaards.authentication.service;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

public abstract class BaseSubjectProvider implements SubjectProvider{

	private Set<QName> profiles;
	
	public BaseSubjectProvider(){
		profiles = new HashSet<QName>();
	}
	
	public void addSupportedProfile(QName profile){
		profiles.add(profile);
	}
	
	public Set<QName> getSupportedAuthenticationProfiles() {
		return profiles;
	}

	
	
}
