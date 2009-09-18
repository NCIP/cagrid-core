package org.cagrid.websso.client.acegi;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.globus.gsi.GlobusCredential;

public class WebSSOUser extends User {
	private static final long serialVersionUID = -3874241273400669354L;

	private String gridId;
	private String delegatedEPR;
	private String firstName;
	private String lastName;
	private String emailId;
	private GlobusCredential gridCredential;

	public WebSSOUser(String string, String string1, boolean b, boolean b1,
			boolean b2, boolean b3, GrantedAuthority[] grantedAuthorities)
			throws IllegalArgumentException {
		super(string, string1, b, b1, b2, b3, grantedAuthorities);
	}

	public WebSSOUser(UserDetails user) {
		this(user.getUsername(), user.getPassword(), true, true, true, true,
				user.getAuthorities());
	}

	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
	}

	public String getDelegatedEPR() {
		return delegatedEPR;
	}

	public void setDelegatedEPR(String delegatedEPR) {
		this.delegatedEPR = delegatedEPR;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEmailId() {
		return emailId;
	}

	public GlobusCredential getGridCredential() {
		return gridCredential;
	}

	public void setGridCredential(GlobusCredential gridCredential) {
		this.gridCredential = gridCredential;
	}
}