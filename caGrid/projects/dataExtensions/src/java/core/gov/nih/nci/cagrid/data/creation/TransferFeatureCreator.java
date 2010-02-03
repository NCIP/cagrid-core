package gov.nih.nci.cagrid.data.creation;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;
import gov.nih.nci.cagrid.data.QueryMethodConstants;
import gov.nih.nci.cagrid.data.TransferMethodConstants;
import gov.nih.nci.cagrid.data.transfer.service.globus.Cql2TransferDataServiceProviderImpl;
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
import java.io.FileFilter;
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
	        addTransferQueryMethods();
	        addTransferDirServiceProperty();
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


	private void addTransferQueryMethods() {
		CommonTools.addMethod(getMainService(), getTransferQueryMethod());
		CommonTools.addMethod(getMainService(), getCql2TransferQueryMethod());
	}
	
	
	private MethodType getTransferQueryMethod() {
	    // build the transferQuery method
        MethodType method = new MethodType();
        method.setName(TransferMethodConstants.TRANSFER_QUERY_METHOD_NAME);
        method.setDescription(TransferMethodConstants.TRANSFER_QUERY_METHOD_DESCRIPTION);
        method.setIsImported(true);
        method.setIsProvided(true);
        // input
        MethodTypeInputs transferInputs = new MethodTypeInputs();
        MethodTypeInputsInput queryParam = new MethodTypeInputsInput();
        queryParam.setName(QueryMethodConstants.QUERY_METHOD_PARAMETER_NAME);
        queryParam.setIsArray(false);
        queryParam.setQName(CqlSchemaConstants.CQL_QUERY_QNAME);
        queryParam.setDescription(QueryMethodConstants.QUERY_METHOD_PARAMETER_DESCRIPTION);
        transferInputs.setInput(new MethodTypeInputsInput[]{queryParam});
        method.setInputs(transferInputs);
        // output
        MethodTypeOutput transferOutput = new MethodTypeOutput();
        transferOutput.setIsArray(false);
        transferOutput.setQName(TransferMethodConstants.TRANSFER_CONTEXT_REFERENCE_QNAME);
        transferOutput.setDescription(TransferMethodConstants.TRANSFER_QUERY_METHOD_OUTPUT_DESCRIPTION);
        method.setOutput(transferOutput);
        // import info
        MethodTypeImportInformation transferImport = new MethodTypeImportInformation();
        transferImport.setPortTypeName(TransferDataServicePortType.class.getSimpleName());
        transferImport.setWsdlFile("TransferDataService.wsdl");
        transferImport.setInputMessage(TransferMethodConstants.TRANSFER_QUERY_METHOD_INPUT_MESSAGE);
        transferImport.setOutputMessage(TransferMethodConstants.TRANSFER_QUERY_METHOD_OUTPUT_MESSAGE);
        transferImport.setNamespace(TransferMethodConstants.TRANSFER_DATA_SERVICE_NAMESPACE);
        transferImport.setPackageName(TransferMethodConstants.TRANSFER_DATA_SERVICE_PACKAGE_NAME);
        method.setImportInformation(transferImport);
        // provider info
        MethodTypeProviderInformation transferProvider = new MethodTypeProviderInformation();
        transferProvider.setProviderClass(TransferDataServiceProviderImpl.class.getName());
        method.setProviderInformation(transferProvider);
        // exceptions
        MethodTypeExceptions methodExceptions = new MethodTypeExceptions();
        MethodTypeExceptionsException qpException = new MethodTypeExceptionsException(
            QueryMethodConstants.QUERY_PROCESSING_EXCEPTION_DESCRIPTION,
            QueryMethodConstants.QUERY_PROCESSING_EXCEPTION_NAME, 
            QueryMethodConstants.QUERY_PROCESSING_EXCEPTION_QNAME);
        MethodTypeExceptionsException mqException = new MethodTypeExceptionsException(
            QueryMethodConstants.MALFORMED_QUERY_EXCEPTION_DESCRIPTION,
            QueryMethodConstants.MALFORMED_QUERY_EXCEPTION_NAME, 
            QueryMethodConstants.MALFORMED_QUERY_EXCEPTION_QNAME);
        methodExceptions.setException(new MethodTypeExceptionsException[]{qpException, mqException});
        method.setExceptions(methodExceptions);
        return method;
	}
	
	
	private MethodType getCql2TransferQueryMethod() {
	    // build the executeTransferQuery method
        MethodType method = new MethodType();
        method.setName(TransferMethodConstants.CQL2_TRANSFER_QUERY_METHOD_NAME);
        method.setDescription(TransferMethodConstants.CQL2_TRANSFER_QUERY_METHOD_DESCRIPTION);
        method.setIsImported(true);
        method.setIsProvided(true);
        // input
        MethodTypeInputs transferInputs = new MethodTypeInputs();
        MethodTypeInputsInput queryParam = new MethodTypeInputsInput();
        queryParam.setName(QueryMethodConstants.QUERY_METHOD_PARAMETER_NAME);
        queryParam.setIsArray(false);
        queryParam.setQName(CqlSchemaConstants.CQL2_QUERY_QNAME);
        queryParam.setDescription(QueryMethodConstants.CQL2_QUERY_METHOD_PARAMETER_DESCRIPTION);
        transferInputs.setInput(new MethodTypeInputsInput[]{queryParam});
        method.setInputs(transferInputs);
        // output
        MethodTypeOutput transferOutput = new MethodTypeOutput();
        transferOutput.setIsArray(false);
        transferOutput.setQName(TransferMethodConstants.TRANSFER_CONTEXT_REFERENCE_QNAME);
        transferOutput.setDescription(TransferMethodConstants.TRANSFER_QUERY_METHOD_OUTPUT_DESCRIPTION);
        method.setOutput(transferOutput);
        // import info
        MethodTypeImportInformation transferImport = new MethodTypeImportInformation();
        transferImport.setPortTypeName(TransferMethodConstants.CQL2_TRANSFER_DATA_SERVICE_PORT_TYPE);
        transferImport.setWsdlFile("Cql2TransferDataService.wsdl");
        transferImport.setInputMessage(TransferMethodConstants.CQL2_TRANSFER_QUERY_METHOD_INPUT_MESSAGE);
        transferImport.setOutputMessage(TransferMethodConstants.CQL2_TRANSFER_QUERY_METHOD_OUTPUT_MESSAGE);
        transferImport.setNamespace(TransferMethodConstants.CQL2_TRANSFER_DATA_SERVICE_NAMESPACE);
        transferImport.setPackageName(TransferMethodConstants.CQL2_TRANSFER_DATA_SERVICE_PACKAGE_NAME);
        method.setImportInformation(transferImport);
        // provider info
        MethodTypeProviderInformation transferProvider = new MethodTypeProviderInformation();
        transferProvider.setProviderClass(Cql2TransferDataServiceProviderImpl.class.getName());
        method.setProviderInformation(transferProvider);
        // exceptions
        MethodTypeExceptions methodExceptions = new MethodTypeExceptions();
        MethodTypeExceptionsException qpException = new MethodTypeExceptionsException(
            QueryMethodConstants.QUERY_PROCESSING_EXCEPTION_DESCRIPTION,
            QueryMethodConstants.QUERY_PROCESSING_EXCEPTION_NAME, 
            QueryMethodConstants.QUERY_PROCESSING_EXCEPTION_QNAME);
        MethodTypeExceptionsException mqException = new MethodTypeExceptionsException(
            QueryMethodConstants.MALFORMED_QUERY_EXCEPTION_DESCRIPTION,
            QueryMethodConstants.MALFORMED_QUERY_EXCEPTION_NAME, 
            QueryMethodConstants.MALFORMED_QUERY_EXCEPTION_QNAME);
        methodExceptions.setException(new MethodTypeExceptionsException[]{qpException, mqException});
        method.setExceptions(methodExceptions);
        return method;
	}


	private void copySchemas() throws CreationExtensionException {
		// copy over the [Cql2]TransferDataService.wsdl files
		String serviceSchemaDir = getServiceSchemaDir();
		File dataExtensionSchemaDir = new File(ExtensionsLoader.getInstance().getExtensionsDir(), 
            "data" + File.separator + "schema" + File.separator + "Data");
		File[] transferWsdls = dataExtensionSchemaDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("TransferDataService.wsdl");
            }
        });
		for (File wsdl : transferWsdls) {
		    File wsdlOutFile = new File(serviceSchemaDir, wsdl.getName());
		    try {
		        Utils.copyFile(wsdl, wsdlOutFile);
	        } catch (Exception ex) {
	            throw new CreationExtensionException("Error copying transfer data service wsdls: " + ex.getMessage(), ex);
	        }
		}        
	}
	
	
	private void addTransferDirServiceProperty() {
	    if (!CommonTools.servicePropertyExists(
	        getServiceInformation().getServiceDescriptor(), 
	        TransferMethodConstants.TRANSFER_DISK_BUFFER_DIR_PROPERTY)) {
	        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(), 
	            TransferMethodConstants.TRANSFER_DISK_BUFFER_DIR_PROPERTY, "", false);
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
        // this checks that the transfer context has been created and the transferQuery method exists
        ServiceType transferContext = CommonTools.getService(
            getServiceInformation().getServices(), "TransferServiceContext");
        boolean contextOk = transferContext != null 
            && "org.cagrid.transfer.context".equals(transferContext.getPackageName());
        ServiceType mainService = getServiceInformation().getServices().getService(0);
        MethodType transferMethod = CommonTools.getMethod(
            mainService.getMethods(), TransferMethodConstants.TRANSFER_QUERY_METHOD_NAME);
        boolean methodOk = transferMethod != null;
        return contextOk && methodOk;
    }
}
