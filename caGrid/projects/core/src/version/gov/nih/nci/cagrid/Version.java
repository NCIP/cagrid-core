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
        return 6;
    }
    
    
    /**
     * Gets the minor release version number of caGrid
     * for caGrid 1.2.3.4, this returns 3
     * 
     * @return
     */
    public static final int getMinorReleaseVersion() {
        return 0;
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
    
    
    public static void main(String[] args) {
        System.out.println("The caGrid Version is " + getVersionString());
    }
}
