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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.hibernate.HibernateException;

import org.apache.commons.lang.time.StopWatch;

import edu.internet2.middleware.subject.SubjectNotFoundException;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;

public class MembershipRequest {
	private String id;

	private Group group;
	private String requestorId;
	private long requestTime;
	private MembershipRequestStatus status;

	private Member reviewer;
	private long reviewTime;
	private String publicNote;
	private String adminNote;
	
	private ArrayList<MembershipRequestHistory> history = null;

	public MembershipRequest() {
		super();
	}

	public MembershipRequest(Group group, String requestor) {
		this(group, requestor, MembershipRequestStatus.Pending);
	}

	private MembershipRequest(Group group, String requestorId, MembershipRequestStatus status) {
		super();
		this.group = group;
		this.requestorId = requestorId;
		this.requestTime = System.currentTimeMillis();
		this.status = status;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getRequestorId() {
		return requestorId;
	}

	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}

	public Member getReviewer() {
		return reviewer;
	}

	public void setReviewer(Member reviewer) {
		this.reviewer = reviewer;
	}

	public MembershipRequestStatus getStatus() {
		return status;
	}

	private void setStatus(MembershipRequestStatus status) throws InsufficientPrivilegeException {
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			this.status = status;
			GridGrouperHibernateHelper.save(this);
			MembershipRequestHistory membershipRequestHistory = new MembershipRequestHistory(this);
			GridGrouperHibernateHelper.save(membershipRequestHistory);
			
			sw.stop();
		} catch (HibernateException eH) {
			throw new InsufficientPrivilegeException(eH.getMessage(), eH);
		}
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

	public ArrayList<MembershipRequestHistory> getHistory() throws GrouperException {
		if (history == null) {
			try {
				history = MembershipRequestHistoryFinder.findHistory(group.getSession(), group, requestorId);
			} catch (QueryException e) {
				throw new GrouperException("Unable to fetch membershiprequest history: " + e.getMessage(), e);
			}
		}
		return this.history;
	}

	private void setHistory(ArrayList<MembershipRequestHistory> history) {
		this.history = history;
	}

	public static MembershipRequest create(Group group, String requestor) throws MembershipRequestAddException {
		try {
			MembershipRequest m = new MembershipRequest(group, requestor);
			GridGrouperHibernateHelper.save(m);
			MembershipRequestHistory membershipRequestHistory = new MembershipRequestHistory(m);
			GridGrouperHibernateHelper.save(membershipRequestHistory);
			
			m.history = new ArrayList<MembershipRequestHistory>();
			m.history.add(membershipRequestHistory);
			
			return m;
		} catch (HibernateException eH) {
			throw new MembershipRequestAddException("unable to save membershiprequest: " + eH.getMessage(), eH);
		}
	}

	public static void configureGroup(GrouperSession session, Group grp) throws InsufficientPrivilegeException,
			SchemaException, GroupModifyException, GrouperException {

		GroupType membershipRequestGroupType = null;

		try {
			membershipRequestGroupType = GroupTypeFinder.find("MembershipRequests");
		} catch (SchemaException eS) {
			membershipRequestGroupType = createType();
		}

		Set<?> groupTypes = grp.getTypes();
		if (!groupTypes.contains(membershipRequestGroupType)) {
			grp.addType(membershipRequestGroupType);
		}
	}
	
	public static void markAllRequestsRemoved(GrouperSession session, Member rejector, Group group, String note) throws MembershipRequestUpdateException, InsufficientPrivilegeException, SubjectNotFoundException, GrouperException {
		MembershipRequestValidator.canUpdateRequest(group, rejector.getSubject());

		ArrayList<MembershipRequest> requests;
		try {
			requests = MembershipRequestFinder.findRequestsByStatus(session, group, MembershipRequestStatus.Pending);
		} catch (QueryException e) {
			throw new MembershipRequestUpdateException("Unable to approve membershiprequest: " + e.getMessage(), e);
		}
		for (MembershipRequest membershipRequest : requests) {
			membershipRequest.remove(rejector, note, null);
		}
	}

