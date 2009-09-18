package org.cagrid.gaards.websso.authentication;

import gov.nih.nci.cagrid.common.Utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.OneTimePassword;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class UsernamePasswordAuthenticationServiceURLCredentials extends UsernamePasswordCredentials{
	
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(getClass());
	private String authenticationServiceURL = null;
	private String dorianName=null;
	private String authenticationServiceProfile=null;
	private String onetimepassword;
	public static String BASIC_AUTHENTICATION=AuthenticationProfile.BASIC_AUTHENTICATION.getLocalPart();
	public static String ONE_TIME_PASSWORD=AuthenticationProfile.ONE_TIME_PASSWORD.getLocalPart();
	
	public void setDorianName(String dorianName) {
		this.dorianName = dorianName;
	}
	
	public String getDorianName() {
		return dorianName;
	}

	public String getAuthenticationServiceProfile() {
		return authenticationServiceProfile;
	}
	
	public void setAuthenticationServiceProfile(
			String authenticationServiceProfile) {
		this.authenticationServiceProfile = authenticationServiceProfile;
	}
	
	public String getAuthenticationServiceURL() {
		return authenticationServiceURL;
	}

	public void setAuthenticationServiceURL(String authenticationServiceURL) {
		this.authenticationServiceURL = authenticationServiceURL;
	}
	
	public String getOnetimepassword() {
		return onetimepassword;
	}
	
	public void setOnetimepassword(String onetimepassword) {
		this.onetimepassword = onetimepassword;
	}
	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
		
	public Credential getCredential() {
		Credential credential = null;
		log.debug("Authentication profile "+authenticationServiceProfile);
		if (BASIC_AUTHENTICATION.equals(authenticationServiceProfile)) {
			credential = getBasicCredential(getUsername(), getPassword());
		} else if (ONE_TIME_PASSWORD.equals(authenticationServiceProfile)) {
			credential = getOnetimePasswordCredential(getUsername(), getOnetimepassword());
		}else{
			throw new RuntimeException("Authentication Service profile "+authenticationServiceProfile +" is not supported");
		}
		return credential;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}
		final UsernamePasswordAuthenticationServiceURLCredentials c = (UsernamePasswordAuthenticationServiceURLCredentials) obj;
		return this.getUsername().equals(c.getUsername())
				&& this.getPassword().equals(c.getPassword())
				&& this.authenticationServiceURL.equals(c.getAuthenticationServiceURL())
				&& this.dorianName.equals(c.getDorianName())
				&& this.authenticationServiceProfile.equals(c.getAuthenticationServiceProfile());
	}

	@Override
	public int hashCode() {
		return this.getUsername().hashCode() ^ this.getPassword().hashCode()
				^ this.authenticationServiceURL.hashCode()
				^ this.dorianName.hashCode()
				^ this.authenticationServiceProfile.hashCode();
	}
	
	private Credential getBasicCredential(String userName, String password) {
		BasicAuthentication basicAuthentication = new BasicAuthentication();
		basicAuthentication.setUserId(Utils.clean(userName));
		basicAuthentication.setPassword(Utils.clean(password));
		return basicAuthentication;
	}
	
	private Credential getOnetimePasswordCredential(String userName, String onetimepassword) {
		OneTimePassword onetimePassword = new OneTimePassword();
		onetimePassword.setUserId(Utils.clean(userName));
		onetimePassword.setOneTimePassword(Utils.clean(onetimepassword));
		return onetimePassword;
	}
}
