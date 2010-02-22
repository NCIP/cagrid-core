/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package edu.internet2.middleware.grouper;

import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;

public class MembershipRequestsFinder {

	private static Log log = LogFactory.getLog(MembershipRequestsFinder.class);
	
	public static ArrayList<MembershipRequests> findRequestsByStatus(Group group, MembershipRequestStatus status) {
		ArrayList<MembershipRequests> requests = new ArrayList<MembershipRequests>();
		Session hs = null;
		try {
			hs = GridGrouperHibernateHelper.getSession();
			Query qry = null;
			if (MembershipRequestStatus.All.equals(status)) {
				qry = hs.createQuery("from MembershipRequests as mr where mr.group = :grp");
			} else {
				qry = hs.createQuery("from MembershipRequests as mr where mr.group = :grp and mr.status = :status");
				qry.setString("status", status.toString());
			}
			qry.setEntity("grp", group);
			List<?> list = qry.list();

			for (Object object : list) {
				MembershipRequests membershipRequests = (MembershipRequests) object;
				requests.add(membershipRequests);
			}

		} catch (HibernateException e) {
			FaultUtil.logFault(log, e);
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

	public static MembershipRequests findRequest(Group group, String requestor) throws MemberNotFoundException {
		Session hs = null;

		try {
			hs = GridGrouperHibernateHelper.getSession();
			Query qry = hs.createQuery("from MembershipRequests as mr where mr.group = :grp and requestor = :requestor");;
			qry.setString("requestor", requestor);
			qry.setEntity("grp", group);
			List<?> list = qry.list();

			for (Object object : list) {
				MembershipRequests membershipRequests = (MembershipRequests) object;
				return membershipRequests;
			}

		} catch (HibernateException e) {
			FaultUtil.logFault(log, e);
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

}
