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
package org.cagrid.introduce.test.system.tci;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;


public class SecurityTestCaseInfo extends TestCaseInfo {

    @Override
    public String getDir() {
        return getName();
    }


    @Override
    public String getName() {
        return "SecurityTests";
    }


    @Override
    public String getNamespace() {
        return "http://tests.security.introduce.cagrid.org/" + getName();
    }


    @Override
    public String getPackageDir() {
        return getPackageName().replace('.',File.separatorChar);
    }


    @Override
    public String getPackageName() {
        return "org.cagrid.introduce.security.tests";
    }


    @Override
    public String getResourceFrameworkType() {
        return IntroduceConstants.INTRODUCE_MAIN_RESOURCE + "," + IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE + "," + IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE;
    }

}
