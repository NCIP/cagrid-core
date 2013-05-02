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

public interface AuditorConstants {

    // auditor related constants
    public static final String DATA_SERVICE_AUDITING_NAMESPACE = "http://gov.nih.nci.cagrid.data/Auditing";
    public static final String DATA_SERVICE_AUDITORS_NAME = "DataServiceAuditors";
    public static final QName DATA_SERVICE_AUDITORS_QNAME = new QName(DATA_SERVICE_AUDITING_NAMESPACE, DATA_SERVICE_AUDITORS_NAME);
    public static final String DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + "_auditorsConfigFile";
    public static final String DATA_SERVICE_AUDITORS_CONFIG_FILE_NAME = "dataServiceAuditorsConfiguration.xml";
    
}
