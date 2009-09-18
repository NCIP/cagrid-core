package gov.nih.nci.cagrid.introduce.extension.example;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;

import java.io.File;
import java.io.IOException;

public class ExampleCreationPostProcessor implements CreationExtensionPostProcessor {

	public void postCreate(ServiceExtensionDescriptionType desc, ServiceInformation serviceInfo) throws CreationExtensionException {
		String fileName = serviceInfo.getIntroduceServiceProperties().getProperty(IntroduceConstants.INTRODUCE_SKELETON_DESTINATION_DIR) + File.separator + this.getClass().getName();
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CreationExtensionException(e.getMessage(),e);
		}
	}

}
