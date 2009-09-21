package org.cagrid.data.test.upgrades;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UpgradeTestConstants {

    private static final String DATA_CURRENT_VERSION_PROPERTIES_FILE = "/data-version.properties";
    private static final String DATA_CURRENT_VERSION_PROPERTY = "dataTests.needs.data.version";

    
    public static String getCurrentDataVersion() throws RuntimeException {
        InputStream propStream = UpgradeTestConstants.class.getResourceAsStream(DATA_CURRENT_VERSION_PROPERTIES_FILE);
        if (propStream == null) {
            throw new RuntimeException(
                "Problem determining the data version constants; unable to load properties file ["
                    + DATA_CURRENT_VERSION_PROPERTIES_FILE + "]");
        }
        Properties props = new Properties();
        try {
            props.load(propStream);
        } catch (IOException e) {
            throw new RuntimeException(
                "Problem determining the data version constants; unable to load properties file ["
                    + DATA_CURRENT_VERSION_PROPERTIES_FILE + "]");
        }
        String dataVersion = props.getProperty(DATA_CURRENT_VERSION_PROPERTY);
        if (dataVersion == null) {
            throw new RuntimeException("Problem determining the data version constants; unable to load property ["
                + DATA_CURRENT_VERSION_PROPERTY + "] from properties file.");
        }

        return dataVersion;
    }
}
