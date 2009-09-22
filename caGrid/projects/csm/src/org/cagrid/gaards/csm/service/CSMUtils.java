package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.dao.ApplicationSearchCriteria;

import java.util.ArrayList;
import java.util.List;

public class CSMUtils {

	public static ApplicationSearchCriteria convert(
			org.cagrid.gaards.csm.bean.ApplicationSearchCriteria criteria) {
		Application app = new Application();
		if (criteria != null) {
			app.setApplicationId(criteria.getId());
			app.setApplicationName(criteria.getName());
			app.setApplicationDescription(criteria.getDescription());
		}
		return new ApplicationSearchCriteria(app);
	}

	public static org.cagrid.gaards.csm.bean.Application convert(Application app) {
		org.cagrid.gaards.csm.bean.Application a = new org.cagrid.gaards.csm.bean.Application();
		a.setId(app.getApplicationId());
		a.setName(app.getApplicationName());
		a.setDescription(app.getApplicationDescription());
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
}
