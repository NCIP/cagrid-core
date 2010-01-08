package gov.nih.nci.cagrid.data;

import javax.xml.namespace.QName;

public interface TransferMethodConstants {

    // CQL 1 transfer query method constants
    public static final String TRANSFER_QUERY_METHOD_NAME = "transferQuery";
    public static final String TRANSFER_QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method which returns results via the caGrid Transfer framework.";
    public static final String TRANSFER_QUERY_METHOD_OUTPUT_DESCRIPTION = "The transfer context reference containing the EPR of the transfer resource";
    public static final String TRANSFER_DATA_SERVICE_NAMESPACE = "http://gov.nih.nci.cagrid.data.transfer/TransferDataService";
    public static final String TRANSFER_DATA_SERVICE_PACKAGE_NAME = ServiceNamingConstants.DATA_SERVICE_PACKAGE + ".transfer";
    public static final String TRANSFER_CONTEXT_CLIENT = "org.cagrid.transfer.context.client.TransferServiceContextClient";
    public static final QName TRANSFER_CONTEXT_REFERENCE_QNAME = new QName("http://transfer.cagrid.org/TransferService/Context/types", "TransferServiceContextReference");
    public static final QName TRANSFER_QUERY_METHOD_INPUT_MESSAGE = new QName(TRANSFER_DATA_SERVICE_NAMESPACE, "TransferQueryRequest");
    public static final QName TRANSFER_QUERY_METHOD_OUTPUT_MESSAGE = new QName(TRANSFER_DATA_SERVICE_NAMESPACE, "TransferQueryResponse");

    // CQL 2 transfer query method constants
    public static final String CQL2_TRANSFER_QUERY_METHOD_NAME = "executeTransferQuery";
    public static final String CQL2_TRANSFER_QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method which takes CQL 2 and returns results via the caGrid Transfer framework.";
    public static final String CQL2_TRANSFER_DATA_SERVICE_NAMESPACE = "http://org.cagrid.dataservice.transfer/Cql2TransferDataService";
    public static final String CQL2_TRANSFER_DATA_SERVICE_PACKAGE_NAME = ServiceNamingConstants.CQL2_DATA_SERVICE_PACKAGE + ".transfer";
    public static final String CQL2_TRANSFER_DATA_SERVICE_PORT_TYPE = "Cql2TransferDataServicePortType";
    public static final QName CQL2_TRANSFER_QUERY_METHOD_INPUT_MESSAGE = new QName(CQL2_TRANSFER_DATA_SERVICE_NAMESPACE, "ExecuteTransferQueryRequest");
    public static final QName CQL2_TRANSFER_QUERY_METHOD_OUTPUT_MESSAGE = new QName(CQL2_TRANSFER_DATA_SERVICE_NAMESPACE, "ExecuteTransferQueryResponse");
}
