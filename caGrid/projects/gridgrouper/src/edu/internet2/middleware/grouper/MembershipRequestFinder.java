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
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;

public class MembershipRequestFinder {

	private static Log log = LogFactory.getLog(MembershipRequestFinder.class);
	
	public static ArrayList<MembershipRequest> findRequestsByStatus(GrouperSession grouperSession, Group group, MembershipRequestStatus status) throws QueryException {
		ArrayList<MembershipRequest> requests = new ArrayList<MembershipRequest>();
		Session hs = null;
		try {
			hs = GridGrouperHibernateHelper.getSession();
			Query qry = null;
			if (MembershipRequestStatus.All.equals(status)) {
				qry = hs.createQuery("from MembershipRequest as mr where mr.group = :grp");
			} else {
				qry = hs.createQuery("from MembershipRequest as mr where mr.group = :grp and mr.statusValue = :status");
				qry.setString("status", status.toString());
			}
			qry.setEntity("grp", group);
			List<?> list = qry.list();

			for (Object object : list) {
				MembershipRequest membershipRequests = (MembershipRequest) object;
				membershipRequests.getGroup().setSession(grouperSession);
				requests.add(membershipRequests);
			}

		} catch (HibernateException e) {
			 throw new QueryException("error finding requests: " + e.getMessage(), e);  
		} finally {
			if (hs != null) {
				try {
					hs.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return requests;
	} 

	public static MembershipRequest findRequest(GrouperSession grouperSession, Group group, String requestor) throws QueryException  {
		Session hs = null;

		try {
			hs = GridGrouperHibernateHelper.getSession();
			Query qry = hs.createQuery("from MembershipRequest as mr where mr.group = :grp and requestor = :requestor");;
			qry.setString("requestor", requestor);
			qry.setEntity("grp", group);
			List<?> list = qry.list();

			for (Object object : list) {
				MembershipRequest membershipRequests = (MembershipRequest) object;
				membershipRequests.getGroup().setSession(grouperSession);
				return membershipRequests;
			}

		} catch (HibernateException e) {
			 throw new QueryException("error finding requesrt: " + e.getMessage(), e);  
		} finally {
			if (hs != null) {
				try {
					hs.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return null;
	} 
	
	public static void removeRequest(Subject caller, Group group, String requestor) throws MemberNotFoundException, InsufficientPrivilegeException, MembershipRequestUpdateException, GrouperException {
		Session hs = null;

		try {
			hs = GridGrouperHibernateHelper.getSession();
			Query qry = hs.createQuery("from MembershipRequest as mr where mr.group = :grp and requestor = :requestor");;
			qry.setString("requestor", requestor);
			qry.setEntity("grp", group);
			List<?> list = qry.list();
			
			Member member = MemberFinder.findBySubject(caller);

			for (Object object : list) {
				MembershipRequest membershipRequests = (MembershipRequest) object;
				membershipRequests.getGroup().setSession(group.getSession());
				membershipRequests.remove(member, "Member removed from the group.", "Member removed from the group.");			
			}
			
		} catch (HibernateException e) {
			throw new MembershipRequestUpdateException("Unable to remove membershiprequest: " + e.getMessage(), e);
		} finally {
			if (hs != null) {
				try {
					hs.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	} 


}
