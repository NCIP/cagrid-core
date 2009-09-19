package gov.nih.nci.cagrid.sdkinstall;

/** 
 *  SdkVersion
 *  Describes a version of the caCORE sdk which can be installed
 * 
 * @author David Ervin
 * 
 * @created Jun 13, 2007 10:46:26 AM
 * @version $Id: SdkVersion.java,v 1.1 2007-06-13 16:36:39 dervin Exp $ 
 */
public enum SdkVersion {
    VERSION_3_2_1;
    
    public String getVersion() {
        switch (this) {
            case VERSION_3_2_1:
                return "3.2.1";
        }
        throw new AssertionError("Unknown SdkVersion: " + this);
    }
    
    
    public String getZipFileName() {
        switch (this) {
            case VERSION_3_2_1:
                return "caCORE_SDK_321.zip";
        }
        throw new AssertionError("Unknown SdkVersion: " + this);
    }
}
