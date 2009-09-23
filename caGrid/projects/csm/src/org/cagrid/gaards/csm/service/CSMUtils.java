package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.dao.ApplicationSearchCriteria;
import gov.nih.nci.security.dao.GroupSearchCriteria;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;

public class CSMUtils {

	private static Log log = LogFactory.getLog(CSMUtils.class.getName());

	public static ApplicationSearchCriteria convert(
			org.cagrid.gaards.csm.bean.ApplicationSearchCriteria criteria) {
		Application app = new Application();
		if (criteria != null) {
			if (criteria.getId() != null) {
				app.setApplicationId(criteria.getId().longValue());
			}
			app.setApplicationName(criteria.getName());
			app.setApplicationDescription(criteria.getDescription());
		}
		return new ApplicationSearchCriteria(app);
	}

	public static org.cagrid.gaards.csm.bean.Application convert(Application app) {
		org.cagrid.gaards.csm.bean.Application a = new org.cagrid.gaards.csm.bean.Application();
		if (app.getApplicationId() != null) {
			a.setId(new BigInteger(String.valueOf(app.getApplicationId())));
		}
		a.setName(app.getApplicationName());
		a.setDescription(app.getApplicationDescription());
		return a;
	}

	public static Application convert(org.cagrid.gaards.csm.bean.Application app) {
		Application a = new Application();
		if (app.getId() != null) {
			a.setApplicationId(app.getId().longValue());
		}
		a.setApplicationName(app.getName());
		a.setApplicationDescription(app.getDescription());
		return a;
	}

	public static List<org.cagrid.gaards.csm.bean.Application> convert(
			List<Application> apps) {
		List<org.cagrid.gaards.csm.bean.Application> list = new ArrayList<org.cagrid.gaards.csm.bean.Application>();
		for (int i = 0; i < apps.size(); i++) {
			list.add(convert(apps.get(i)));
		}
		return list;
	}

	public static Group getWebServiceAdminGroup(AuthorizationManager auth)
			throws CSMInternalFault {
		try {
			Application webService = auth
					.getApplication(Constants.CSM_WEB_SERVICE_CONTEXT);
			Group group = new Group();
			group.setApplication(webService);
			group.setGroupName(webService.getApplicationName() + " "
					+ Constants.ADMIN_GROUP_SUFFIX);
			List<Group> groups = auth
					.getObjects(new GroupSearchCriteria(group));
			return groups.get(0);
		} catch (Exception e) {
			logError(e.getMessage(), e);
			CSMInternalFault fault = new CSMInternalFault();
			fault
					.setFaultString("An unexpected error occurred loading the CSM Web Service Admin Group.");
			throw fault;
		}
	}

	private static void logInfo(String s) {
		System.out.println(s);
		log.info(s);
	}

	private static void logError(String s, Exception e) {
		log.error(s, e);
	}
}
