package gov.nih.nci.cagrid.data.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.cql.utilities.CQL2WsddTypesUtil;

import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.style.StyleCodegenPostProcessor;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;


/**
 * DataServiceCodegenPostProcessor 
 * Post-processor for dataservice code generation
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Mar 29, 2006
 * @version $Id: DataServiceOperationProviderCodegenPostProcessor.java,v 1.4 2009-01-13 15:55:19 dervin Exp $
 */
public class DataServiceOperationProviderCodegenPostProcessor extends BaseCodegenPostProcessorExtension {

	public void performCodegenProcess(ServiceExtensionDescriptionType desc, ServiceInformation info)
		throws CodegenExtensionException {
		// add the necessary jars to the eclipse .classpath
		modifyEclipseClasspath(desc, info);
		
		// get the data service extension data
		Data extensionData = getExtensionData(desc, info);

		// create the XSD with the group of allowable return types for the service
		ResultTypeGeneratorInformation typeInfo = new ResultTypeGeneratorInformation();
		typeInfo.setServiceInfo(info);
		typeInfo.setModelInformation(extensionData.getModelInformation());
		CQLResultTypesGenerator.generateCQLResultTypesXSD(typeInfo);
		
		// create the class to QName mapping
		generateClassToQnameMapping(extensionData, info);
		
		// handle service feature modifications
		if (extensionData.getServiceFeatures() != null 
		    && extensionData.getServiceFeatures().getServiceStyle() != null) {
		    // if a style provided a codegen post processor, execute it here
		    String styleName = extensionData.getServiceFeatures().getServiceStyle().getName();
		    try {
		        ServiceStyleContainer styleContainer = ServiceStyleLoader.getStyle(styleName);
		        if (styleContainer == null) {
		            throw new CreationExtensionException("Could not load service style " + styleName);
		        }
		        StyleCodegenPostProcessor stylePostProcessor = styleContainer.loadCodegenPostProcessor();
		        if (stylePostProcessor != null) {
		            stylePostProcessor.codegenPostProcessStyle(desc, info);
		        }
		    } catch (Exception ex) {
		        throw new CodegenExtensionException(
		            "Error executing style codegen post processor: " + ex.getMessage(), ex);
		    }
		}
		
		// put the CQL 2 types in the wsdds
		modifyWsdd(info);
	}
	
	
	private void modifyWsdd(ServiceInformation info) throws CodegenExtensionException {
	    List<File> wsdds = new ArrayList<File>();
	    // add the server wsdd
	    wsdds.add(new File(info.getBaseDirectory(), "server-config.wsdd"));
	    // add the client-config.wsdd for every service context
	    for (ServiceType service : info.getServices().getService()) {
	        File clientWSDD = new File(info.getBaseDirectory(), "src" + File.separator
	            + CommonTools.getPackageDir(service) + File.separator + "client" + File.separator
	            + "client-config.wsdd");
	        wsdds.add(clientWSDD);
	    }
	    
	    for (File wsdd : wsdds) {
	        try {
                CQL2WsddTypesUtil.addCql2TypesMappingToWsdd(wsdd);
            } catch (IOException ex) {
                throw new CodegenExtensionException("Error adding CQL 2 types mapping to wsdd " 
                    + wsdd.getAbsolutePath() + ": " + ex.getMessage(), ex);
            }
	    }
	}
}
