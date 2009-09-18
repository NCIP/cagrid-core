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
