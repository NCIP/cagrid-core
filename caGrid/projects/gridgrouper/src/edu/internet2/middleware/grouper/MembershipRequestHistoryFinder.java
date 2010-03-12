package edu.internet2.middleware.grouper;

import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MembershipRequestHistoryFinder {

	private static Log log = LogFactory.getLog(MembershipRequestHistoryFinder.class);
	
	public static ArrayList<MembershipRequestHistory> findHistory(GrouperSession grouperSession, Group group, String requestor) throws QueryException {
		ArrayList<MembershipRequestHistory> requests = new ArrayList<MembershipRequestHistory>();
		Session hs = null;
		try {
			hs = GridGrouperHibernateHelper.getSession();
			Query qry = hs.createQuery("select mrh from MembershipRequestHistory as mrh inner join mrh.membershipRequest as mr where mr.group = :grp and mr.requestorId = :requestor");
			qry.setEntity("grp", group);
			qry.setString("requestor", requestor);
			List<?> list = qry.list();

			for (Object object : list) {
				MembershipRequestHistory membershipRequests = (MembershipRequestHistory) object;
				requests.add(membershipRequests);
			}

		} catch (HibernateException e) {
			 throw new QueryException("error finding history requests: " + e.getMessage(), e);  
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

}
