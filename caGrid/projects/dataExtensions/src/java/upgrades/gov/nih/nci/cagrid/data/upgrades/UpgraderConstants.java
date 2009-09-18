package gov.nih.nci.cagrid.data.upgrades;

/**
 * UpgraderConstants
 * Constants for the data service upgraders.  Provides a single point for
 * setting things like the current version, jar names, etc.
 * 
 * FIXME: change this to 1.3 with the release!!!
 * 
 * @author ervin
 */
public interface UpgraderConstants {

    /** Current caGrid version */
    public static final String CAGRID_CURRENT_VERSION = "1.3-dev";
    
    /** Current data services version */
    public static final String DATA_CURRENT_VERSION = "1.3-dev";
    
    /** Current WS-Enumeration support version */
    public static final String ENUMERATION_CURRENT_VERSION = "1.3-dev";
    
    /** Current SDK 3.1 / 3.2 support version */
    public static final String SDK_3_CURRENT_VERSION = "1.3-dev";
}
