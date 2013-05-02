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
/**
 * 
 */
package org.cagrid.gaards.websso.authentication.helper;

import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;
import org.globus.gsi.GlobusCredential;

/**
 * @author modik [Kunal Modi - Ekagra Software Technologies Ltd.]
 * 
 */
public interface ProxyValidator {

	public boolean validate(GlobusCredential proxy)
			throws AuthenticationConfigurationException;
}
