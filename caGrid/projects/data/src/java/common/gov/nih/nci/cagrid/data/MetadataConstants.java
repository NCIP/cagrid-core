/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.data;

import javax.xml.namespace.QName;

public interface MetadataConstants {

    // metadata schema constants
    public static final String METADATA_SCHEMA_LOCATION = "xsd/cagrid/types";
    public static final String DATA_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + "/data/data.xsd";
    public static final String COMMON_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + "/common/common.xsd";
    public static final String CAGRID_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + "/caGridMetadata.xsd";
    public static final String DATA_METADATA_URI = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice";
    
    
    // DomainModel QName
    public static final QName DOMAIN_MODEL_QNAME = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
    
    // service metadata QName
    public static final QName SERVICE_METADATA_QNAME = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
    
    // query language support
    public static final String QUERY_LANGUAGE_SUPPORT_XSD = "QueryLanguageSupportMetadata.xsd";
    public static final QName QUERY_LANGUAGE_SUPPORT_QNAME = new QName("http://org.cagrid.dataservice.cql/QueryLanguageSupport", "QueryLanguageSupport");
    public static final String QUERY_LANGUAGE_SUPPORT_RESOURCE_SETTER_METHOD_NAME = "setQueryLanguageSupport";
    public static final String QUERY_LANGUAGE_SUPPORT_PACKAGE = "org.cagrid.dataservice.cql.support";
    public static final String QUERY_LANGUAGE_SUPPORT_DESCRIPTION = "Describes the level of support for CQL versions";
    
    // data instance metadata
    public static final String DATA_INSTANCE_XSD = "DataInstanceCounts.xsd";
    public static final QName DATA_INSTANCE_QNAME = new QName("gme://org.cagrid.dataservice.metadata/InstanceCount", "DataServiceInstanceCounts");
    // TODO: verify this is right
    public static final String DATA_INSTANCE_RESOURCE_SETTER_METHOD_NAME = "setDataServiceInstanceCounts";
    public static final String DATA_INSTANCE_PACKAGE = "org.cagrid.dataservice.metadata.instancecount";
    public static final String DATA_INSTANCE_DESCRIPTION = "Lists the data types and the number of instances of each";
}
