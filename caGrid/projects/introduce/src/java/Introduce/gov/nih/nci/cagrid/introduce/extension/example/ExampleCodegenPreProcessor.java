package gov.nih.nci.cagrid.introduce.extension.example;

import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPreProcessor;

import java.io.File;
import java.io.IOException;

public class ExampleCodegenPreProcessor implements CodegenExtensionPreProcessor {

	public void preCodegen(ServiceExtensionDescriptionType desc, ServiceInformation info)
		throws CodegenExtensionException {
		// TODO Auto-generated method stub
		String fileName = info.getBaseDirectory().getAbsolutePath() + File.separator + this.getClass().getName();
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CodegenExtensionException(e.getMessage(),e);
		}
	}

}
