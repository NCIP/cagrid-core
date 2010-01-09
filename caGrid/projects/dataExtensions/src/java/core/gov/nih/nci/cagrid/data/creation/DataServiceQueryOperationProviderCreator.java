package gov.nih.nci.cagrid.data.creation;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.MetadataConstants;
import gov.nih.nci.cagrid.data.QueryMethodConstants;
import gov.nih.nci.cagrid.data.ServiceNamingConstants;
import gov.nih.nci.cagrid.data.codegen.CQLResultTypesGenerator;
import gov.nih.nci.cagrid.data.codegen.ResultTypeGeneratorInformation;
import gov.nih.nci.cagrid.data.cql.validation.DomainModelValidator;
import gov.nih.nci.cagrid.data.cql.validation.ObjectWalkingCQLValidator;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.data.service.globus.Cql2DataServiceProviderImpl;
import gov.nih.nci.cagrid.data.service.globus.DataServiceProviderImpl;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.style.StyleCreationPostProcessor;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptions;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeProviderInformation;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * DataServiceQueryOperationProviderCreator 
 * Adds the operation provider for the data service query operation to 
 * an introduce created service at post-create time
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Jun 15, 2006
 * @version $Id: DataServiceQueryOperationProviderCreator.java,v 1.3 2007/07/18
 *          14:30:53 dervin Exp $
 */
public class DataServiceQueryOperationProviderCreator implements CreationExtensionPostProcessor {

    public static final String DEFAULT_CQL_VALIDATOR_CLASS = ObjectWalkingCQLValidator.class.getName();
    public static final String DEFAULT_DOMAIN_MODEL_VALIDATOR = DomainModelValidator.class.getName();

    private static Log log = LogFactory.getLog(DataServiceQueryOperationProviderCreator.class);


    public void postCreate(ServiceExtensionDescriptionType desc, ServiceInformation serviceInfo)
        throws CreationExtensionException {
        // check the naming of the service for problems
        checkServiceNaming(serviceInfo);
        // only need to run if the query operation does not exist
        if (!queryOperationCreated(serviceInfo)) {
            ServiceType mainService = serviceInfo.getServices().getService(0);

            // copy schemas
            copyDataServiceSchemas(serviceInfo);
            // copy libraries
            copyDataServiceLibraries(serviceInfo);
            // generate the allowable result types schema
            ResultTypeGeneratorInformation typeInfo = new ResultTypeGeneratorInformation();
            typeInfo.setServiceInfo(serviceInfo);
            try {
                CQLResultTypesGenerator.generateCQLResultTypesXSD(typeInfo);
            } catch (CodegenExtensionException ex) {
                throw new CreationExtensionException(
                    "Problen creating initial permissible result type XSD.", ex);
            }
            // add the data namespaces
            addDataServiceNamespaces(serviceInfo);
            // edit service properties
            modifyServiceProperties(serviceInfo);
            // add the query method
            addQueryMethods(mainService);
            // features and service style
            processFeatures(serviceInfo, mainService, desc);
        }
    }


    private void checkServiceNaming(ServiceInformation serviceInfo) throws CreationExtensionException {
        ServiceType mainService = serviceInfo.getServices().getService(0);
        if (ServiceNamingConstants.DATA_SERVICE_SERVICE_NAME.equals(mainService.getName())) {
            throw new CreationExtensionException(
                "The data service infrastructure already makes use of the Service Name "
                    + ServiceNamingConstants.DATA_SERVICE_SERVICE_NAME);
        }
        if (ServiceNamingConstants.DATA_SERVICE_PACKAGE.equals(mainService.getPackageName())) {
            throw new CreationExtensionException(
                "The data service infrastructure already makes use of the package name "
                    + ServiceNamingConstants.DATA_SERVICE_PACKAGE);
        }
        if (ServiceNamingConstants.DATA_SERVICE_NAMESPACE.equals(mainService.getNamespace())) {
            throw new CreationExtensionException(
                "The data service infrastructure already makes use of the namespace "
                    + ServiceNamingConstants.DATA_SERVICE_NAMESPACE);
        }
    }


