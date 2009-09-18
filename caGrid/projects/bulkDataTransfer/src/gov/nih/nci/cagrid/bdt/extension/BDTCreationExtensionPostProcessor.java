package gov.nih.nci.cagrid.bdt.extension;

import gov.nih.nci.cagrid.bdt.service.BDTServiceConstants;
import gov.nih.nci.cagrid.bdt.templates.BDTResourceTemplate;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeProviderInformation;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.Custom;
import gov.nih.nci.cagrid.introduce.beans.service.ResourceFrameworkOptions;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;


public class BDTCreationExtensionPostProcessor implements CreationExtensionPostProcessor {
    public static final String WS_ENUM_EXTENSION_NAME = "cagrid_wsEnum";

	
	public void postCreate(ServiceExtensionDescriptionType desc, ServiceInformation serviceInfo)
		throws CreationExtensionException {
	    
        checkServiceNaming(serviceInfo);
        
        // add the ws-enumeration extension
        installWsEnumExtension(serviceInfo);

        // apply BDT service requirements to it
		try {
			System.out.println("Adding BDT service components to template");
			makeBDTService(serviceInfo);
			addResourceImplStub(serviceInfo);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CreationExtensionException(
				"Error adding BDT service components to template! " + ex.getMessage(), ex);
		}
		// add the proper deployment metadata
		try {
			System.out.println("Modifying metadata");
			modifyServiceProperties(serviceInfo);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CreationExtensionException(
                "Error modifying metadata: " + ex.getMessage(), ex);
		}
	}

    
    private void checkServiceNaming(ServiceInformation serviceInfo) throws CreationExtensionException {
        ServiceType mainService = serviceInfo.getServices().getService(0);
        if (BDTServiceConstants.BDT_SERVICE_NAME.equals(mainService.getName())) {
            throw new CreationExtensionException(
                "The BDT infrastructure already makes use of the Service Name " + BDTServiceConstants.BDT_SERVICE_NAME);
        }
        if (BDTServiceConstants.BDT_SERVICE_PACKAGE.equals(mainService.getPackageName())) {
            throw new CreationExtensionException(
                "The BDT infrastructure already makes use of the package name " + BDTServiceConstants.BDT_SERVICE_PACKAGE);
        }
        if (BDTServiceConstants.BDT_SERVICE_NAMESPACE.equals(mainService.getNamespace())) {
            throw new CreationExtensionException(
                "The BDT infrastructure already makes use of the namespace " + BDTServiceConstants.BDT_SERVICE_NAMESPACE);
        }
    }

    
	private void makeBDTService(ServiceInformation info) throws Exception {
		String schemaDir = getServiceSchemaDir(info);
		File schemaDirFile = new File(schemaDir);
		System.out.println("Copying schemas to " + schemaDir);
		File extensionSchemaDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "bdt"
			+ File.separator + "schema");
		File extensionDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "bdt");
		List schemaFiles = Utils.recursiveListFiles(extensionSchemaDir, new FileFilters.XSDFileFilter());
		for (int i = 0; i < schemaFiles.size(); i++) {
			File schemaFile = (File) schemaFiles.get(i);
			String subname = schemaFile.getCanonicalPath().substring(
				extensionSchemaDir.getCanonicalPath().length() + File.separator.length());
			copySchema(subname, schemaDir);
		}

		List wsdlFiles = Utils.recursiveListFiles(extensionSchemaDir, new FileFilters.WSDLFileFilter());
		for (int i = 0; i < wsdlFiles.size(); i++) {
			File wsdlFile = (File) wsdlFiles.get(i);
			String subname = wsdlFile.getCanonicalPath().substring(
				extensionSchemaDir.getCanonicalPath().length() + File.separator.length());
			copySchema(subname, schemaDir);
		}

		// copy libraries for data services into the new bdt lib directory
		copyLibraries(info);
		// namespaces
		System.out.println("Modifying namespace definitions");
		NamespacesType namespaces = info.getServiceDescriptor().getNamespaces();
		if (namespaces == null) {
			namespaces = new NamespacesType();
		}

		// add some namespaces to the service
		List<NamespaceType> bdtNamespaces = new ArrayList<NamespaceType>(Arrays.asList(namespaces.getNamespace()));
		// metadata
		NamespaceType metadataNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
			+ BDTServiceConstants.METADATA_SCHEMA, schemaDirFile);
		// metadataNamespace.setGenerateStubs(new Boolean(false));
		metadataNamespace.setPackageName("gov.nih.nci.cagrid.bdt.beans.metadata");
		//base reference
		NamespaceType refNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
			+ BDTServiceConstants.BDT_REF_SCHEMA, schemaDirFile);
		refNamespace.setGenerateStubs(new Boolean(false));
		refNamespace.setPackageName("gov.nih.nci.cagrid.bdt.stubs.reference");
		// transfer
		NamespaceType transferNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
			+ BDTServiceConstants.TRANSFER_SCHEMA, schemaDirFile);
		transferNamespace.setGenerateStubs(new Boolean(false));
		transferNamespace.setPackageName("org.globus.transfer");
		// enumeration
		NamespaceType enumerationNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
			+ BDTServiceConstants.ENUMERATION_SCHEMA, schemaDirFile);
		enumerationNamespace.setGenerateStubs(new Boolean(false));
		enumerationNamespace.setPackageName("org.xmlsoap.schemas.ws._2004._09.enumeration");
		// new addressing
		NamespaceType addressingNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
			+ BDTServiceConstants.ADDRESSING_SCHEMA, schemaDirFile);
		addressingNamespace.setGenerateStubs(new Boolean(false));
		addressingNamespace.setPackageName("org.globus.addressing");
        // enumeration response container
        NamespaceType responseContainerNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
            + BDTServiceConstants.ENUMERATION_RESPONSE_CONTAINER_SCHEMA, schemaDirFile);
        // responseContainerNamespace.setGenerateStubs(new Boolean(true));
        responseContainerNamespace.setPackageName("gov.nih.nci.cagrid.enumeration.stubs.response");
        
		bdtNamespaces.add(metadataNamespace);
		bdtNamespaces.add(refNamespace);
		bdtNamespaces.add(transferNamespace);
		bdtNamespaces.add(enumerationNamespace);
		bdtNamespaces.add(addressingNamespace);
        bdtNamespaces.add(responseContainerNamespace);

		NamespaceType[] nsArray = new NamespaceType[bdtNamespaces.size()];
		bdtNamespaces.toArray(nsArray);
		namespaces.setNamespace(nsArray);
		info.getServiceDescriptor().setNamespaces(namespaces);

		ServiceType mainService = info.getServices().getService(0);

		// add the bdt subservice
		ServiceDescription desc = (ServiceDescription) Utils.deserializeDocument(extensionDir + File.separator
			+ "introduce.xml", ServiceDescription.class);
		ServiceType bdtService = desc.getServices().getService(0);
		bdtService.setName(mainService.getName() + bdtService.getName());
		bdtService.setNamespace(mainService.getNamespace() + "BDT");
		bdtService.setPackageName(mainService.getPackageName() + ".bdt");
		bdtService.setResourceFrameworkOptions(new ResourceFrameworkOptions());
		bdtService.getResourceFrameworkOptions().setCustom(new Custom());
		MethodType[] methods = bdtService.getMethods().getMethod();
		for (int i = 0; i < methods.length; i++) {
			MethodType method = methods[i];
			if (method.getName().equals("Get")) {
				method.setIsProvided(true);
				MethodTypeProviderInformation mpi = new MethodTypeProviderInformation();
				mpi.setProviderClass("gov.nih.nci.cagrid.bdt.service.globus.BulkDataHandlerProviderImpl");
				method.setProviderInformation(mpi);
			} else if (method.getName().equals("CreateEnumeration")) {
				method.setIsProvided(true);
				MethodTypeProviderInformation mpi = new MethodTypeProviderInformation();
				mpi.setProviderClass("gov.nih.nci.cagrid.bdt.service.globus.BulkDataHandlerProviderImpl");
				method.setProviderInformation(mpi);
				method.setIsImported(true);
				MethodTypeImportInformation mii = new MethodTypeImportInformation();
				mii.setFromIntroduce(Boolean.TRUE);
				mii.setInputMessage(new QName("http://cagrid.nci.nih.gov/BulkDataHandler", "CreateEnumerationRequest"));
				mii.setOutputMessage(new QName("http://cagrid.nci.nih.gov/BulkDataHandler",
					"CreateEnumerationResponse"));
				mii.setPackageName("gov.nih.nci.cagrid.bdt.stubs");
				mii.setNamespace("http://cagrid.nci.nih.gov/BulkDataHandler");
				mii.setPortTypeName("BulkDataHandlerPortType");
				mii.setWsdlFile("./BulkDataHandler.wsdl");
				method.setImportInformation(mii);
			} else if (method.getName().equals("GetGridFTPURLs")) {
				method.setIsProvided(true);
				MethodTypeProviderInformation mpi = new MethodTypeProviderInformation();
				mpi.setProviderClass("gov.nih.nci.cagrid.bdt.service.globus.BulkDataHandlerProviderImpl");
				method.setProviderInformation(mpi);
				method.setIsImported(true);
				MethodTypeImportInformation mii = new MethodTypeImportInformation();
				mii.setFromIntroduce(Boolean.TRUE);
				mii.setInputMessage(new QName("http://cagrid.nci.nih.gov/BulkDataHandler", "GetGridFTPURLsRequest"));
				mii.setOutputMessage(new QName("http://cagrid.nci.nih.gov/BulkDataHandler", "GetGridFTPURLsResponse"));
				mii.setPackageName("gov.nih.nci.cagrid.bdt.stubs");
				mii.setNamespace("http://cagrid.nci.nih.gov/BulkDataHandler");
				mii.setPortTypeName("BulkDataHandlerPortType");
				mii.setWsdlFile("./BulkDataHandler.wsdl");
				method.setImportInformation(mii);
			}
		}

		ServiceType[] services = info.getServices().getService();
        services = (ServiceType[]) Utils.appendToArray(services, bdtService);
		info.getServices().setService(services);
	}


	private void addResourceImplStub(ServiceInformation info) throws Exception {
		BDTResourceTemplate resourceTemplate = new BDTResourceTemplate();
		String resourceS = resourceTemplate.generate(
            new SpecificServiceInformation(info, info.getServices().getService(0)));
		File resourceF = new File(info.getBaseDirectory().getAbsolutePath()
			+ File.separator + "src" + File.separator + CommonTools.getPackageDir(info.getServices().getService(0))
			+ File.separator + "service" + File.separator + "BDTResource.java");
		FileWriter resourceFW = new FileWriter(resourceF);
		resourceFW.write(resourceS);
		resourceFW.close();
	}


	private void addServiceMetadata(ServiceInformation info) throws CreationExtensionException {
		ResourcePropertyType serviceMetadata = new ResourcePropertyType();
		serviceMetadata.setPopulateFromFile(true); // no metadata file yet...
		serviceMetadata.setRegister(true);
		serviceMetadata.setFileLocation("./BulkDataHandler-metadata.xml");
		serviceMetadata.setQName(BDTServiceConstants.METADATA_QNAME);
		ResourcePropertiesListType propsList = info.getServices().getService(0).getResourcePropertiesList();
		if (propsList == null) {
			propsList = new ResourcePropertiesListType();
			info.getServices().getService(0).setResourcePropertiesList(propsList);
		}
		ResourcePropertyType[] metadataArray = propsList.getResourceProperty();
		if ((metadataArray == null) || (metadataArray.length == 0)) {
			metadataArray = new ResourcePropertyType[]{serviceMetadata};
		} else {
            metadataArray = (ResourcePropertyType[]) Utils.appendToArray(metadataArray, serviceMetadata);
		}
		propsList.setResourceProperty(metadataArray);

		try {
            File baseBdtMetadata = new File(ExtensionsLoader.getInstance().getExtensionsDir() 
                + File.separator + "bdt" + File.separator
                + "etc" + File.separator + "BulkDataHandler-metadata.xml");
            File serviceBdtMetadata = new File(info.getBaseDirectory().getAbsolutePath()
                + File.separator + "etc" + File.separator + "BulkDataHandler-metadata.xml");
			Utils.copyFile(baseBdtMetadata, serviceBdtMetadata);
		} catch (IOException ex) {
			throw new CreationExtensionException("Error copying BDT metadata to service: " 
                + ex.getMessage(), ex);
		}
	}


	private boolean serviceMetadataExists(ServiceInformation info) {
		ResourcePropertiesListType propsList = info.getServices().getService(0).getResourcePropertiesList();
		if (propsList == null) {
			return false;
		}
		ResourcePropertyType[] props = propsList.getResourceProperty();
		if ((props == null) || (props.length == 0)) {
			return false;
		}
		for (int i = 0; i < props.length; i++) {
			if (props[i].getQName().equals(BDTServiceConstants.METADATA_QNAME)) {
				return true;
			}
		}
		return false;
	}


	private String getServiceSchemaDir(ServiceInformation info) {
		return info.getBaseDirectory().getAbsolutePath() + File.separator + "schema"
			+ File.separator + info.getServices().getService(0).getName();
	}


	private String getServiceLibDir(ServiceInformation info) {
		return info.getBaseDirectory().getAbsolutePath() + File.separator + "lib";
	}


	private void copySchema(String schemaName, String outputDir) throws Exception {
		File schemaFile = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "bdt" + File.separator
			+ "schema" + File.separator + schemaName);
		System.out.println("Copying schema from " + schemaFile.getAbsolutePath());
		File outputFile = new File(outputDir + File.separator + schemaName);
		System.out.println("Saving schema to " + outputFile.getAbsolutePath());
		Utils.copyFile(schemaFile, outputFile);
	}


	private void copyLibraries(ServiceInformation info) throws Exception {
		String toDir = getServiceLibDir(info);
		File directory = new File(toDir);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		// from the lib directory
		File libDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "lib");
		File[] libs = libDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String name = pathname.getName();
				return (name.endsWith(".jar") && (name.startsWith("caGrid-BulkDataHandler")
					|| name.startsWith("wsrf_core_enum") || name.startsWith("wsrf_core_stubs_enum")));
			}
		});
		File[] copiedLibs = new File[libs.length];
		if (libs != null) {
			for (int i = 0; i < libs.length; i++) {
				File outFile = new File(toDir + File.separator + libs[i].getName());
				copiedLibs[i] = outFile;
				Utils.copyFile(libs[i], outFile);
			}
		}
		modifyClasspathFile(copiedLibs, info);
	}
    
    
    private void installWsEnumExtension(ServiceInformation info) throws CreationExtensionException {
        // verify the ws-enum extension is installed
        if (!wsEnumExtensionInstalled()) {
            throw new CreationExtensionException("The required extension " + WS_ENUM_EXTENSION_NAME
                + " was not found to be installed.  Please install it and try creating your service again");
        }

        if (!wsEnumExtensionUsed(info)) {
            System.out.println("Adding the WS-Enumeration extension to the service");
            // add the WS-Enumeration extension
            ExtensionTools.addExtensionToService(info, WS_ENUM_EXTENSION_NAME);
            // order the extensions so WS-Enumeration runs before BDT
            List<ExtensionType> extensions = new ArrayList<ExtensionType>();
            Collections.addAll(extensions, info.getExtensions().getExtension());
            int bdtIndex = -1;
            int enumIndex = -1;
            for (int i = 0; i < extensions.size(); i++) {
                ExtensionType extension = extensions.get(i);
                if (extension.getName().equals("bdt")) {
                    bdtIndex = i;
                } else if (extension.getName().equals(WS_ENUM_EXTENSION_NAME)) {
                    enumIndex = i;
                }
            }
            if (bdtIndex < enumIndex) {
                System.out.println("Reordering extensions so enumeration runs before BDT");
                Collections.swap(extensions, bdtIndex, enumIndex);
                info.getExtensions().setExtension(extensions.toArray(new ExtensionType[0]));
            }
            // execute the extension, since it won't run otherwise
            executeWsEnumExtension(info);
        }
    }
    
    
    private boolean wsEnumExtensionInstalled() {
        List extensionDescriptors = ExtensionsLoader.getInstance().getServiceExtensions();
        for (int i = 0; i < extensionDescriptors.size(); i++) {
            ServiceExtensionDescriptionType ex = (ServiceExtensionDescriptionType) extensionDescriptors.get(i);
            if (ex.getName().equals(WS_ENUM_EXTENSION_NAME)) {
                return true;
            }
        }
        return false;
    }


    private boolean wsEnumExtensionUsed(ServiceInformation info) {
        ServiceDescription desc = info.getServiceDescriptor();
        if ((desc.getExtensions() != null) && (desc.getExtensions().getExtension() != null)) {
            for (int i = 0; i < desc.getExtensions().getExtension().length; i++) {
                if (desc.getExtensions().getExtension(i).getName().equals(WS_ENUM_EXTENSION_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    private void executeWsEnumExtension(ServiceInformation info) throws CreationExtensionException{
        // invoke
        CreationExtensionPostProcessor pp = null;
        ServiceExtensionDescriptionType desc = ExtensionsLoader.getInstance()
            .getServiceExtension(WS_ENUM_EXTENSION_NAME);
        try {
            pp = ExtensionTools.getCreationPostProcessor(WS_ENUM_EXTENSION_NAME);
        } catch (Exception ex) {
            throw new CreationExtensionException("Error loading " + WS_ENUM_EXTENSION_NAME 
                + " creation post processor: " + ex.getMessage(), ex);
        }
        if (pp != null) {
            pp.postCreate(desc, info);
        } else {
            System.out.println(WS_ENUM_EXTENSION_NAME + " did not provide a creation post processing class");
        }
    }


	private void modifyClasspathFile(File[] libs, ServiceInformation info) throws Exception {
		File classpathFile = new File(info.getBaseDirectory().getAbsolutePath()
			+ File.separator + ".classpath");
		ExtensionUtilities.syncEclipseClasspath(classpathFile, libs);
	}


	private void modifyServiceProperties(ServiceInformation info) throws Exception {
		if (!serviceMetadataExists(info)) {
			addServiceMetadata(info);
		}
	}
}
