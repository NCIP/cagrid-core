package gov.nih.nci.cagrid.tests.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://tests.cagrid.nci.nih.gov/BasicDataService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "BasicDataServiceKey");
	public static final QName RESOURCE_PROPERY_SET = new QName(SERVICE_NS, "BasicDataServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA_MD_RP = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	public static final QName DOMAINMODEL_MD_RP = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	
}
