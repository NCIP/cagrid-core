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

public interface ServiceNamingConstants {
    
    // data service naming constants
    public static final String DATA_SERVICE_PACKAGE = "gov.nih.nci.cagrid.data";
    public static final String DATA_SERVICE_SERVICE_NAME = "DataService";
    public static final String DATA_SERVICE_NAMESPACE = "http://" + DATA_SERVICE_PACKAGE + "/" + DATA_SERVICE_SERVICE_NAME;
    public static final String DATA_SERVICE_PORT_TYPE_NAME = DATA_SERVICE_SERVICE_NAME + "PortType";
    
    // CQL 2 data service naming constants
    public static final String CQL2_DATA_SERVICE_PACKAGE = "org.cagrid.dataservice";
    public static final String CQL2_DATA_SERVICE_SERVICE_NAME = "DataService";
    public static final String CQL2_DATA_SERVICE_NAMESPACE = "http://" + CQL2_DATA_SERVICE_PACKAGE + "/" + CQL2_DATA_SERVICE_SERVICE_NAME;
    public static final String CQL2_DATA_SERVICE_PORT_TYPE_NAME = "Cql2DataServicePortType";
}
