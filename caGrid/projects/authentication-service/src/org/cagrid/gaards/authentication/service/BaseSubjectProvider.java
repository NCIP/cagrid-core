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
