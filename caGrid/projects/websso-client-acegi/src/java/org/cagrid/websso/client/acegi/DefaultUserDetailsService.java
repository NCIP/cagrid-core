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
