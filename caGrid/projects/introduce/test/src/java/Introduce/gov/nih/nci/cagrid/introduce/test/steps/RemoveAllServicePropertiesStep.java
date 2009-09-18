package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.property.ServiceProperties;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

public class RemoveAllServicePropertiesStep extends BaseStep {
	private TestCaseInfo tci;

	public RemoveAllServicePropertiesStep(TestCaseInfo tci, boolean build)
			throws Exception {
		super(tci.getDir(), build);
		this.tci = tci;
	}

	public void runStep() throws Throwable {
		System.out.println("Adding metadata.");

		ServiceDescription introService = (ServiceDescription) Utils
				.deserializeDocument(getBaseDir() + File.separator
						+ tci.getDir() + File.separator + "introduce.xml",
						ServiceDescription.class);
		ServiceProperties properties = introService.getServiceProperties();
		properties.setProperty(null);

		Utils.serializeDocument(getBaseDir() + File.separator + tci.getDir()
				+ File.separator + "introduce.xml", introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);

		try {
			SyncTools sync = new SyncTools(new File(getBaseDir()
					+ File.separator + tci.getDir()));
			sync.sync();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		buildStep();
	}

}
