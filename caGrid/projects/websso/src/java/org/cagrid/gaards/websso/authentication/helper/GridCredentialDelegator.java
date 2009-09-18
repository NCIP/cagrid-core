package org.cagrid.gaards.websso.authentication.helper;

import java.util.List;

import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;
import org.globus.gsi.GlobusCredential;

public interface GridCredentialDelegator {

	public String delegateGridCredential(GlobusCredential globusCredential,
			List<String> hostIdentityList)
			throws AuthenticationConfigurationException;
}
