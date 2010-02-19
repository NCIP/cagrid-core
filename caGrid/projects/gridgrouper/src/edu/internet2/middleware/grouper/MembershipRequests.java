package edu.internet2.middleware.grouper;

import net.sf.hibernate.HibernateException;

import org.apache.commons.lang.time.StopWatch;

public class MembershipRequests {
	private String id;

	private Group group;
	private String requestor;
	private long requestTime;
	private String status;

	private Member reviewer;
	private long reviewTime;
	private String reviewerNote;

	public MembershipRequests() {
		super();
	}

	public MembershipRequests(Group group, String requestor) {
		this(group, requestor, "Pending");
	}

	private MembershipRequests(Group group, String requestor, String status) {
		super();
		this.group = group;
		this.requestor = requestor;
		this.requestTime = System.currentTimeMillis();
		this.status = status;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	
	public Member getReviewer() {
		return reviewer;
	}

	public void setReviewer(Member reviewer) {
		this.reviewer = reviewer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) throws InsufficientPrivilegeException {
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			this.status = status;
			GridGrouperHibernateHelper.save(this);
			sw.stop();
		} catch (HibernateException eH) {
			throw new InsufficientPrivilegeException(eH.getMessage(), eH);
		}
	}

	public String getId() {
		return this.id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public long getRequestTime() {
		return this.requestTime;
	}

	private void setRequestTime(long time) {
		this.requestTime = time;
	}
	
	public long getReviewTime() {
		return this.reviewTime;
	}

	private void setReviewTime(long time) {
		this.reviewTime = time;
	}

	public String getReviewerNote() {
		return this.reviewerNote;
	}

	private void setReviewerNote(String note) {
		this.reviewerNote = note;
	}

	public static MembershipRequests create(Group group, String requestor) throws MemberNotFoundException {
		try {
			MembershipRequests m = new MembershipRequests(group, requestor);
			GridGrouperHibernateHelper.save(m);
			return m;
		} catch (HibernateException eH) {
			throw new MemberNotFoundException("unable to save membershiprequest: " + eH.getMessage(), eH);
		}
	}
	
	public void approve(Member approver, String note) throws MemberNotFoundException {
		this.status = "Approved";
		this.reviewer = approver;
		this.reviewerNote = note;
		this.reviewTime = System.currentTimeMillis();
		try {
			GridGrouperHibernateHelper.save(this);
		} catch (HibernateException eH) {
			throw new MemberNotFoundException("unable to save membershiprequest: " + eH.getMessage(), eH);
		}
	}
	
	public void reject(Member rejector, String note) throws MemberNotFoundException {
		this.status = "Rejected";
		this.reviewer = rejector;
		this.reviewerNote = note;
		this.reviewTime = System.currentTimeMillis();
		try {
			GridGrouperHibernateHelper.save(this);
		} catch (HibernateException eH) {
			throw new MemberNotFoundException("unable to save membershiprequest: " + eH.getMessage(), eH);
		}
		
	}

	public void pending() throws MemberNotFoundException {
		this.status = "Pending";
		this.reviewerNote = "Request Resubmitted. " + this.reviewerNote;
		this.reviewTime = 0;
		try {
			GridGrouperHibernateHelper.save(this);
		} catch (HibernateException eH) {
			throw new MemberNotFoundException("unable to save membershiprequest: " + eH.getMessage(), eH);
		}
		
	}

}
