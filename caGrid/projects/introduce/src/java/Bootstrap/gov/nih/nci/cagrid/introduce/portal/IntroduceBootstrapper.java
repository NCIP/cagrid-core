package gov.nih.nci.cagrid.introduce.portal;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * IntroduceBootstrapper
 * 
 * This tool initializes the classpath used by Introduce all at once so that
 * Axis can find the classes it needs to know about for serialization
 * and extensions load up the way they should, all without making the initial
 * classpath on the ant command so long that Windows truncates it and
 * refuses to launch the process
 * 
 * @author David
 *
 */
public class IntroduceBootstrapper {
    
    public static final String INTRODUCE_CLASSNAME = "gov.nih.nci.cagrid.introduce.portal.Introduce";
    
    public static final String GLOBUS_LOCATION_ENV = "GLOBUS_LOCATION";
    public static final String ANT_HOME_ENV = "ANT_HOME";
    
    public static final String[] DIR_LOCATIONS = new String[] {
        "resources/portal/introduce",
        "extensions",
        "doc/help"
    };
    
    public static final String[] LIB_LOCATIONS = new String[] {
        "build" + File.separator + "jars",
        "lib", 
        "ext" + File.separator + "dependencies" + File.separator + "jars",
        "extensions" + File.separator + "lib"
    };

    
    public static void main(String[] args) {
        List<File> classpathSource = new ArrayList<File>();
        List<String> locations = new ArrayList<String>();
        locations.add(System.getenv(GLOBUS_LOCATION_ENV) + File.separator + "lib");
        locations.add(System.getenv(ANT_HOME_ENV) + File.separator + "lib");
        Collections.addAll(locations, LIB_LOCATIONS);
        for (String location : LIB_LOCATIONS) {
            File baseDir = new File(location);
            File[] jars = baseDir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(".jar");
                }
            });
            Collections.addAll(classpathSource, jars);
        }
        for (String location : DIR_LOCATIONS) {
            File dir = new File(location);
            classpathSource.add(dir);
        }
        URL[] urls = new URL[classpathSource.size()];
        try {
            for (int i = 0; i < classpathSource.size(); i++) {
                urls[i] = classpathSource.get(i).toURI().toURL();
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        URLClassLoader loader = new URLClassLoader(urls, IntroduceBootstrapper.class.getClassLoader());
        try {
            Class<?> introduce = loader.loadClass(INTRODUCE_CLASSNAME);
            Method introduceMain = introduce.getMethod("main", String[].class);
            introduceMain.invoke(null, new Object[] {args});
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
