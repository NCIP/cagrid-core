package gov.nih.nci.cagrid.data.style;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.common.FileFilters;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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
    
    public static final String NOCOPY_LIBS_FILE = "nocopy.libs";

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
    
    
    public File[] getStyleCopyLibs() throws IOException {
        File styleLib = new File(styleDir, "lib");
        if (styleLib.exists() && styleLib.isDirectory()) {
            final Set<String> nocopy = new HashSet<String>();
            File nocopyFile = new File(styleDir, NOCOPY_LIBS_FILE);
            if (nocopyFile.exists()) {
                StringBuffer nocopyContents = Utils.fileToStringBuffer(nocopyFile);
                StringTokenizer nocopyTok = new StringTokenizer(nocopyContents.toString(), ",");
                while (nocopyTok.hasMoreTokens()) {
                    nocopy.add(nocopyTok.nextToken());
                }
            }
            File[] jars = styleLib.listFiles(new FileFilters.JarFileFilter() {
                public boolean accept(File path) {
                    boolean ok = super.accept(path);
                    return !nocopy.contains(path.getName()) && ok;
                }
            });
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
            File[] libs = getStyleRuntimeLibs();
            if (libs != null) {
                URL[] urls = new URL[libs.length];
                for (int i = 0; i < libs.length; i++) {
                    urls[i] = libs[i].toURI().toURL();
                }
                classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
            } else {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
        }
        return classLoader;
    }
    
    
    protected File[] getStyleRuntimeLibs() {
        File styleLib = new File(styleDir, "lib");
        if (styleLib.exists() && styleLib.isDirectory()) {
            File[] jars = styleLib.listFiles(new FileFilters.JarFileFilter());
            return jars;
        }
        return null;
    }
}
