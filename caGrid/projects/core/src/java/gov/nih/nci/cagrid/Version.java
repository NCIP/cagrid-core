package gov.nih.nci.cagrid;

public class Version {

    /**
     * Gets the version of caGrid
     * for caGrid 1.2.3.4, this returns 1
     * 
     * @return
     */
    public static final int getStreamVersion() {
        return 1;
    }
    
    
    /**
     * Gets the major release version number of caGrid
     * for caGrid 1.2.3.4, this returns 2
     * 
     * @return
     */
    public static final int getMajorReleaseVersion() {
        return 4;
    }
    
    
    /**
     * Gets the minor release version number of caGrid
     * for caGrid 1.2.3.4, this returns 3
     * 
     * @return
     */
    public static final int getMinorReleaseVersion() {
        return 1;
    }
    
    
    /**
     * Gets the point release version number of caGrid
     * for caGrid 1.2.3.4, this returns 4
     * 
     * @return
     */
    public static final int getPointReleaseVersion() {
        return 0;
    }
    
    
    public static int compareTo(String versionString) {
        return getVersionString().compareTo(versionString);
    }
    
    
    public static String getVersionString() {
        return "" + getStreamVersion() + "." + getMajorReleaseVersion() + "." 
            + getMinorReleaseVersion() + "." + getPointReleaseVersion();
    }
}
