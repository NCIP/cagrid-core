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

/**
 * BdtMethodConstants
 * Nothing from this class should be used for purposes
 * other than backwards compatibility and removing
 * the BDT operation from old services during upgrades
 * 
 * @author David
 */
public interface BdtMethodConstants {


    // bdt query method constants
    @Deprecated
    public static final String BDT_QUERY_METHOD_NAME = "bdtQuery";
    @Deprecated
    public static final String BDT_QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method which returns results handled by Bulk Data Transfer.";
    @Deprecated
    public static final String BDT_DATA_SERVICE_NAMESPACE = "http://gov.nih.nci.cagrid.data.bdt/BDTDataService";
    @Deprecated
    public static final String BDT_DATA_SERVICE_PACKAGE_NAME = ServiceNamingConstants.DATA_SERVICE_PACKAGE + ".bdt";
    @Deprecated
    public static final String BDT_HANDLER_CLIENT_CLASSNAME = "gov.nih.nci.cagrid.bdt.client.BulkDataHandlerClient";
    @Deprecated
    public static final QName BDT_HANDLER_REFERENCE_QNAME = new QName("http://cagrid.nci.nih.gov/BulkDataHandlerReference", "BulkDataHandlerReference");
    @Deprecated
    public static final QName BDT_QUERY_METHOD_INPUT_MESSAGE = new QName(BDT_DATA_SERVICE_NAMESPACE, "BdtQueryRequest");
    @Deprecated
    public static final QName BDT_QUERY_METHOD_OUTPUT_MESSAGE = new QName(BDT_DATA_SERVICE_NAMESPACE, "BdtQueryResponse");
    
}
