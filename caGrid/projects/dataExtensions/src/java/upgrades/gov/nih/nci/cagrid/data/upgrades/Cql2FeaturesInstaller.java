package gov.nih.nci.cagrid.data.upgrades;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;
import gov.nih.nci.cagrid.data.MetadataConstants;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.creation.CQL2NamespaceUtil;
import gov.nih.nci.cagrid.data.creation.DataServiceQueryOperationProviderCreator;
import gov.nih.nci.cagrid.data.creation.WsEnumerationFeatureCreator;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgradeStatus;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.jdom.Element;

/**
 * Cql2FeaturesInstaller
 * Installs CQL 2 features into 1.2 and 1.3 data services on upgrade
 * 
 * @author David
 */
public class Cql2FeaturesInstaller {

    private ServiceInformation serviceInfo = null;
    private ExtensionType extensionType = null;
    private ExtensionUpgradeStatus status = null;
    
    public Cql2FeaturesInstaller(ServiceInformation serviceInfo, 
        ExtensionType extensionType, ExtensionUpgradeStatus status) {
        this.serviceInfo = serviceInfo;
        this.extensionType = extensionType;
        this.status = status;
    }
    
    
    public void installCql2Features() throws UpgradeException {
        addCql2Wsdls();
        addCql2Query();
        if (serviceUsingEnumeration()) {
            addCql2EnumerationQuery();
        }
        addCql2QueryProcessorServiceProperty();
        // no transfer was available before 1.4, so no installTransfer...
        // let the developer know about CQL 2
        status.addIssue("caGrid 1.4 data services add support for CQL 2." +
        		"  As such, a CQL 2 query processor must be provided for full " +
        		"compatibility with the data service specification.",
        		"A guide for upgrading to CQL 2 is provided online here: " +
        		"https://cagrid.org/display/dataservices/Upgrade+to+CQL+2");
    }
    

