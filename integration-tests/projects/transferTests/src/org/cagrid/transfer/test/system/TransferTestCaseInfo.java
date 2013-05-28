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
