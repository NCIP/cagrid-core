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
package gov.nih.nci.cagrid.data.upgrades;

/**
 * UpgraderConstants
 * Constants for the data service upgraders.  Provides a single point for
 * setting things like the current version, jar names, etc.
 * 
 * @author ervin
 */
public interface UpgraderConstants {

    /** Current data services version */
    public static final String DATA_CURRENT_VERSION = "1.6";
    
    /** Current WS-Enumeration support version */
    public static final String ENUMERATION_CURRENT_VERSION = "1.6";
}
