package org.cagrid.tests.data.styles.cacore42.upgrade.from14;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.tests.data.styles.cacore42.upgrade.UpgradeStory;


public class SDK42DataServiceUpgradeTest extends UpgradeStory {

    public SDK42DataServiceUpgradeTest() {
        super();
    }


    public String getDescription() {
        return "Tests upgrading a caGrid 1.4 Data Service backed by caCORE SDK 4.2 to the current version";
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
        return "DataServiceWithSdk42-1.4.zip";
    }
}
