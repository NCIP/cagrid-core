package org.cagrid.gaards.dorian.test.system.steps;

import org.globus.gsi.GlobusCredential;

public interface GridCredentialRequestOutcome {
	public abstract void check(GlobusCredential credential, Throwable error) throws Exception;
}
