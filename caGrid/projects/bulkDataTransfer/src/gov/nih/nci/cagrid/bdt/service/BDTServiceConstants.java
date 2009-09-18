package gov.nih.nci.cagrid.bdt.service;

import javax.xml.namespace.QName;


public interface BDTServiceConstants {
    public static final String BDT_SERVICE_NAME = "BulkDataHandler";
    public static final String BDT_SERVICE_PACKAGE = "gov.nih.nci.cagrid.bdt";
    public static final String BDT_SERVICE_NAMESPACE = "http://cagrid.nci.nih.gov/BulkDataHandler";
	public static final QName METADATA_QNAME = new QName("http://cagrid.nci.nih.gov/1/BulkDataTransferMetadata", "BulkDataTransferMetadata");
	public static final String METADATA_SCHEMA = "BulkDataTransferServiceMetadata.xsd";
	public static final String BDT_REF_SCHEMA = "BulkDataHandlerReference.xsd";
	public static final String ENUMERATION_SCHEMA = "enumeration.xsd";
	public static final String ADDRESSING_SCHEMA = "addressing.xsd";
	public static final String TRANSFER_SCHEMA = "transfer.xsd";
    public static final String ENUMERATION_RESPONSE_CONTAINER_SCHEMA = "EnumerationResponseContainer.xsd";
}