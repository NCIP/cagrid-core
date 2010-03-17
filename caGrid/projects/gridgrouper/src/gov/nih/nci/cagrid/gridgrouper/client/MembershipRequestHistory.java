package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.subject.SubjectNotFoundException;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestHistoryDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;
import gov.nih.nci.cagrid.gridgrouper.grouper.MemberI;
import gov.nih.nci.cagrid.gridgrouper.grouper.MembershipRequestHistoryI;

public class MembershipRequestHistory extends GridGrouperObject implements MembershipRequestHistoryI {
    private MembershipRequestHistoryDescriptor des;

    private GridGrouper gridGrouper;
    
    private MemberI reviewer;
	
    public MembershipRequestHistory(GridGrouper gridGrouper, MembershipRequestHistoryDescriptor des) throws SubjectNotFoundException {
        this.gridGrouper = gridGrouper;
        this.des = des;
        if (des.getReviewer() != null) {
        	this.reviewer = new Member(gridGrouper, des.getReviewer());
        }
    }
    
    public String getPublicNote() {
    	return des.getPublicNote();
    }

    public String getAdminNote() {
    	return des.getAdminNote();
    }

    public long getReviewTime() {
    	return des.getReviewTime();
    }

	public MemberI getReviewer() {
		return reviewer;
	}

	public MembershipRequestStatus getStatus() {
		return des.getStatus();
	}
	
}
