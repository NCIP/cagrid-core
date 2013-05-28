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
package org.cagrid.tests.data.styles.cacore42.upgrade.from15;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.tests.data.styles.cacore42.upgrade.UpgradeStory;


public class SDK42DataServiceUpgradeTest extends UpgradeStory {

    public SDK42DataServiceUpgradeTest() {
        super();
    }


    public String getDescription() {
        return "Tests upgrading a caGrid 1.5 Data Service backed by caCORE SDK 4.2 to the current version";
    }


    public DataTestCaseInfo getTestCaseInfo() {
        DataTestCaseInfo info = new DataTestCaseInfo() {
            public String getPackageName() {
                return "org.cagrid.test.data.with.sdk42";
            }
            
            
            public String getNamespace() {
                return "http://sdk42.with.data.test.cagrid.org/DataServiceWithSdk42";
            }
            
            
            public String getName() {
                return "DataServiceWithSdk42";
            }
            
            
            protected String getServiceDirName() {
                return "DataServiceWithSdk42";
            }
        };
        return info;
    }


    @Override
    public String getServiceZipName() {
        return "DataServiceWithSdk42-1.5.zip";
    }
}
