package org.cagrid.data.style.test.cacore32;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.cagrid.data.test.creation.DataTestCaseInfo;

public class CopySdkLibrariesStep extends Step {
    
    private DataTestCaseInfo testInfo = null;
    private File sdkPackageDir = null;
    
    public CopySdkLibrariesStep(DataTestCaseInfo testInfo, File sdkPackageDir) {
        this.testInfo = testInfo;
        this.sdkPackageDir = sdkPackageDir;
    }
    

    public void runStep() throws Throwable {
        File serviceLibDir = new File(testInfo.getDir(), "lib");
        File clientLibDir = new File(sdkPackageDir, "sdk3.2.1" + File.separator + "client" + File.separator + "lib");
        File[] clientJars = clientLibDir.listFiles(new FileFilters.JarFileFilter());
                
        // list jars in the globus lib dir and avoid copying anything
        // from the SDK which overlaps that in globus
        File globusLibDir = new File(CommonTools.getGlobusLocation(), "lib");
        File[] globusLibs = globusLibDir.listFiles(new FileFilters.JarFileFilter());
        
        Set<String> globusLibNames = new HashSet<String>();
        for (File lib : globusLibs) {
            globusLibNames.add(lib.getName());
        }

        // copy libs that don't conflict with globus libs
        for (File lib : clientJars) {
            if (!globusLibNames.contains(lib.getName())) {
                File libOut = new File(serviceLibDir, lib.getName());
                Utils.copyFile(lib, libOut);
            }
        }
    }
}
