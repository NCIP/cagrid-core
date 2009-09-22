package org.cagrid.gaards.csm.service;

import java.util.List;

import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;

import gov.nih.nci.cagrid.common.FaultUtil;
import junit.framework.TestCase;

public class TestCSM extends TestCase {
	public void testCSMInitialization() {

		try {
			CSM csm = new CSM(Utils.getCSMProperties());
			List<Application> apps = csm.getApplications(new ApplicationSearchCriteria());
			assertEquals(1, apps.size());
			assertEquals(Constants.CSM_WEB_SERVICE_CONTEXT, apps.get(0).getName());
			assertEquals(Constants.CSM_WEB_SERVICE_DESCRIPTION, apps.get(0).getDescription());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
		} finally {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		Utils.initializeDatabase();
	}
}
