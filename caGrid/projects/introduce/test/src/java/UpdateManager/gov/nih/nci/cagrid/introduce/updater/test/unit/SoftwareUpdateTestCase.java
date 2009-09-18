package gov.nih.nci.cagrid.introduce.updater.test.unit;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.software.SoftwareType;
import gov.nih.nci.cagrid.introduce.updater.UpdateManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class SoftwareUpdateTestCase extends TestCase {

	public void testUpdaterNullUpdates() {
		UpdateManager manager = null;
		try {
			manager = new UpdateManager(null);
		} catch (Exception e) {
			return;
		}
		fail("UpdateManger taking in null should throw exception");
	}

	public void testUpdaterInvokeEmptyUpdates() {
		SoftwareType software = new SoftwareType();
		try {
			Utils.serializeObject(software,
					new QName("gme://gov.nih.nci.cagrid.introduce/1/Software",
							"Software"), new FileWriter(new File("."
							+ File.separator + "updates" + File.separator
							+ "software.xml")));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		UpdateManager manager = null;
		try {
			manager = new UpdateManager(software);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SoftwareUpdateTestCase.class);
	}
}
