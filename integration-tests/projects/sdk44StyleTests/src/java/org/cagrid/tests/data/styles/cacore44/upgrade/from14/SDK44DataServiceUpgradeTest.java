package org.cagrid.tests.data.styles.cacore44.upgrade.from14;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.tests.data.styles.cacore44.upgrade.UpgradeStory;


public class SDK44DataServiceUpgradeTest extends UpgradeStory {

    public SDK44DataServiceUpgradeTest() {
        super();
    }


    public String getDescription() {
        return "Tests upgrading a caGrid 1.4 Data Service backed by caCORE SDK 4.4 to the current version";
    }


    public DataTestCaseInfo getTestCaseInfo() {
        DataTestCaseInfo info = new DataTestCaseInfo() {
            public String getPackageName() {
                return "org.cagrid.test.data.with.sdk44";
            }
            
            
            public String getNamespace() {
                return "http://sdk44.with.data.test.cagrid.org/DataServiceWithSdk44";
            }
            
            
            public String getName() {
                return "DataServiceWithSdk44";
            }
            
            
            protected String getServiceDirName() {
                return "DataServiceWithSdk44";
            }
        };
        return info;
    }


    @Override
    public String getServiceZipName() {
        return "DataServiceWithSdk44-1.4.zip";
    }
}
