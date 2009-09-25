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
    public static final String DATA_CURRENT_VERSION = "1.4";
    
    /** Current WS-Enumeration support version */
    public static final String ENUMERATION_CURRENT_VERSION = "1.4";
    
    /** Current SDK 3.1 / 3.2 support version */
    public static final String SDK_3_CURRENT_VERSION = "1.4";
}
