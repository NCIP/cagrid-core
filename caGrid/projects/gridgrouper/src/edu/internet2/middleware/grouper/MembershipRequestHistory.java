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

import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;

public class MembershipRequestHistory {
	private String id;

	private MembershipRequestStatus status;

	private Member reviewer;
	private long updateDate;
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
		this.updateDate = System.currentTimeMillis();
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

	public long getUpdateDate() {
		return this.updateDate;
	}

	private void setUpdateDate(long time) {
		this.updateDate = time;
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
