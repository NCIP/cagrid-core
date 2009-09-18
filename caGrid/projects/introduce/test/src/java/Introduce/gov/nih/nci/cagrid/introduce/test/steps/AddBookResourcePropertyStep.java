package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

import javax.xml.namespace.QName;


public class AddBookResourcePropertyStep extends BaseStep {
	private TestCaseInfo tci;


	public AddBookResourcePropertyStep(TestCaseInfo tci, boolean build) throws Exception {
		super(tci.getDir(),build);
		this.tci = tci;
	}


	public void runStep() throws Throwable {
		System.out.println("Adding simple resource property.");

		ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
			+ tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
	
		ResourcePropertiesListType metadatasType = CommonTools.getService(introService.getServices(),tci.getName()).getResourcePropertiesList();
		ResourcePropertyType metadata = new ResourcePropertyType();
		metadata.setRegister(true);
		metadata.setQName(new QName("gme://projectmobius.org/1/BookStore", "Book"));
		
		// add new metadata to array in bean
		// this seems to be a wierd way be adding things....
		ResourcePropertyType[] newMetadatas;
		int newLength = 0;
		if (metadatasType !=null && metadatasType.getResourceProperty() != null) {
			newLength = metadatasType.getResourceProperty().length + 1;
			newMetadatas = new ResourcePropertyType[newLength];
			System.arraycopy(metadatasType.getResourceProperty(), 0, newMetadatas, 0, metadatasType.getResourceProperty().length);
		} else {
			newLength = 1;
			newMetadatas = new ResourcePropertyType[newLength];
		}
		ResourcePropertiesListType rplist = new ResourcePropertiesListType();
		newMetadatas[newLength - 1] = metadata;
		rplist.setResourceProperty(newMetadatas);
		CommonTools.getService(introService.getServices(),tci.getName()).setResourcePropertiesList(rplist);

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
