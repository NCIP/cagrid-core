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
