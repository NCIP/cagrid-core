package gov.nih.nci.cagrid.sdkquery4.style.upgrade;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.style.StyleVersionUpgrader;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.sdkquery4.processor2.SDK4CQL2QueryProcessor;

public class UpgradeFrom1pt2to1pt4 implements StyleVersionUpgrader {

    private static Log LOG = LogFactory.getLog(UpgradeFrom1pt2to1pt4.class);

    public void upgradeStyle(ServiceInformation serviceInformation, ExtensionTypeExtensionData extensionData,
        String serviceFromVersion, String serviceToVersion) throws Exception {
        // load the style
        ServiceStyleContainer styleContainer = ServiceStyleLoader.getStyle("caCORE SDK v 4.0");
        
        // update style libraries
        File[] upgradeLibs = styleContainer.getStyleCopyLibs();
        File serviceLibDir = new File(serviceInformation.getBaseDirectory(), "lib");
        // copy in new libs, remove old ones
        for (File upgradeLib : upgradeLibs) {
            File oldExactMatch = new File(serviceLibDir, upgradeLib.getName());
            LOG.debug("Looking for old non-caGrid library: " + oldExactMatch.getName());
            if (oldExactMatch.exists()) {
                oldExactMatch.delete();
                LOG.debug("Deleted old library: " + oldExactMatch.getName());
            }
            if (upgradeLib.getName().startsWith("caGrid-")) {
                int versionIndex = upgradeLib.getName().indexOf("-1.4.jar");
                File oldCagridMatch = new File(serviceLibDir, 
                    upgradeLib.getName().substring(0, versionIndex) + "-1.2.jar");
                LOG.debug("Looking for old caGrid 1.2 library " + oldCagridMatch.getName());
                if (oldCagridMatch.exists()) {
                    oldCagridMatch.delete();
                    LOG.debug("Deleted old library: " + oldCagridMatch.getName());
                }
            }
            File copyLib = new File(serviceLibDir, upgradeLib.getName());
            Utils.copyFile(upgradeLib, copyLib);
            LOG.debug("Copied new library: " + upgradeLib.getName());
        }
        
        // set CQL 2 query processor classname property
        CommonTools.setServiceProperty(serviceInformation.getServiceDescriptor(),
            QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY, 
            SDK4CQL2QueryProcessor.class.getName(), false);
        
        // add CQL 2 query processor properties
        SDK4CQL2QueryProcessor processor = (SDK4CQL2QueryProcessor) styleContainer.createClassLoader()
            .loadClass(SDK4CQL2QueryProcessor.class.getName()).newInstance();
        Properties processorProperties = processor.getRequiredParameters();
        Set<String> fromEtc = processor.getParametersFromEtc();
        for (Object key : processorProperties.keySet()) {
            String propName = (String) key;
            String def = processorProperties.getProperty(propName);
            CommonTools.setServiceProperty(serviceInformation.getServiceDescriptor(),
                QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CONFIG_PREFIX + propName,
                def, fromEtc.contains(propName));
        }
    }
}