    private void copyDataServiceSchemas(ServiceInformation serviceInfo) throws CreationExtensionException {
        // grab cql query and result set schemas and move them into the
        // service's directory
        String schemaDir = getServiceSchemaDir(serviceInfo);
        log.debug("Copying schemas to " + schemaDir);
        FileFilter dataXsdFilter = new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory() || pathname.getName().endsWith(".xsd")) {
                    return !pathname.getName().equals(WsEnumConstants.ENUMERATION_WSDL_NAME)
                        && !pathname.getName().equals(WsEnumConstants.ENUMERATION_XSD_NAME)
                        && !pathname.getName().equals(WsEnumConstants.ADDRESSING_XSD_NAME);
                }
                return false;
            }
        };
        // get schemas for data services
        File baseSchemaDir = new File(ExtensionsLoader.getInstance().getExtensionsDir(),
            "data" + File.separator + "schema");
        File dataSchemaDir = new File(baseSchemaDir, "Data");
        List<File> dataSchemaFiles = Utils.recursiveListFiles(dataSchemaDir, dataXsdFilter);
        // also copy the WSDLs for data services
        dataSchemaFiles.add(new File(dataSchemaDir, "DataService.wsdl"));
        dataSchemaFiles.add(new File(dataSchemaDir, "Cql2DataService.wsdl"));
        try {
            for (File schemaFile : dataSchemaFiles) {
                String subname = schemaFile.getAbsolutePath().substring(
                    dataSchemaDir.getAbsolutePath().length() + File.separator.length());
                File schemaOut = new File(schemaDir + File.separator + subname);
                Utils.copyFile(schemaFile, schemaOut);
            }
        } catch (Exception ex) {
            throw new CreationExtensionException(
                "Error copying data service schemas: " + ex.getMessage(), ex);
        }
    }


    private void addDataServiceNamespaces(ServiceInformation serviceInfo) throws CreationExtensionException {
        String schemaDir = getServiceSchemaDir(serviceInfo);
        File schemaDirFile = new File(schemaDir);
        // add the data service namespaces and schemas to the service descriptor
        try {
            // CQL query namespace
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(), 
                CommonTools.createNamespaceType(schemaDir + File.separator
                    + CqlSchemaConstants.CQL_QUERY_SCHEMA, schemaDirFile));
            // CQL 2 query namespace
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(),
                CommonTools.createNamespaceType(schemaDir + File.separator
                    + CqlSchemaConstants.CQL2_SCHEMA_FILENAME, schemaDirFile));
            // CQL result namespace
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(),
                CommonTools.createNamespaceType(schemaDir + File.separator
                    + CqlSchemaConstants.CQL_RESULT_SET_SCHEMA, schemaDirFile));
            // CQL 2 result namespace
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(),
                CommonTools.createNamespaceType(schemaDir + File.separator
                    + CqlSchemaConstants.CQL2_RESULTS_SCHEMA_FILENAME, schemaDirFile));
            // results restriction namespace
            NamespaceType restrictionNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
                + CQLResultTypesGenerator.getResultTypeXSDFileName(
                    getDataService(serviceInfo.getServiceDescriptor())),
                    schemaDirFile);
            restrictionNamespace.setPackageName(
                serviceInfo.getServiceDescriptor().getServices().getService(0).getPackageName()
                + ".cqlresulttypes");
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(), restrictionNamespace);
            // Domain Model namespace
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(),
                CommonTools.createNamespaceType(schemaDir + File.separator
                    + MetadataConstants.DATA_METADATA_SCHEMA, schemaDirFile));
            // caGrid metadata namespace
            NamespaceType caGridMdNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
                + MetadataConstants.CAGRID_METADATA_SCHEMA, schemaDirFile);
            caGridMdNamespace.setGenerateStubs(Boolean.FALSE);
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(), caGridMdNamespace);
            // Query language support metadata namespace
            NamespaceType supportNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
                + MetadataConstants.QUERY_LANGUAGE_SUPPORT_XSD, schemaDirFile);
            supportNamespace.setPackageName(MetadataConstants.QUERY_LANGUAGE_SUPPORT_PACKAGE);
            supportNamespace.setGenerateStubs(Boolean.FALSE);
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(), supportNamespace);
            // ds exceptions namespace
            NamespaceType exceptionsNamespace = CommonTools.createNamespaceType(schemaDir + File.separator
                + QueryMethodConstants.DATA_SERVICE_EXCEPTIONS_SCHEMA, schemaDirFile);
            exceptionsNamespace.setGenerateStubs(Boolean.FALSE);
            exceptionsNamespace.setPackageName(DataServiceConstants.DATA_SERVICE_PACKAGE + ".faults");
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(), exceptionsNamespace);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CreationExtensionException(
                "Error creating namespace for data service: " + ex.getMessage(), ex);
        }
    }


    private ServiceType getDataService(ServiceDescription serviceDescription) throws CreationExtensionException {
        String serviceName = serviceDescription.getServices().getService(0).getName();
        ServiceType mainService = CommonTools.getService(serviceDescription.getServices(), serviceName);
        if (mainService == null) {
            throw new CreationExtensionException("No main service could be located!");
        }
        return mainService;
    }


    private void addQueryMethods(ServiceType service) {
        MethodType queryMethod = createCql1QueryMethod();
        MethodType cql2QueryMethod = createCql2QueryMethod();
        // add the query method to the service
        CommonTools.addMethod(service, queryMethod);
        CommonTools.addMethod(service, cql2QueryMethod);
    }


    private void copyDataServiceLibraries(ServiceInformation serviceInfo) throws CreationExtensionException {
        String toDir = getServiceLibDir(serviceInfo);
        File directory = new File(toDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // from the lib directory
        File libDir = new File(ExtensionsLoader.getInstance().getExtensionsDir(), "lib");
        File[] libs = libDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return (name.endsWith(".jar") && 
                    ((name.startsWith("caGrid-data-") || name.startsWith("caGrid-CQL-")
                    || name.startsWith("caGrid-core-") || name.startsWith("caGrid-caDSR-") 
                    || name.startsWith("caGrid-metadata-") || name.startsWith("caGrid-mms-"))));
            }
        });
        File[] copiedLibs = new File[libs.length];
        try {
            if (libs != null) {
                for (int i = 0; i < libs.length; i++) {
                    File outFile = new File(toDir + File.separator + libs[i].getName());
                    copiedLibs[i] = outFile;
                    Utils.copyFile(libs[i], outFile);
                }
            }
        } catch (Exception ex) {
            throw new CreationExtensionException(
                "Error copying data service libraries: " + ex.getMessage(), ex);
        }
        try {
            modifyClasspathFile(copiedLibs, serviceInfo);
        } catch (Exception ex) {
            throw new CreationExtensionException(
                "Error modifying eclipse .classpath file: " + ex.getMessage(), ex);
        }
    }


    private String getServiceSchemaDir(ServiceInformation serviceInfo) {
        String schemaDir = serviceInfo.getBaseDirectory().getAbsolutePath()
            + File.separator + "schema";
        String serviceName = serviceInfo.getServiceDescriptor().getServices().getService(0).getName();
        return schemaDir + File.separator + serviceName;
    }


    private String getServiceLibDir(ServiceInformation serviceInfo) {
        String libDir = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "lib";
        return libDir;
    }


    private void modifyClasspathFile(File[] libs, ServiceInformation serviceInfo) throws Exception {
        File classpathFile = new File(serviceInfo.getBaseDirectory(), ".classpath");
        if (classpathFile.exists()) {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, libs);
        } else {
            log.warn("The eclipse classpath file " + classpathFile.getAbsolutePath()
                + " was not found.  Was it deleted?");
        }
    }


    private void modifyServiceProperties(ServiceInformation info) throws CreationExtensionException {
        ServiceDescription desc = info.getServiceDescriptor();
        // does the query processor class property exist?
        if (!CommonTools.servicePropertyExists(desc, DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY)) {
            // set the service property to the stub query processor class name
            String stubQpClassname = ExtensionDataUtils.getQueryProcessorStubClassName(info);
            CommonTools.setServiceProperty(desc, DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY,
                stubQpClassname, false);
        } else {
            try {
                String value = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
                log.debug(DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY
                    + " property is already defined as " + value);
            } catch (Exception ex) {
                throw new CreationExtensionException("Error creating service property "
                    + DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
            }
        }
        // does the cql2 query processor class property exist?
        if (!CommonTools.servicePropertyExists(desc, DataServiceConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY)) {
            // set the service property to be empty
            CommonTools.setServiceProperty(desc, DataServiceConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY,
                "", false);
        } else {
            try {
                String value = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY);
                log.debug(DataServiceConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY
                    + " property is already defined as " + value);
            } catch (Exception ex) {
                throw new CreationExtensionException("Error creating service property "
                    + DataServiceConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY);
            }
        }
        // does the server config location property exist?
        if (!CommonTools.servicePropertyExists(desc, DataServiceConstants.SERVER_CONFIG_LOCATION)) {
            CommonTools.setServiceProperty(desc, DataServiceConstants.SERVER_CONFIG_LOCATION, 
                "server-config.wsdd", true, "The location of the server-config.wsdd file");
        }
        // validation properties
        CommonTools.setServiceProperty(desc, DataServiceConstants.CQL_VALIDATOR_CLASS, DEFAULT_CQL_VALIDATOR_CLASS,
            false, "The name of the class to use for CQL query structure validation");
        CommonTools.setServiceProperty(desc, DataServiceConstants.DOMAIN_MODEL_VALIDATOR_CLASS,
            DEFAULT_DOMAIN_MODEL_VALIDATOR, false,
            "The name of the class to use for CQL validation against a domain model");
        CommonTools.setServiceProperty(desc, DataServiceConstants.VALIDATE_CQL_FLAG,
            DataServiceConstants.DEFAULT_VALIDATE_CQL_FLAG, false,
            "A flag to indicate that CQL should be validated for structural correctness");
        CommonTools.setServiceProperty(desc, DataServiceConstants.VALIDATE_DOMAIN_MODEL_FLAG,
            DataServiceConstants.DEFAULT_VALIDATE_DOMAIN_MODEL_FLAG, false,
            "A flag to indicate that CQL should be validated for correctness against the domain model");
        // filename of class to qname mapping property
        CommonTools.setServiceProperty(desc, DataServiceConstants.CLASS_MAPPINGS_FILENAME,
            DataServiceConstants.CLASS_TO_QNAME_XML, true,
            "The name of the file containing the class name to QName mapping");
    }


    private void processFeatures(ServiceInformation info, ServiceType service,
        ServiceExtensionDescriptionType extensionDesc) throws CreationExtensionException {
        ExtensionTypeExtensionData extensionData = getExtensionData(info.getServiceDescriptor());
        ServiceFeatures features = null;
        try {
            features = ExtensionDataUtils.getExtensionData(extensionData).getServiceFeatures();
        } catch (Exception ex) {
            throw new CreationExtensionException("Error getting service features: " + ex.getMessage(), ex);
        }
        if (features != null) {
            // ws-enumeration
            if (features.isUseWsEnumeration()) {
                FeatureCreator wsEnumCreator = new WsEnumerationFeatureCreator(info, service);
                wsEnumCreator.addFeature();
            }
            // transfer
            if (features.isUseTransfer()) {
                FeatureCreator transferCreator = new TransferFeatureCreator(info, service);
                transferCreator.addFeature();
            }
            // service style
            if (features.getServiceStyle() != null) {
                try {
                    ServiceStyleContainer styleContainer = ServiceStyleLoader.getStyle(features.getServiceStyle().getName());
                    if (styleContainer == null) {
                        throw new CreationExtensionException("Could not load service style "
                            + features.getServiceStyle());
                    }
                    // copy libraries from the style into the service's lib directory
                    File[] copyStyleLibs = styleContainer.getStyleCopyLibs();
                    File serviceLibDir = new File(info.getBaseDirectory(), "lib");
                    for (File lib : copyStyleLibs) {
                        Utils.copyFile(lib, new File(serviceLibDir, lib.getName()));
                    }
                    StyleCreationPostProcessor processor = styleContainer.loadCreationPostProcessor();
                    if (processor != null) {
                        processor.creationPostProcessStyle(extensionDesc, info);
                    }
                    // sync up the eclipse .classpath file
                    ExtensionUtilities.resyncWithLibDir(new File(info.getBaseDirectory(), ".classpath"));
                } catch (Exception ex) {
                    throw new CreationExtensionException("Error executing style creation post processor: "
                        + ex.getMessage(), ex);
                }
            } else {
                log.info("No service style found");
            }
        } else {
            log.warn("No data service features information could be found!");
        }
    }


    private ExtensionTypeExtensionData getExtensionData(ServiceDescription desc) {
        for (int i = 0; i < desc.getExtensions().getExtension().length; i++) {
            ExtensionType ext = desc.getExtensions().getExtension(i);
            if (ext.getName().equals("data")) {
                if (ext.getExtensionData() == null) {
                    ext.setExtensionData(new ExtensionTypeExtensionData());
                }
                return ext.getExtensionData();
            }
        }
        return null;
    }


    public static MethodType createCql1QueryMethod() {
        MethodType queryMethod = new MethodType();
        queryMethod.setName(DataServiceConstants.QUERY_METHOD_NAME);
        queryMethod.setDescription(DataServiceConstants.QUERY_METHOD_DESCRIPTION);
        // method input parameters
        MethodTypeInputs inputs = new MethodTypeInputs();
        MethodTypeInputsInput queryInput = new MethodTypeInputsInput();
        queryInput.setName(DataServiceConstants.QUERY_METHOD_PARAMETER_NAME);
        queryInput.setIsArray(false);
        queryInput.setQName(DataServiceConstants.CQL_QUERY_QNAME);
        queryInput.setDescription(DataServiceConstants.QUERY_METHOD_PARAMETER_DESCRIPTION);
        inputs.setInput(new MethodTypeInputsInput[]{queryInput});
        queryMethod.setInputs(inputs);
        // method output
        MethodTypeOutput output = new MethodTypeOutput();
        output.setIsArray(false);
        output.setQName(DataServiceConstants.CQL_RESULT_COLLECTION_QNAME);
        output.setDescription(DataServiceConstants.QUERY_METHOD_OUTPUT_DESCRIPTION);
        queryMethod.setOutput(output);
        // exceptions on query method
        MethodTypeExceptions queryExceptions = new MethodTypeExceptions();
        MethodTypeExceptionsException qpException = new MethodTypeExceptionsException(
            DataServiceConstants.QUERY_PROCESSING_EXCEPTION_DESCRIPTION,
            DataServiceConstants.QUERY_PROCESSING_EXCEPTION_NAME, DataServiceConstants.QUERY_PROCESSING_EXCEPTION_QNAME);
        MethodTypeExceptionsException mqException = new MethodTypeExceptionsException(
            DataServiceConstants.MALFORMED_QUERY_EXCEPTION_DESCRIPTION,
            DataServiceConstants.MALFORMED_QUERY_EXCEPTION_NAME, DataServiceConstants.MALFORMED_QUERY_EXCEPTION_QNAME);
        queryExceptions.setException(new MethodTypeExceptionsException[]{qpException, mqException});
        queryMethod.setExceptions(queryExceptions);
        // query method is imported
        MethodTypeImportInformation importInfo = new MethodTypeImportInformation();
        importInfo.setNamespace(DataServiceConstants.DATA_SERVICE_NAMESPACE);
        importInfo.setPackageName(DataServiceConstants.DATA_SERVICE_PACKAGE);
        importInfo.setPortTypeName(DataServiceConstants.DATA_SERVICE_PORT_TYPE_NAME);
        importInfo.setWsdlFile("DataService.wsdl");
        importInfo.setInputMessage(new QName(DataServiceConstants.DATA_SERVICE_NAMESPACE, "QueryRequest"));
        importInfo.setOutputMessage(new QName(DataServiceConstants.DATA_SERVICE_NAMESPACE, "QueryResponse"));
        queryMethod.setIsImported(true);
        queryMethod.setImportInformation(importInfo);
        // query method is provided
        MethodTypeProviderInformation providerInfo = new MethodTypeProviderInformation();
        providerInfo.setProviderClass(DataServiceProviderImpl.class.getName());
        queryMethod.setProviderInformation(providerInfo);
        queryMethod.setIsProvided(true);
        return queryMethod;
    }
    
    
    public static MethodType createCql2QueryMethod() {
        MethodType queryMethod = new MethodType();
        queryMethod.setName(QueryMethodConstants.CQL2_QUERY_METHOD_NAME);
        queryMethod.setDescription(QueryMethodConstants.CQL2_QUERY_METHOD_DESCRIPTION);
        // method input parameters
        MethodTypeInputs inputs = new MethodTypeInputs();
        MethodTypeInputsInput queryInput = new MethodTypeInputsInput();
        queryInput.setName(QueryMethodConstants.CQL2_QUERY_METHOD_PARAMETER_NAME);
        queryInput.setIsArray(false);
        queryInput.setQName(CqlSchemaConstants.CQL2_QUERY_QNAME);
        queryInput.setDescription(QueryMethodConstants.CQL2_QUERY_METHOD_PARAMETER_DESCRIPTION);
        inputs.setInput(new MethodTypeInputsInput[]{queryInput});
        queryMethod.setInputs(inputs);
        // method output
        MethodTypeOutput output = new MethodTypeOutput();
        output.setIsArray(false);
        output.setQName(CqlSchemaConstants.CQL2_RESULTS_QNAME);
        output.setDescription(QueryMethodConstants.CQL2_QUERY_METHOD_OUTPUT_DESCRIPTION);
        queryMethod.setOutput(output);
        // exceptions on query method
        MethodTypeExceptions queryExceptions = new MethodTypeExceptions();
        MethodTypeExceptionsException qpException = new MethodTypeExceptionsException(
            QueryMethodConstants.QUERY_PROCESSING_EXCEPTION_DESCRIPTION,
            QueryMethodConstants.QUERY_PROCESSING_EXCEPTION_NAME, DataServiceConstants.QUERY_PROCESSING_EXCEPTION_QNAME);
        MethodTypeExceptionsException mqException = new MethodTypeExceptionsException(
            QueryMethodConstants.MALFORMED_QUERY_EXCEPTION_DESCRIPTION,
            QueryMethodConstants.MALFORMED_QUERY_EXCEPTION_NAME, DataServiceConstants.MALFORMED_QUERY_EXCEPTION_QNAME);
        queryExceptions.setException(new MethodTypeExceptionsException[]{qpException, mqException});
        queryMethod.setExceptions(queryExceptions);
        // query method is imported
        MethodTypeImportInformation importInfo = new MethodTypeImportInformation();
        importInfo.setNamespace(ServiceNamingConstants.CQL2_DATA_SERVICE_NAMESPACE);
        importInfo.setPackageName(ServiceNamingConstants.CQL2_DATA_SERVICE_PACKAGE);
        importInfo.setPortTypeName(ServiceNamingConstants.CQL2_DATA_SERVICE_PORT_TYPE_NAME);
        importInfo.setWsdlFile("Cql2DataService.wsdl");
        importInfo.setInputMessage(new QName(ServiceNamingConstants.CQL2_DATA_SERVICE_NAMESPACE, "ExecuteQueryRequest"));
        importInfo.setOutputMessage(new QName(ServiceNamingConstants.CQL2_DATA_SERVICE_NAMESPACE, "ExecuteQueryResponse"));
        queryMethod.setIsImported(true);
        queryMethod.setImportInformation(importInfo);
        // query method is provided
        MethodTypeProviderInformation providerInfo = new MethodTypeProviderInformation();
        providerInfo.setProviderClass(Cql2DataServiceProviderImpl.class.getName());
        queryMethod.setProviderInformation(providerInfo);
        queryMethod.setIsProvided(true);
        return queryMethod;
    }

    
    private boolean queryOperationCreated(ServiceInformation info) {
        ServiceType mainService = info.getServices().getService(0);
        MethodType queryMethod = CommonTools
            .getMethod(mainService.getMethods(), DataServiceConstants.QUERY_METHOD_NAME);
        if (queryMethod != null) {
            return createCql1QueryMethod().equals(queryMethod);
        }
        return false;
    }
}