    private File getDataSchemaDir() {
        return new File(ExtensionsLoader.getInstance().getExtensionsDir(),
            "data" + File.separator + "schema" + File.separator + "Data");
    }
    
    
    private File getServiceSchemaDir() {
        String baseServiceName = serviceInfo.getServices().getService(0).getName();
        return new File(serviceInfo.getBaseDirectory(), "schema" + File.separator + baseServiceName);
    }
    
    
    private boolean serviceUsingEnumeration() throws UpgradeException {
        MessageElement[] anys = extensionType.getExtensionData().get_any();
        MessageElement rawDataElement = null;
        for (int i = 0; (anys != null) && (i < anys.length); i++) {
            if (anys[i].getQName().equals(Data.getTypeDesc().getXmlType())) {
                rawDataElement = anys[i];
                break;
            }
        }
        if (rawDataElement == null) {
            throw new UpgradeException("No extension data was found for the data service extension");
        }
        Element extElement = AxisJdomUtils.fromMessageElement(rawDataElement);
        Element serviceFeaturesElement = extElement.getChild("ServiceFeatures", extElement.getNamespace());
        String useEnumValue = serviceFeaturesElement.getAttributeValue("useWsEnumeration");
        return Boolean.valueOf(useEnumValue).booleanValue();
    }
    
    
    private void addCql2QueryProcessorServiceProperty() {
        ServiceDescription desc = this.serviceInfo.getServiceDescriptor();
        // does the cql2 query processor class property exist?
        if (!CommonTools.servicePropertyExists(desc,
            QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY)) {
            // set the service property to be empty
            CommonTools.setServiceProperty(desc, 
                QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY,
                "", false);
            status.addDescriptionLine("Added service property " 
                + QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY);
        }
    }
    
    
    private void addCql2Query() throws UpgradeException {
        CQL2NamespaceUtil nsUtil = new CQL2NamespaceUtil(serviceInfo, status);
        try {
            nsUtil.addCql2QuerySchema();
        } catch (Exception ex) {
            throw new UpgradeException("Error adding CQL 2 query namespace: " + ex.getMessage(), ex);
        }
        
        String schemaDirName = getServiceSchemaDir().getAbsolutePath();
        File schemaDirFile = getServiceSchemaDir();
        // copy schemas into service first, then run add namespace operations
        List<File> neededSchemas = new ArrayList<File>();
        Collections.addAll(neededSchemas,
            getDataSchemaDir().listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    String name = pathname.getName();
                    return name.startsWith("Cql2") && name.endsWith(".wsdl");
                }
            }));
        Collections.addAll(neededSchemas,
            getDataSchemaDir().listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    String name = pathname.getName();
                    return name.startsWith("Cql2") && name.endsWith("Types.xsd");
                }
            }));
        try {
            for (File schema : neededSchemas) {
                File schemaOut = new File(getServiceSchemaDir(), schema.getName());
                Utils.copyFile(schema, schemaOut);
                status.addDescriptionLine("Added CQL 2 support schema: " + schema.getName());
            }
        } catch (IOException ex) {
            throw new UpgradeException("Error copying new schemas or wsdl: " + ex.getMessage(), ex);
        }
        try {
            // Query language support metadata namespace
            NamespaceType supportNamespace = CommonTools.createNamespaceType(schemaDirName + File.separator
                + MetadataConstants.QUERY_LANGUAGE_SUPPORT_XSD, schemaDirFile);
            supportNamespace.setPackageName(MetadataConstants.QUERY_LANGUAGE_SUPPORT_PACKAGE);
            supportNamespace.setGenerateStubs(Boolean.FALSE);
            CommonTools.addNamespace(serviceInfo.getServiceDescriptor(), supportNamespace);
        } catch (Exception ex) {
            throw new UpgradeException("Error adding CQL 2 schemas " + ex.getMessage(), ex);
        }
        // add the CQL 2 execute query method
        MethodType cql2Method = DataServiceQueryOperationProviderCreator.createCql2QueryMethod();
        CommonTools.addMethod(
            serviceInfo.getServices().getService(0), cql2Method);
        status.addDescriptionLine("Added base CQL 2 query operation " + cql2Method.getName());
    }
    
    
    private void addCql2EnumerationQuery() {
        MethodType cql2EnumerationMethod = WsEnumerationFeatureCreator.getCql2EnumerationQueryMethod();
        CommonTools.addMethod(serviceInfo.getServices().getService(0), cql2EnumerationMethod);
        status.addDescriptionLine("Added CQL 2 enumeration query operation " + cql2EnumerationMethod.getName());
    }
    
    
    private void addCql2Wsdls() throws UpgradeException {
        // add Cql2*.wsdl
        File[] cql2Wsdls = getDataSchemaDir().listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.startsWith("Cql2") && name.endsWith(".wsdl");
            }
        });
        for (File wsdl : cql2Wsdls) {
            try {
                File wsdlOut = new File(getServiceSchemaDir(), wsdl.getName());
                Utils.copyFile(wsdl, wsdlOut);
                status.addDescriptionLine("Added wsdl " + wsdl.getName());
            } catch (IOException ex) {
                status.addDescriptionLine("Error copying wsdl: " + ex.getMessage());
                throw new UpgradeException("Error copying wsdl: " + ex.getMessage(), ex);
            }
        }
        // the types schemas that go with the WSDLs
        File[] typesSchemas = getDataSchemaDir().listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.startsWith("Cql2") && name.endsWith("Types.xsd");
            }
        });
        for (File schema : typesSchemas) {
            try {
                File schemaOut = new File(getServiceSchemaDir(), schema.getName());
                Utils.copyFile(schema, schemaOut);
                status.addDescriptionLine("Added schema " + schema.getName());
            } catch (IOException ex) {
                status.addDescriptionLine("Error copying schema: " + ex.getMessage());
                throw new UpgradeException("Error copying schema:" + ex.getMessage(), ex);
            }
        }
    }
}
