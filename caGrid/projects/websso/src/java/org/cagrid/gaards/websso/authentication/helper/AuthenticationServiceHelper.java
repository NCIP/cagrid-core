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
package org.cagrid.gaards.websso.authentication.helper;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.websso.exception.AuthenticationErrorException;
import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;

public interface AuthenticationServiceHelper {

	public SAMLAssertion authenticate(String authenticationServiceURL,Credential credential)
			throws AuthenticationErrorException,
			AuthenticationConfigurationException;
}
