package edu.internet2.middleware.grouper;

import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;

public class MembershipRequestHistory {
	private String id;

	private MembershipRequestStatus status;

	private Member reviewer;
	private long reviewTime;
	private String publicNote;
	private String adminNote;
	
	private MembershipRequest membershipRequest;

	public MembershipRequestHistory() {
		super();
	}

	public MembershipRequestHistory(MembershipRequest membershipRequest) {
		super();
		this.status = membershipRequest.getStatus();
		this.reviewer = membershipRequest.getReviewer();
		this.publicNote = membershipRequest.getPublicNote();
		this.adminNote = membershipRequest.getAdminNote();
		this.membershipRequest = membershipRequest;
		if (MembershipRequestStatus.Pending.equals(this.status)) {
			this.reviewTime = membershipRequest.getRequestTime();
		} else {
			this.reviewTime = membershipRequest.getReviewTime();
		}
	}

	public Member getReviewer() {
		return reviewer;
	}

	private void setReviewer(Member reviewer) {
		this.reviewer = reviewer;
	}

	public MembershipRequestStatus getStatus() {
		return status;
	}
	
	private String getStatusValue() {
		return status.toString();
	}

	private void setStatusValue(String value) {
		this.status = MembershipRequestStatus.fromString(value);
	}


	public String getId() {
		return this.id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public long getReviewTime() {
		return this.reviewTime;
	}

	private void setReviewTime(long time) {
		this.reviewTime = time;
	}

	public String getPublicNote() {
		return this.publicNote;
	}

	private void setPublicNote(String note) {
		this.publicNote = note;
	}

	public String getAdminNote() {
		return this.adminNote;
	}

	private void setAdminNote(String note) {
		this.adminNote = note;
	}
	
	private MembershipRequest getMembershipRequest() {
		return this.membershipRequest;
	}

	private void setMembershipRequest(MembershipRequest membershipRequest) {
		this.membershipRequest = membershipRequest;
	}
	

}
