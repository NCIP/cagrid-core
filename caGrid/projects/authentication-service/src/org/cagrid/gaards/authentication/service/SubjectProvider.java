package org.cagrid.gaards.authentication.service;

import java.util.Set;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.common.InvalidCredentialException;

public interface SubjectProvider {

	public Subject getSubject(Credential credential)
			throws InvalidCredentialException;

	public Set<QName> getSupportedAuthenticationProfiles();

}
