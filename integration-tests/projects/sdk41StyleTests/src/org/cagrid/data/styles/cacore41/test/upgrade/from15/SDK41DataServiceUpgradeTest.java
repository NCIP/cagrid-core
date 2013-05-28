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
package org.cagrid.data.styles.cacore41.test.upgrade.from15;

import org.cagrid.data.styles.cacore41.test.upgrade.UpgradeStory;
import org.cagrid.data.test.creation.DataTestCaseInfo;


public class SDK41DataServiceUpgradeTest extends UpgradeStory {

    public SDK41DataServiceUpgradeTest() {
        super();
    }


    public String getDescription() {
        return "Tests upgrading a caGrid 1.5 Data Service backed by caCORE SDK 4.1 to the current version";
    }


    public DataTestCaseInfo getTestCaseInfo() {
        DataTestCaseInfo info = new DataTestCaseInfo() {
            public String getPackageName() {
                return "org.cagrid.test.data.with.sdk41";
            }
            
            
            public String getNamespace() {
                return "http://sdk41.with.data.test.cagrid.org/DataServiceWithSdk41";
            }
            
            
            public String getName() {
                return "DataServiceWithSdk41";
            }
            
            
            protected String getServiceDirName() {
                return "DataServiceWithSdk41";
            }
        };
        return info;
    }


    @Override
    public String getServiceZipName() {
        return "DataServiceWithSdk41-1.5.zip";
    }
}
