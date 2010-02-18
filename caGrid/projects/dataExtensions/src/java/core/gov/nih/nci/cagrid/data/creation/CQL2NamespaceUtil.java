package gov.nih.nci.cagrid.data.creation;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgradeStatus;

import java.io.File;
import java.io.FileFilter;

import org.cagrid.cql.utilities.CQLConstants;
import org.cagrid.cql.utilities.encoding.Cql2DeserializerFactory;
import org.cagrid.cql.utilities.encoding.Cql2SerializerFactory;

public class CQL2NamespaceUtil {
    
    private ServiceInformation info = null;
    private ExtensionUpgradeStatus status = null;
    
    public CQL2NamespaceUtil(ServiceInformation info) {
        this(info, null);
    }
    

    public CQL2NamespaceUtil(ServiceInformation info, ExtensionUpgradeStatus upgradeStatus) {
        this.info = info;
        this.status = upgradeStatus;
    }
    
    
    protected void logMessage(String message) {
        if (status != null) {
            status.addDescriptionLine(message);
        }
    }
    
    
    public void addCql2QuerySchema() throws Exception {
        // copy the CQL 2 schemas in to the service's schema dir
        File serviceSchemaDir = getServiceSchemaDir();
        File[] cql2Schemas = getDataSchemaDir().listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.endsWith(".xsd") && (
                    name.startsWith("CQL") || 
                    name.startsWith("Predicates") ||
                    name.startsWith("AssociationPopulationSpec") ||
                    name.startsWith("Aggregations") ||
                    name.startsWith("QueryLanguageSupport"));
            }
        });
        for (File schema : cql2Schemas) {
            File out = new File(serviceSchemaDir, schema.getName());
            Utils.copyFile(schema, out);
            logMessage("Added CQL 2 support schema: " + schema.getName());
        }
        // add the namespace type to the service
        CommonTools.addNamespace(info.getServiceDescriptor(),
            CommonTools.createNamespaceType(serviceSchemaDir.getAbsolutePath()
                + File.separator + CqlSchemaConstants.CQL2_SCHEMA_FILENAME, 
                serviceSchemaDir));
        // fix the serialization for the CQL 2 query namespace
        NamespaceType cql2Namespace = CommonTools.getNamespaceType(
            info.getServiceDescriptor().getNamespaces(), CQLConstants.CQL2_NAMESPACE_URI);
        cql2Namespace.setGenerateStubs(Boolean.FALSE);
        cql2Namespace.setPackageName("org.cagrid.cql2");
        for (SchemaElementType elem : cql2Namespace.getSchemaElement()) {
            elem.setClassName(elem.getType());
            elem.setSerializer(Cql2SerializerFactory.class.getName());
            elem.setDeserializer(Cql2DeserializerFactory.class.getName());
            logMessage("Configured custom serialization for CQL 2 type " + elem.getType());
        }
    }
    
    
    private File getDataSchemaDir() {
        return new File(ExtensionsLoader.getInstance().getExtensionsDir(), 
            "data" + File.separator + "schema" + File.separator + "Data");
    }
    
    
    private File getServiceSchemaDir() {
        String baseServiceName = info.getServices().getService(0).getName();
        return new File(info.getBaseDirectory(), "schema" + File.separator + baseServiceName);
    }
}
