package org.cagrid.websso.client.acegi;

public class DefaultUserDetailsService extends BaseUserDetailsService {

	@Override
	protected WebSSOUser loadUserByGridId(String gridId) {
		//retrieve user information from database using CSM or Data Access Service by unique GridID.
		//for default implementation username=gridId;
		String userName=gridId;
		DefaultGrantedAuthority [] grantedAuthorities=new DefaultGrantedAuthority[]{new DefaultGrantedAuthority("ROLE_DEFAULT")};
		WebSSOUser user=new WebSSOUser(userName, "default",true,true,true,true,grantedAuthorities);
		return user;
	}
}
