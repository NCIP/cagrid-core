package gov.nih.nci.cagrid.metadata;

import javax.xml.namespace.QName;


public class MetadataConstants {

	// namespaces
	public static final String CAGRID_MD_NAMESPACE = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata";
	public static final String CAGRID_COMMON_MD_NAMESPACE = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.common";
	public static final String CAGRID_SERVICE_MD_NAMESPACE = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.service";
	public static final String CAGRID_DATA_MD_NAMESPACE = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice";
	public static final String CADSR_NAMESPACE = "gme://caCORE.caBIG/3.0/gov.nih.nci.cadsr.domain";
	public static final String CADSR_UML_NAMESPACE = "gme://caCORE.caBIG/3.0/gov.nih.nci.cadsr.umlproject.domain";
	public static final String AGGREGATOR_NAMESPACE = "http://mds.globus.org/aggregator/types";

	// exposed resource properties
	public static final String CAGRID_MD_NAME = "ServiceMetadata";
	public static final QName CAGRID_MD_QNAME = new QName(CAGRID_MD_NAMESPACE, CAGRID_MD_NAME);
	public static final String CAGRID_DATA_MD_NAME = "DomainModel";
	public static final QName CAGRID_DATA_MD_QNAME = new QName(CAGRID_DATA_MD_NAMESPACE, CAGRID_DATA_MD_NAME);

}
