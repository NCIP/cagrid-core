package edu.internet2.middleware.grouper;

import edu.internet2.middleware.subject.Subject;

class MembershipRequestsValidator {
	
	  protected static void canUpdateRequest(Group group, Subject subj)
	    throws  InsufficientPrivilegeException
	  {
			if (!PrivilegeResolver.hasPriv(group.getSession(), group, subj, AccessPrivilege.ADMIN)) {					
				throw new InsufficientPrivilegeException(subj.getName() + "does not have the required permission to update a membership request");
			}

	  } 

}

