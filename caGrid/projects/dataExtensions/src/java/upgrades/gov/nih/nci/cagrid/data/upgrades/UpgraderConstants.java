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
    public static final String DATA_CURRENT_VERSION = "1.5";
    
    /** Current WS-Enumeration support version */
    public static final String ENUMERATION_CURRENT_VERSION = "1.5";
}
