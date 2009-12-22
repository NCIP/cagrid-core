package gov.nih.nci.cagrid.data;

public interface ServiceNamingConstants {
    
    // data service naming constants
    public static final String DATA_SERVICE_PACKAGE = "gov.nih.nci.cagrid.data";
    public static final String DATA_SERVICE_SERVICE_NAME = "DataService";
    public static final String DATA_SERVICE_NAMESPACE = "http://" + DATA_SERVICE_PACKAGE + "/" + DATA_SERVICE_SERVICE_NAME;
    public static final String DATA_SERVICE_PORT_TYPE_NAME = DATA_SERVICE_SERVICE_NAME + "PortType";
}
