package org.cagrid.data.styles.cacore4.test.upgrade.from15;

import org.cagrid.data.styles.cacore4.test.upgrade.UpgradeStory;
import org.cagrid.data.test.creation.DataTestCaseInfo;


public class SDK4DataServiceUpgradeTest extends UpgradeStory {

    public SDK4DataServiceUpgradeTest() {
        super();
    }


    public String getDescription() {
        return "Tests upgrading a caGrid 1.5 Data Service backed by caCORE SDK 4.0 to the current version";
    }


    public DataTestCaseInfo getTestCaseInfo() {
        DataTestCaseInfo info = new DataTestCaseInfo() {
            public String getPackageName() {
                return "org.cagrid.test.data.with.sdk40";
            }
            
            
            public String getNamespace() {
                return "http://sdk40.with.data.test.cagrid.org/DataServiceWithSdk40";
            }
            
            
            public String getName() {
                return "DataServiceWithSdk40";
            }
            
            
            protected String getServiceDirName() {
                return "DataServiceWithSdk40";
            }
        };
        return info;
    }


    @Override
    public String getServiceZipName() {
        return "DataServiceWithSdk40-1.5.zip";
    }
}
