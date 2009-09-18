package gov.nih.nci.cagrid.sdkquery4.style.wizard;

/** 
 *  SchemaResolutionStatus
 *  Enumerates possible states for package to schema resolution
 * 
 * @author David Ervin
 * 
 * @created Jan 23, 2008 11:02:33 AM
 * @version $Id: SchemaResolutionStatus.java,v 1.2 2009-01-07 04:45:45 oster Exp $ 
 */
public enum SchemaResolutionStatus {

    SCHEMA_FOUND, MAPPING_ERROR, 
    GME_NAMESPACE_NOT_FOUND, NEVER_TRIED;
    
    public String toString() {
        String value = null;
        switch (this) {
            case SCHEMA_FOUND:
                value = "Found";
                break;
            case MAPPING_ERROR:
                value = "Mapping Error";
                break;
            case GME_NAMESPACE_NOT_FOUND:
                value = "No Namespace";
                break;
            case NEVER_TRIED:
                value = "Unknown";
                break;
            default:
                throw new IllegalArgumentException("Invalid value for " + SchemaResolutionStatus.class.getName());
        }
        return value;
    }
}
