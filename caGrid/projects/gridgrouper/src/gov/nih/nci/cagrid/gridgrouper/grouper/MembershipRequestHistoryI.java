package gov.nih.nci.cagrid.gridgrouper.grouper;

import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;

public interface MembershipRequestHistoryI {
	
	public MemberI getReviewer();
	
	public MembershipRequestStatus getStatus();
}
