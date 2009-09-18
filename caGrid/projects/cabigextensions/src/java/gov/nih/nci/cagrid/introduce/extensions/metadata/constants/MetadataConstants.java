package gov.nih.nci.cagrid.introduce.extensions.metadata.constants;

import java.io.File;

import javax.xml.namespace.QName;


public interface MetadataConstants {

    public static final String EXTENSION_NAME = "cagrid_metadata";
    public static final String METADATA_JAR_PREFIX = "caGrid-metadata";

    // metadata schema constants
    public static final String METADATA_SCHEMA_LOCATION = "cagrid" + File.separator + "types";
    public static final String DATA_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + File.separator + "data"
        + File.separator + "data.xsd";
    public static final String COMMON_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + File.separator + "common"
        + File.separator + "common.xsd";
    public static final String SERVICE_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + File.separator + "service"
        + File.separator + "servicemodel.xsd";
    public static final String CAGRID_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + File.separator
        + "caGridMetadata.xsd";

    // service metadata QName
    public static final QName SERVICE_METADATA_QNAME = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata",
        "ServiceMetadata");

    public static final String COMMON_METADATA_NAMESPACE = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.common";

    public static final String SERVICE_METADATA_NAMESPACE = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.service";

    public static final QName SERVICE_QNAME = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.service",
        "Service");

    public static final String MMS_URL_PROPERTY = "MMS_URL";
}
