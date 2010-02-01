package org.cagrid.websso.client.acegi;

import gov.nih.nci.cagrid.common.FaultUtil;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.providers.cas.CasAuthoritiesPopulator;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.cagrid.websso.common.WebSSOClientException;
import org.cagrid.websso.common.WebSSOClientHelper;
import org.globus.gsi.GlobusCredential;
import org.springframework.beans.factory.annotation.Required;

public class WebSSOAuthoritiesPopulator implements CasAuthoritiesPopulator {

	private final Log log = LogFactory.getLog(getClass());

	private UserDetailsService userDetailsService;

    private String hostCertificate;

    private String hostKey;

    /**
     * Obtains the granted authorities for the specified user.
     * <P>
     * May throw any <code>AuthenticationException</code> or return <code>null</code> if the
     * authorities are unavailable.
     * </p>
     */
    public UserDetails getUserDetails(String casUserId) throws AuthenticationException {
    	WebSSOUser user = (WebSSOUser) userDetailsService.loadUserByUsername(casUserId);
		String delegationEPR =user.getDelegatedEPR(); 
		log.debug("User Info "+user);
		try {
			GlobusCredential userCredential = WebSSOClientHelper.getUserCredential(delegationEPR,hostCertificate,hostKey);
			user.setGridCredential(userCredential);
		} catch (WebSSOClientException e) {
			log.info(FaultUtil.printFaultToString(e));
			throw new BadCredentialsException("Error occured while validating user credentials ",e);
		}
		return user;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    @Required
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public String getHostCertificate() {
		return hostCertificate;
	}

	@Required
	public void setHostCertificate(String hostCertificate) {
		this.hostCertificate = hostCertificate;
	}

	public String getHostKey() {
		return hostKey;
	}

	@Required
	public void setHostKey(String hostKey) {
		this.hostKey = hostKey;
	}
}