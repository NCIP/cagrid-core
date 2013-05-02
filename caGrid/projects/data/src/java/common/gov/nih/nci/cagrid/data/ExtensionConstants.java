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

public interface ExtensionConstants {

    // service URL parameter constants
    public static final String CADSR_SERVICE_URL = "CADSR_DATA_URL";
    public static final String GME_SERVICE_URL = "GME_URL";
    public static final String MMS_URL = "MMS_URL";
    
    // service style constants
    public static final String SERVICE_STYLES_DIR_NAME = "styles";
    public static final String SERVICE_STYLE_FILE_NAME = "style.xml";
    
    // class to qname mapping QName
    public static final QName MAPPING_QNAME = new QName("http://gov.nih.nci.cagrid.data", "ClassMappings");
}
