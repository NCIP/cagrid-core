package gov.nih.nci.cagrid.data.style;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.style.DataServiceStyle;
import gov.nih.nci.cagrid.introduce.common.IntroducePropertiesManager;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/** 
 *  ServiceStyleLoader
 *  Utility to load service styles available to the data service extension
 * 
 * @author David Ervin
 * 
 * @created Jul 9, 2007 12:34:36 PM
 * @version $Id: ServiceStyleLoader.java,v 1.3 2008-12-01 19:11:59 dervin Exp $ 
 */
public class ServiceStyleLoader {
    
    /**
     * Gets a list of available Data Service styles.  Styles which present in the styles directory
     * but do not explicitly match the current Introduce version as comptaible will <b>not</b> be included.
     *  
     * @return
     * @throws Exception
     */
    public static List<ServiceStyleContainer> getAvailableStyles() throws Exception {
        return getAvailableStyles(false);
    }
    
    
    /**
     * Gets a list of available Data Service styles.  Styles present in the styles directory which
     * do not explicitly match the current Introduce version as compatible may optionally be returned
     * by setting the <i>includeIncompatibleVersions</i> parameter to 'true'
     * 
     * @param includeIncompatibleVersions
     *      If 'true', Styles which may not be compatible with the current Introduce version are not returned.
     * @return
     * @throws Exception
     */
    public static List<ServiceStyleContainer> getAvailableStyles(boolean includeIncompatibleVersions) throws Exception {
        // get the current Introduce version
        String introduceVersion = IntroducePropertiesManager.getIntroduceVersion();
        
        // list to store style descriptions
        List<ServiceStyleContainer> styles = new ArrayList<ServiceStyleContainer>();
        
        // locate the styles directory
        File extensionsDir = ExtensionsLoader.getInstance().getExtensionsDir();
        File stylesDir = new File(extensionsDir.getAbsolutePath() + File.separator 
            + "data" + File.separator + DataServiceConstants.SERVICE_STYLES_DIR_NAME);
        
        if (stylesDir.exists() && stylesDir.isDirectory()) {
            // load the styles
            File[] styleDirs = stylesDir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            for (File styleDir : styleDirs) {
                File styleXmlFile = new File(styleDir.getAbsolutePath() 
                    + File.separator + DataServiceConstants.SERVICE_STYLE_FILE_NAME);
                DataServiceStyle style = (DataServiceStyle) Utils.deserializeDocument(
                    styleXmlFile.getAbsolutePath(), DataServiceStyle.class);
                boolean okToAdd = true;
                if (!includeIncompatibleVersions) {
                    // check for compatibility
                    okToAdd = false;
                    if (style.getCompatibleCaGridVersions() != null && 
                        style.getCompatibleCaGridVersions().getVersion() != null) {
                        for (CompatibleCaGridVersionsVersion version : style.getCompatibleCaGridVersions().getVersion()) {
                            if (introduceVersion.equals(version.get_value())) {
                                okToAdd = true;
                                break;
                            }
                        }
                    }
                }
                if (okToAdd) {
                    ServiceStyleContainer container = new ServiceStyleContainer(style, styleDir);
                    styles.add(container);
                }
            }
        }
        return styles;
    }
    
    
    /**
     * Gets the service style identified by name
     * 
     * @param name
     *      The name of the service style to load
     * @return
     * @throws Exception
     */
    public static ServiceStyleContainer getStyle(String name) throws Exception {
        return getStyle(name, false);
    }
    
    
    
    /**
     * Gets the service style identified by name.  A style which does not explicitly 
     * match the current Introduce version as compatible may optionally be returned
     * by setting the <i>includeIncompatibleVersions</i> parameter to 'true'
     * 
     * @param name
     * @param includeIncompatibleVersions
     * @return
     * @throws Exception
     */
    public static ServiceStyleContainer getStyle(String name, boolean includeIncompatibleVersions) throws Exception {
        List<ServiceStyleContainer> styles = getAvailableStyles(includeIncompatibleVersions);
        for (ServiceStyleContainer container : styles) {
            if (container.getServiceStyle().getName().equals(name)) {
                return container;
            }
        }
        return null;
    }
}
