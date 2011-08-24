package org.cagrid.iso21090.tests.upgrade.from13;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.iso21090.tests.upgrade.UpgradeStory;


public class SDK43DataServiceUpgradeTest extends UpgradeStory {

    public SDK43DataServiceUpgradeTest() {
        super();
    }


    public String getDescription() {
        return "Tests upgrading a caGrid 1.3 Data Service backed by caCORE SDK 4.3 to the current version";
    }


    public DataTestCaseInfo getTestCaseInfo() {
        DataTestCaseInfo info = new DataTestCaseInfo() {
            public String getPackageName() {
                return "org.cagrid.test.data.with.sdk43";
            }
            
            
            public String getNamespace() {
                return "http://sdk43.with.data.test.cagrid.org/DataServiceWithSdk43";
            }
            
            
            public String getName() {
                return "DataServiceWithSdk43";
            }
            
            
            protected String getServiceDirName() {
                return "DataServiceWithSdk43";
            }
        };
        return info;
    }


    @Override
    public String getServiceZipName() {
        return "DataServiceWithSdk43-1.3.zip";
    }
}
