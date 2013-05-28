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
package org.cagrid.gaards.authentication.test.system.steps;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseAuthenticationOutcome implements
		AuthenticationOutcome {

	private Log log;

	public BaseAuthenticationOutcome() {
		this.log = LogFactory.getLog(this.getClass().getName());
	}

	public Log getLog() {
		return log;
	}

}
