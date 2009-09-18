package gov.nih.nci.cagrid.data.creation;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.transfer.service.globus.TransferDataServiceProviderImpl;
import gov.nih.nci.cagrid.data.transfer.stubs.TransferDataServicePortType;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptions;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeProviderInformation;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;


/**
 * TransferFeatureCreator 
 * Adds the components needed for the caGrid Transfer
 * feature of data services
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 22, 2006
 * @version $Id: TransferFeatureCreator.java,v 1.1 2009-04-10 14:51:31 dervin Exp $
 */
public class TransferFeatureCreator extends FeatureCreator {
	public static final String TRANSFER_EXTENSION_NAME = "caGrid_Transfer";

	public TransferFeatureCreator(ServiceInformation info, ServiceType mainService) {
		super(info, mainService);
	}


	public void addFeature() throws CreationExtensionException {
	    if (!featureAlreadyCreated()) {
	        installTransferExtension();
	        copySchemas();
	        addTransferQueryMethod();
        }
	}


	private void installTransferExtension() throws CreationExtensionException {
		// verify the transfer extension is installed
		if (!transferExtensionInstalled()) {
			throw new CreationExtensionException("The required extension " + TRANSFER_EXTENSION_NAME
				+ " was not found to be installed.  Please install it and try creating your service again");
		}

		if (!transferExtensionUsed()) {
			System.out.println("Adding the caGrid Transfer extension to the service");
			// add the caGrid Transfer extension to the service model
            ExtensionTools.addExtensionToService(getServiceInformation(), TRANSFER_EXTENSION_NAME);
            // edit the introduce.properties file to add the transfer extension to the list
            Properties introduceProps = new Properties();
            File propsFile = new File(getServiceInformation().getBaseDirectory(), IntroduceConstants.INTRODUCE_PROPERTIES_FILE);
            try {
                FileInputStream fis = new FileInputStream(propsFile);
                introduceProps.load(fis);
                fis.close();
                String extensionsProperty = introduceProps.getProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS);
                extensionsProperty += "," + TRANSFER_EXTENSION_NAME;
                introduceProps.setProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS, extensionsProperty);
                FileOutputStream fos = new FileOutputStream(propsFile);
                introduceProps.store(fos, "Edited by " + TransferFeatureCreator.class.getName());
                fos.close();
            } catch (IOException ex) {
                throw new CreationExtensionException("Error editing " 
                    + propsFile.getAbsolutePath() + " : " + ex.getMessage(), ex);
            }
		}
	}


	private void addTransferQueryMethod() {
		// add the transferQuery method to the data service
		MethodType transferQueryMethod = new MethodType();
		transferQueryMethod.setName(DataServiceConstants.TRANSFER_QUERY_METHOD_NAME);
		transferQueryMethod.setDescription(DataServiceConstants.TRANSFER_QUERY_METHOD_DESCRIPTION);
		transferQueryMethod.setIsImported(true);
		transferQueryMethod.setIsProvided(true);
        // input
		MethodTypeInputs transferInputs = new MethodTypeInputs();
		MethodTypeInputsInput queryParam = new MethodTypeInputsInput();
		queryParam.setName(DataServiceConstants.QUERY_METHOD_PARAMETER_NAME);
		queryParam.setIsArray(false);
		queryParam.setQName(DataServiceConstants.CQL_QUERY_QNAME);
		queryParam.setDescription(DataServiceConstants.QUERY_METHOD_PARAMETER_DESCRIPTION);
		transferInputs.setInput(new MethodTypeInputsInput[]{queryParam});
		transferQueryMethod.setInputs(transferInputs);
        // output
		MethodTypeOutput transferOutput = new MethodTypeOutput();
		transferOutput.setIsArray(false);
        transferOutput.setQName(DataServiceConstants.TRANSFER_CONTEXT_REFERENCE_QNAME);
        transferOutput.setDescription(DataServiceConstants.TRANSFER_QUERY_METHOD_OUTPUT_DESCRIPTION);
		transferQueryMethod.setOutput(transferOutput);
		// import info
		MethodTypeImportInformation transferImport = new MethodTypeImportInformation();
		transferImport.setPortTypeName(TransferDataServicePortType.class.getSimpleName());
		transferImport.setWsdlFile("TransferDataService.wsdl");
		transferImport.setInputMessage(DataServiceConstants.TRANSFER_QUERY_METHOD_INPUT_MESSAGE);
		transferImport.setOutputMessage(DataServiceConstants.TRANSFER_QUERY_METHOD_OUTPUT_MESSAGE);
		transferImport.setNamespace(DataServiceConstants.TRANSFER_DATA_SERVICE_NAMESPACE);
		transferImport.setPackageName(DataServiceConstants.TRANSFER_DATA_SERVICE_PACKAGE_NAME);
		transferQueryMethod.setImportInformation(transferImport);
		// provider info
		MethodTypeProviderInformation transferProvider = new MethodTypeProviderInformation();
		transferProvider.setProviderClass(TransferDataServiceProviderImpl.class.getName());
		transferQueryMethod.setProviderInformation(transferProvider);
		// exceptions
		MethodTypeExceptions methodExceptions = new MethodTypeExceptions();
		MethodTypeExceptionsException qpException = new MethodTypeExceptionsException(
			DataServiceConstants.QUERY_PROCESSING_EXCEPTION_DESCRIPTION,
			DataServiceConstants.QUERY_PROCESSING_EXCEPTION_NAME, 
			DataServiceConstants.QUERY_PROCESSING_EXCEPTION_QNAME);
		MethodTypeExceptionsException mqException = new MethodTypeExceptionsException(
			DataServiceConstants.MALFORMED_QUERY_EXCEPTION_DESCRIPTION,
			DataServiceConstants.MALFORMED_QUERY_EXCEPTION_NAME, 
			DataServiceConstants.MALFORMED_QUERY_EXCEPTION_QNAME);
		methodExceptions.setException(new MethodTypeExceptionsException[]{qpException, mqException});
		transferQueryMethod.setExceptions(methodExceptions);
		// add the method to the service
		CommonTools.addMethod(getMainService(), transferQueryMethod);
	}


	private void copySchemas() throws CreationExtensionException {
		// copy over the TransferDataService.wsdl file
		String serviceSchemaDir = getServiceSchemaDir();
		File dataExtensionSchemaDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY, 
            "data" + File.separator + "schema");
        File wsdlFile = new File(dataExtensionSchemaDir, 
            "Data" + File.separator + "TransferDataService.wsdl");
        File wsdlOutFile = new File(serviceSchemaDir, wsdlFile.getName());
        try {
			Utils.copyFile(wsdlFile, wsdlOutFile);
        } catch (Exception ex) {
			throw new CreationExtensionException("Error copying data service schemas: " + ex.getMessage(), ex);
		}
	}


	private boolean transferExtensionInstalled() {
		List extensionDescriptors = ExtensionsLoader.getInstance().getServiceExtensions();
		for (int i = 0; i < extensionDescriptors.size(); i++) {
			ServiceExtensionDescriptionType ex = (ServiceExtensionDescriptionType) extensionDescriptors.get(i);
			if (ex.getName().equals(TRANSFER_EXTENSION_NAME)) {
				return true;
			}
		}
		return false;
	}


	private boolean transferExtensionUsed() {
		ServiceDescription desc = getServiceInformation().getServiceDescriptor();
		if ((desc.getExtensions() != null) && (desc.getExtensions().getExtension() != null)) {
			for (int i = 0; i < desc.getExtensions().getExtension().length; i++) {
				if (desc.getExtensions().getExtension(i).getName().equals(TRANSFER_EXTENSION_NAME)) {
					return true;
				}
			}
		}
		return false;
	}


	private String getServiceSchemaDir() {
	    return new File(getServiceInformation().getBaseDirectory(),
            "schema" + File.separator + getMainService().getName()).getAbsolutePath();
	}
    
    
    private boolean featureAlreadyCreated() {
        // does the transfer service context exist?
        // TODO: this only checks that the transfer context has been created.... need to check for the transferQuery method
        ServiceType service = CommonTools.getService(
            getServiceInformation().getServices(), "TransferServiceContext");
        if (service != null) {
            return "org.cagrid.transfer.context".equals(service.getPackageName());
        }
        return false;
    }
}
