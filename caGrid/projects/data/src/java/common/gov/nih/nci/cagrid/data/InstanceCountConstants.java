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

public interface InstanceCountConstants {

    // constants related to the instance count updater
    public static final String COUNT_UPDATE_FREQUENCY = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + "_countUpdateFrequency";
    public static final String COUNT_UPDATE_FREQUENCY_DEFAULT = "600"; // 600 seconds = 10 minutes
    public static final String COUNT_UPDATE_FREQUENCY_DESCRIPTION = "The number of seconds between updates to the instance count metadata";
}
