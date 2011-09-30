package org.cagrid.data.styles.cacore41.test.upgrade.from14;

import org.cagrid.data.styles.cacore41.test.upgrade.UpgradeStory;
import org.cagrid.data.test.creation.DataTestCaseInfo;


public class SDK41DataServiceUpgradeTest extends UpgradeStory {

    public SDK41DataServiceUpgradeTest() {
        super();
    }


    public String getDescription() {
        return "Tests upgrading a caGrid 1.4 Data Service backed by caCORE SDK 4.1 to the current version";
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
        return "DataServiceWithSdk41-1.4.zip";
    }
}
