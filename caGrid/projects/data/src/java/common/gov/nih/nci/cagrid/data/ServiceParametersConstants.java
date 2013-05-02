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

public interface ServiceParametersConstants {

    // data service specific service parameters
    public static final String DATA_SERVICE_PARAMS_PREFIX = "dataService";
    public static final String CLASS_MAPPINGS_FILENAME = DATA_SERVICE_PARAMS_PREFIX + "_classMappingsFilename";
    public static final String CLASS_TO_QNAME_XML = "classToQname.xml";

    // the server-config.wsdd location
    public static final String SERVER_CONFIG_LOCATION = "serverConfigLocation";
}
