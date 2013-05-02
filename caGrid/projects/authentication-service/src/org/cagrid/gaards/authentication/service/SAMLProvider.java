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

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import javax.security.auth.Subject;

import org.cagrid.gaards.authentication.common.InsufficientAttributeException;

public interface SAMLProvider {
    SAMLAssertion getSAML(Subject subject) throws InsufficientAttributeException;
}
