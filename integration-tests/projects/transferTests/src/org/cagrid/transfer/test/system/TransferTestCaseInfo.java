package org.cagrid.transfer.test.system;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

public class TransferTestCaseInfo extends TestCaseInfo {
    

    public String getDir() {
        return getName();
    }


    public String getName() {
        return "TransferSystemTest";
    }


    public String getNamespace() {
        return "http://org.cagrid.transfer.system.test/SystemTest";
    }


    public String getPackageDir() {
        return getPackageName().replace('.',File.separatorChar);
    }


    public String getPackageName() {
        return "org.cagrid.transfer.system.test";
    }


    public String getResourceFrameworkType() {
        return IntroduceConstants.INTRODUCE_MAIN_RESOURCE + "," + IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE + "," + IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE;
    }

    
    public String getExtensions() {
       return "caGrid_Transfer";
    }
    
   

}
