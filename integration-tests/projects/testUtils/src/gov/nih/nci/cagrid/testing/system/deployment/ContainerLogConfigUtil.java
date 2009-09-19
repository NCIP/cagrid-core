package gov.nih.nci.cagrid.testing.system.deployment;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Utility to configure logging messages printed by service containers
 * 
 * @author David
 */
public class ContainerLogConfigUtil {
    
    public static final String LOG_FILE_NAME = "log4j.properties";
    public static final String CATEGORY_PREFIX = "log4j.category";

    private ServiceContainer container = null;
    
    public ContainerLogConfigUtil(ServiceContainer container) {
        this.container = container;
    }
    
    
    public void setSoapLoggingEnabled(boolean enable) throws IOException {
        StringBuffer config = getConfiguration();
        String soapLogging = CATEGORY_PREFIX + ".org.globus.wsrf.handlers.MessageLoggingHandler=DEBUG";
        if (enable) {
            uncommentLine(config, soapLogging);
        } else {
            commentOutLine(config, soapLogging);
        }
        storeConfiguration(config);
    }
    
    
    public void setIndexDebugEnabled(boolean enable) throws IOException {
        StringBuffer config = getConfiguration();
        String indexLogging = CATEGORY_PREFIX + ".org.globus.mds=DEBUG";
        if (enable) {
            uncommentLine(config, indexLogging);
        } else {
            commentOutLine(config, indexLogging);
        }
        storeConfiguration(config);
    }
    
    
    public void setPackageDebug(String packageName, boolean enable) throws IOException {
        StringBuffer config = getConfiguration();
        String configLine = CATEGORY_PREFIX + "." + packageName + "=DEBUG";
        // see if the package has been configured
        if (!packageIsConfigured(config, packageName)) {
            // add the line to the config
            config.append("\n").append(configLine).append("\n");
        }
        // turn on / off debugging for this package
        if (enable) {
            uncommentLine(config, configLine);
        } else {
            commentOutLine(config, configLine);
        }
        storeConfiguration(config);
    }
    
    
    private void commentOutLine(StringBuffer config, String line) {
        if (!isCommentedOut(config, line)) {
            int index = config.indexOf(line);
            config.insert(index, "# ");
        }
    }
    
    
    private void uncommentLine(StringBuffer config, String line) {
        if (isCommentedOut(config, line)) {
            String findme = "# " + line;
            int index = config.indexOf(findme);
            config.delete(index, index + "# ".length());
        }
    }
    
    
    private boolean isCommentedOut(StringBuffer config, String line) {
        return config.indexOf("# " + line) != -1;
    }
    
    
    private boolean packageIsConfigured(StringBuffer config, String packName) {
        String line = CATEGORY_PREFIX + "." + packName + "=DEBUG";
        return config.indexOf(line) != -1;
    }
    
    
    private StringBuffer getConfiguration() throws IOException {
        return Utils.fileToStringBuffer(getLogConfigFile());
    }
    
    
    private void storeConfiguration(StringBuffer config) throws IOException {
        Utils.stringBufferToFile(config, getLogConfigFile().getAbsolutePath());
    }
    
    
    private File getLogConfigFile() {
        File configFile = null;
        if (container instanceof GlobusServiceContainer) {
            configFile = new File(container.getProperties().getContainerDirectory(), LOG_FILE_NAME);
        } else if (container instanceof TomcatServiceContainer) {
            configFile = new File(container.getProperties().getContainerDirectory(), 
                "webapps/wsrf/WEB-INF/classes/" + LOG_FILE_NAME);
        }
        return configFile;
    }
}
