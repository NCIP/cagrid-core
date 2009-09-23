package org.cagrid.gaards.csm.service;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.math.BigInteger;
import java.util.List;

import junit.framework.TestCase;

import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;

public class TestCSM extends TestCase {

	private static String SUPER_ADMIN = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=superadmin";
	private static String GENERAL_USER = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=jdoe";

	public void testCSMInitialization() {

		try {
			CSM csm = new CSM(Utils.getCSMProperties());
			findApplications(csm, null, null, 1);
			List<Application> apps = findApplications(csm, null,
					Constants.CSM_WEB_SERVICE_CONTEXT, 1);
			assertEquals(1, apps.size());
			assertEquals(Constants.CSM_WEB_SERVICE_CONTEXT, apps.get(0)
					.getName());
			assertEquals(Constants.CSM_WEB_SERVICE_DESCRIPTION, apps.get(0)
					.getDescription());
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

	public void testCreateAndDeleteApplication() {

		try {
			CSM csm = new CSM(Utils.getCSMProperties());
			findApplications(csm, null, null, 1);
			List<Application> apps = findApplications(csm, null,
					Constants.CSM_WEB_SERVICE_CONTEXT, 1);
			assertEquals(1, apps.size());
			assertEquals(Constants.CSM_WEB_SERVICE_CONTEXT, apps.get(0)
					.getName());
			assertEquals(Constants.CSM_WEB_SERVICE_DESCRIPTION, apps.get(0)
					.getDescription());
			CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(),
					SUPER_ADMIN);

			Application a = new Application();
			a.setName("myapp");
			a.setDescription("This is my application!!!");

			// Test that a non admin cannot add an application
			try {
				csm.createApplication(GENERAL_USER, a);
				fail("Only CSM Web Service admins should be able to add applications");
			} catch (AccessDeniedFault e) {

			}

			Application result = csm.createApplication(SUPER_ADMIN, a);
			assertNotNull(result.getId());
			assertEquals(a.getName(), result.getName());
			assertEquals(a.getDescription(), result.getDescription());
			findApplications(csm, null, null, 2);
			List<Application> myapps = findApplications(csm, null, a.getName(),
					1);
			assertEquals(result, myapps.get(0));
			
			
			// Test that a non admin cannot remove an application
			try {
				csm.removeApplication(GENERAL_USER, result.getId().longValue());
				fail("Only CSM Web Service admins should be able to remove applications");
			} catch (AccessDeniedFault e) {

			}
			
			csm.removeApplication(SUPER_ADMIN, result.getId().longValue());
			findApplications(csm, null, null, 1);
			findApplications(csm, null, a.getName(), 0);
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

	private List<Application> findApplications(CSM csm, BigInteger id,
			String name, int expectedResult) throws Exception {
		ApplicationSearchCriteria search = new ApplicationSearchCriteria();
		search.setId(id);
		search.setName(name);
		List<Application> apps = csm.getApplications(search);
		assertEquals(expectedResult, apps.size());
		return apps;
	}

	protected void setUp() throws Exception {
		super.setUp();
		Utils.initializeDatabase();
	}
}
