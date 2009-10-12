package gov.nih.nci.cagrid.data.style;

import gov.nih.nci.cagrid.introduce.common.FileFilters;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/** 
 *  ServiceStyleContainer
 *  Records the service style descriptor and some metadata together 
 * 
 * @author David Ervin
 * 
 * @created Jul 9, 2007 12:43:54 PM
 * @version $Id: ServiceStyleContainer.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class ServiceStyleContainer {

    private DataServiceStyle style;
    private File styleDir;
    private ClassLoader classLoader = null;
    
    public ServiceStyleContainer(DataServiceStyle style, File styleDir) {
        this.style = style;
        this.styleDir = styleDir;
    }
    
    
    public DataServiceStyle getServiceStyle() {
        return style;
    }
    
    
    public File getStyleDirectory() {
        return styleDir;
    }
    
    
    public File[] getStyleLibraries() {
        File styleLib = new File(styleDir.getAbsolutePath() + File.separator + "lib");
        if (styleLib.exists() && styleLib.isDirectory()) {
            File[] jars = styleLib.listFiles(new FileFilters.JarFileFilter());
            return jars;
        }
        return null;
    }
    
    
    /**
     * Loads the service creation post processor for this service style
     * 
     * @return
     *      The service style's creation post processor, or <code>null</code> if none is supplied
     * @throws Exception
     */
    public StyleCreationPostProcessor loadCreationPostProcessor() throws Exception {
        if (style.getCreationHelper() != null && style.getCreationHelper().getPostCreationClassname() != null) {
            String classname = style.getCreationHelper().getPostCreationClassname();
            ClassLoader loader = createClassLoader();
            Class<?> processorClass = loader.loadClass(classname);
            return (StyleCreationPostProcessor) processorClass.newInstance();
        }
        return null;
    }
    
    
    /**
     * Loads the service codegen pre processor for this service style
     * 
     * @return
     *      The service style's codegen pre processor, or <code>null</code> if none is supplied
     * @throws Exception
     */
    public StyleCodegenPreProcessor loadCodegenPreProcessor() throws Exception {
        if (style.getCodegenHelpers() != null && style.getCodegenHelpers().getPreCodegenClassname() != null) {
            String classname = style.getCodegenHelpers().getPreCodegenClassname();
            ClassLoader loader = createClassLoader();
            Class<?> processorClass = loader.loadClass(classname);
            return (StyleCodegenPreProcessor) processorClass.newInstance();
        }
        return null;
    }
    
    
    /**
     * Loads the service codegen post processor for this service style
     * 
     * @return
     *      The service style's codegen post processor, or <code>null</code> if none is supplied
     * @throws Exception
     */
    public StyleCodegenPostProcessor loadCodegenPostProcessor() throws Exception {
        if (style.getCodegenHelpers() != null && style.getCodegenHelpers().getPostCodegenClassname() != null) {
            String classname = style.getCodegenHelpers().getPostCodegenClassname();
            ClassLoader loader = createClassLoader();
            Class<?> processorClass = loader.loadClass(classname);
            return (StyleCodegenPostProcessor) processorClass.newInstance();
        }
        return null;
    }
    
    
    public StyleVersionUpgrader loadVersionUpgrader(String fromVersion, String toVersion) throws Exception {
        if (style.getVersionUpgrade() != null) {
            String classname = null;
            for (VersionUpgrade upgrade : style.getVersionUpgrade()) {
                if (upgrade.getFromVersion().equals(fromVersion) && upgrade.getToVersion().equals(toVersion)) {
                    classname = upgrade.getClassname();
                    break;
                }
            }
            ClassLoader loader = createClassLoader();
            Class<?> upgraderClass = loader.loadClass(classname);
            return (StyleVersionUpgrader) upgraderClass.newInstance();
        }
        return null;
    }
    
    
    /**
     * Creates a class loader which can load classes from the 
     * libraries in the style's lib directory 
     * @return
     *      A URL Class Loader using the libraries in the style's lib directory, or the
     *      current thread's context class loader if no further libraries are available.
     * @throws MalformedURLException
     */
    public ClassLoader createClassLoader() throws MalformedURLException {
        if (classLoader == null) {
            File[] libs = getStyleLibraries();
            if (libs != null) {
                URL[] urls = new URL[libs.length];
                for (int i = 0; i < libs.length; i++) {
                    urls[i] = libs[i].toURL();
                }
                classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
            } else {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
        }
        return classLoader;
    }
}
