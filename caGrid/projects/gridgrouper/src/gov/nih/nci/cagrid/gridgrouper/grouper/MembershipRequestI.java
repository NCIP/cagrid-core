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
package gov.nih.nci.cagrid.gridgrouper.grouper;

import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;

import java.util.Set;

public interface MembershipRequestI {

	public String getRequestorId();
	
	public MemberI getReviewer();
	
	public MembershipRequestStatus getStatus();

	public Set<?> getHistory();

}
