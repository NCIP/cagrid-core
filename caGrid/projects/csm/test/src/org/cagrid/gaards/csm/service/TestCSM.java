package org.cagrid.gaards.csm.service;

import gov.nih.nci.cagrid.common.FaultUtil;
import junit.framework.TestCase;

public class TestCSM extends TestCase {
	public void testCSMInitialization() {
		try {

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
