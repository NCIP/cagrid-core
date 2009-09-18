package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.data.codegen.templates.CQLResultTypesTemplate;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author oster
 * 
 */
public class CQLResultTypesGenerator {

	/**
	 * Generates the XSD for potential CQL Query Result types
	 * 
	 * @param typeInfo
	 * 		The type information
	 * @throws CodegenExtensionException
	 */
	public static void generateCQLResultTypesXSD(ResultTypeGeneratorInformation typeInfo)
		throws CodegenExtensionException {
		CQLResultTypesTemplate typeTemplate = new CQLResultTypesTemplate();

		String resultTypesXSD = typeTemplate.generate(typeInfo);
		File resultTypeXSDFile = getResultTypeXSDFile(typeInfo.getServiceInfo());

		FileWriter fw;
		try {
			fw = new FileWriter(resultTypeXSDFile);
			fw.write(resultTypesXSD);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CodegenExtensionException("Problem creating CQL Results restriction XSD.", e);
		}
	}


	public static String getResultTypeXSDFileName(ServiceType dataService) {
		return dataService.getName() + "_CQLResultTypes.xsd";
	}


	public static File getResultTypeXSDFile(ServiceInformation info) {
		ServiceType dataService = info.getServices().getService(0);
		String schemaDir = info.getBaseDirectory() + File.separator + "schema" + File.separator + dataService.getName();
		File resultTypeXSDFile = new File(schemaDir + File.separator + getResultTypeXSDFileName(dataService));
		return resultTypeXSDFile;
	}
}
