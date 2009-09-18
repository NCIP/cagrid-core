package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.property.ServiceProperties;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;


public class AddServicePropertiesStep extends BaseStep {
	private TestCaseInfo tci;


	public AddServicePropertiesStep(TestCaseInfo tci, boolean build) throws Exception {
		super(tci.getDir(), build);
		this.tci = tci;
	}


	public void runStep() throws Throwable {
		System.out.println("Adding a simple method.");

		ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
			+ tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
		ServiceProperties props = introService.getServiceProperties();
		if (props == null) {
			props = new ServiceProperties();
			introService.setServiceProperties(props);
		}

		ServicePropertiesProperty newProperty1 = new ServicePropertiesProperty();
		newProperty1.setKey("foo");
		newProperty1.setValue("bar");

		ServicePropertiesProperty newProperty2 = new ServicePropertiesProperty();
		newProperty2.setKey("bar");
		newProperty2.setValue("barValue");

		// add new method to array in bean
		// this seems to be a wierd way be adding things....
		ServicePropertiesProperty[] propertyTypeArr;
		int newLength = 0;
		if (props.getProperty() != null) {
			newLength = props.getProperty().length + 2;
			propertyTypeArr = new ServicePropertiesProperty[newLength];
			System.arraycopy(props.getProperty(), 0, propertyTypeArr, 0, props.getProperty().length);
		} else {
			newLength = 2;
			propertyTypeArr = new ServicePropertiesProperty[newLength];
		}
		propertyTypeArr[newLength - 2] = newProperty1;
		propertyTypeArr[newLength - 1] = newProperty2;
		props.setProperty(propertyTypeArr);

		Utils.serializeDocument(getBaseDir() + File.separator + tci.getDir() + File.separator + "introduce.xml",
			introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);

		try {
			SyncTools sync = new SyncTools(new File(getBaseDir() + File.separator + tci.getDir()));
			sync.sync();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		buildStep();
	}

}