	public void approve(Member approver, String publicNote, String adminNote) throws MembershipRequestUpdateException, InsufficientPrivilegeException, GrouperException {
		try {
			MembershipRequestValidator.canUpdateRequest(this.group, approver.getSubject());

			this.status = MembershipRequestStatus.Approved;
			this.reviewer = approver;
			this.publicNote = publicNote;
			this.adminNote = adminNote;
			this.reviewTime = System.currentTimeMillis();

			GridGrouperHibernateHelper.save(this);
			MembershipRequestHistory membershipRequestHistory = new MembershipRequestHistory(this);
			GridGrouperHibernateHelper.save(membershipRequestHistory);
			getHistory().add(membershipRequestHistory);
		} catch (HibernateException eH) {
			throw new MembershipRequestUpdateException("Unable to approve membershiprequest: " + eH.getMessage(), eH);
		} catch (SubjectNotFoundException e) {
			throw new MembershipRequestUpdateException("Unable to approve membershiprequest: " + e.getMessage(), e);
		}
	}

	public void pending() throws MembershipRequestUpdateException, GrouperException {

		this.status = MembershipRequestStatus.Pending;
		this.publicNote = "Request Resubmitted. " + this.publicNote;
		this.reviewTime = 0;
		try {
			GridGrouperHibernateHelper.save(this);
			MembershipRequestHistory membershipRequestHistory = new MembershipRequestHistory(this);
			GridGrouperHibernateHelper.save(membershipRequestHistory);
			getHistory().add(membershipRequestHistory);
		} catch (HibernateException eH) {
			throw new MembershipRequestUpdateException("Unable to change membershiprequest to pending: " + eH.getMessage(), eH);
		}

	}
	
	public void reject(Member rejector, String publicNote, String adminNote) throws MembershipRequestUpdateException, InsufficientPrivilegeException, GrouperException {
		try {
			MembershipRequestValidator.canUpdateRequest(this.group, rejector.getSubject());
			
			if (!MembershipRequestStatus.Pending.equals(this.status)) {
				throw new MembershipRequestUpdateException("Only pending membership requests can be rejected");				
			}

			this.status = MembershipRequestStatus.Rejected;
			this.reviewer = rejector;
			this.publicNote = publicNote;
			this.adminNote = adminNote;
			this.reviewTime = System.currentTimeMillis();

			GridGrouperHibernateHelper.save(this);
			MembershipRequestHistory membershipRequestHistory = new MembershipRequestHistory(this);
			GridGrouperHibernateHelper.save(membershipRequestHistory);
			getHistory().add(membershipRequestHistory);
		} catch (HibernateException eH) {
			throw new MembershipRequestUpdateException("Unable to reject membershiprequest: " + eH.getMessage(), eH);
		} catch (SubjectNotFoundException e) {
			throw new MembershipRequestUpdateException("Unable to reject membershiprequest: " + e.getMessage(), e);
		}

	}
	
	public void remove(Member approver, String publicNote, String adminNote) throws MembershipRequestUpdateException, InsufficientPrivilegeException, GrouperException {
		try {
			MembershipRequestValidator.canUpdateRequest(this.group, approver.getSubject());

			this.status = MembershipRequestStatus.Removed;
			this.reviewer = approver;
			this.publicNote = publicNote;
			this.adminNote = adminNote;
			this.reviewTime = System.currentTimeMillis();

			GridGrouperHibernateHelper.save(this);
			MembershipRequestHistory membershipRequestHistory = new MembershipRequestHistory(this);
			GridGrouperHibernateHelper.save(membershipRequestHistory);
			getHistory().add(membershipRequestHistory);
		} catch (HibernateException eH) {
			throw new MembershipRequestUpdateException("Unable to remove membershiprequest: " + eH.getMessage(), eH);
		} catch (SubjectNotFoundException e) {
			throw new MembershipRequestUpdateException("Unable to remove membershiprequest: " + e.getMessage(), e);
		}
	}


	private static GroupType createType() throws GrouperException {
		Set set = new LinkedHashSet();
		Field field = new Field("allowMembershipRequests", FieldType.ATTRIBUTE, Privilege.getInstance("view"), Privilege.getInstance("admin"), false);
		set.add(field);
		GroupType gt = new GroupType("MembershipRequests", set, true, false);
		try { 
			GridGrouperHibernateHelper.save(gt);
		} catch (HibernateException eH) {
			throw new GrouperException("Unable to create membershiprequest group type: " + eH.getMessage(), eH);
		}
		return gt;
	}

}
