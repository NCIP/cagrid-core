package org.cagrid.data.test.creation;

import java.io.File;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

/** 
 *  DataTestCaseInfo
 *  Test case info for use in data service testing
 * 
 * @author David Ervin
 * 
 * @created Jun 12, 2007 11:46:16 AM
 * @version $Id: DataTestCaseInfo.java,v 1.2 2008-05-21 19:51:14 dervin Exp $ 
 */
public abstract class DataTestCaseInfo extends TestCaseInfo {
    
    public static final String TEST_SERVICE_BASE_DIR_PROPERTY = "temp.test.service.dir";
    
    
    public static String getTempDir() {
        String testServiceBase = System.getProperty(TEST_SERVICE_BASE_DIR_PROPERTY);
        if (testServiceBase == null) {
            testServiceBase = System.getProperty("java.io.tmpdir");
        }
        return new File(testServiceBase).getAbsolutePath();
    }
    

    public String getPackageDir() {
        return getPackageName().replace('.',File.separatorChar);
    }
    

    public String getResourceFrameworkType() {
        return IntroduceConstants.INTRODUCE_MAIN_RESOURCE + "," + IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE + "," + IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE;
    }
    
    
    /**
     * If creating an enumeration or BDT service, override this appropriatly
     * @return
     *      The extensions to use
     */
    public String getExtensions() {
        return "data";
    }
    
    
    public final String getDir() {
        File dir = new File(getTempDir(), getServiceDirName());
        return dir.getAbsolutePath();
    }
    
    
    /**
     * Subclasses / implementations need to override this to return the name of 
     * the test service directory.  The test service directory will land in the 
     * directory specified by TEST_SERVICE_BASE_DIR_PROPERTY, or the 
     * system-specific temp directory.
     * @return
     *      The service's directory name
     */
    protected abstract String getServiceDirName();
}
