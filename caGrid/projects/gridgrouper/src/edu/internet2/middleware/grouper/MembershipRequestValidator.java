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
package edu.internet2.middleware.grouper;

import edu.internet2.middleware.subject.Subject;

class MembershipRequestValidator {
	
	  protected static void canUpdateRequest(Group group, Subject subj)
	    throws  InsufficientPrivilegeException
	  {
			if (!PrivilegeResolver.hasPriv(group.getSession(), group, subj, AccessPrivilege.ADMIN)) {					
				throw new InsufficientPrivilegeException(subj.getName() + "does not have the required permission to update a membership request");
			}

	  } 

}

