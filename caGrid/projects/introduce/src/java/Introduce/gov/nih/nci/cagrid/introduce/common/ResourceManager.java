package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.ZipUtilities;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.cagrid.grape.GridApplication;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class ResourceManager {
    private static final Logger logger = Logger.getLogger(ResourceManager.class);
    public static final int MAX_ARCHIVE = 5;
    
    public final static String STATE_FILE = "introduce.state.properties";

    public final static String CACHE_POSTFIX = "_backup.zip";

    public final static String LAST_DIRECTORY = "introduce.lastdir";

    public final static String LAST_DEPLOYMENT = "introduce.lastdeployment";

    public final static String LAST_FILE = "introduce.lastfile";


    public static File getIntroduceUserHome() {
        String userHome = System.getProperty("user.home");
        File userHomeF = new File(userHome);
        File caGridCache = new File(userHomeF.getAbsolutePath() + File.separator + ".introduce_"
            + IntroducePropertiesManager.getIntroduceVersion().replace(".", "_"));
        if (!caGridCache.exists()) {
            caGridCache.mkdirs();
        }
        return caGridCache;
    }


    public static String getResourcePath() {
        File introduceCache = getIntroduceUserHome();
        introduceCache.mkdir();
        return introduceCache.getAbsolutePath();
    }
    
    
    public static String getServiceCachePath() {
        File introduceServiceCache = new File(getResourcePath() + File.separator + "serviceCache");
        introduceServiceCache.mkdir();
        return introduceServiceCache.getAbsolutePath();
    }



    public static String getStateProperty(String key) throws IOException {
        File lastDir = new File(getResourcePath() + File.separator + STATE_FILE);
        Properties properties = new Properties();
        if (!lastDir.exists()) {
            lastDir.createNewFile();
        }
        properties.load(new FileInputStream(lastDir));
        return properties.getProperty(key);
    }


    public static void setStateProperty(String key, String value) throws IOException {
        if (key != null) {
            File lastDir = new File(getResourcePath() + File.separator + STATE_FILE);
            if (!lastDir.exists()) {
                lastDir.createNewFile();
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(lastDir));
            properties.setProperty(key, value);
            properties.store(new FileOutputStream(lastDir), "");
        }
    }


    public static synchronized void purgeArchives(String serviceName) {
        String introduceCache = getServiceCachePath();

        final String finalServiceName = serviceName;
        FilenameFilter f = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.indexOf(finalServiceName + "_") != -1;
            }
        };

        File introduceCacheFile = new File(introduceCache);
        String[] cacheFiles = introduceCacheFile.list(f);
        List cacheFilesList = Arrays.asList(cacheFiles);
        Collections.sort(cacheFilesList, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(cacheFilesList);

        for (int i = 0; i < cacheFilesList.size(); i++) {
            logger.debug("Removing file from cache: " + i + "  " + introduceCache + File.separator
                + cacheFilesList.get(i));
            File cacheFile = new File(introduceCache + File.separator + cacheFilesList.get(i));
            cacheFile.delete();
        }
    }


    public static synchronized void createArchive(String id, String serviceName, String baseDir)
        throws FileNotFoundException, IOException {
        File dir = new File(baseDir);

        String introduceCache = getServiceCachePath();

        // Create the ZIP file
        String outFilename = introduceCache + File.separator + serviceName + "_" + id + CACHE_POSTFIX;
        logger.debug("Creating service archive: " + outFilename);

        ZipUtilities.zipDirectory(dir, new File(outFilename));

        // cleanup if there are more that MAX_ARCHIVE files in the backup area
        cleanup(serviceName);
    }


    private static void cleanup(String serviceName) {
        String introduceCache = getServiceCachePath();

        final String finalServiceName = serviceName;
        FilenameFilter f = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.indexOf(finalServiceName + "_") != -1;
            }
        };

        File introduceCacheFile = new File(introduceCache);
        String[] cacheFiles = introduceCacheFile.list(f);
        List cacheFilesList = Arrays.asList(cacheFiles);
        Collections.sort(cacheFilesList, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(cacheFilesList);

        if (cacheFilesList.size() > MAX_ARCHIVE) {
            for (int i = MAX_ARCHIVE; i < cacheFilesList.size(); i++) {
                logger.debug("Removing file from cache: " + i + "  " + introduceCache + File.separator
                    + cacheFilesList.get(i));
                File cacheFile = new File(introduceCache + File.separator + cacheFilesList.get(i));
                cacheFile.delete();
            }
        }
    }


    public static synchronized void restoreSpecific(String currentId, String serviceName, String baseDir)
        throws FileNotFoundException, IOException {

        // remove the directory first
        boolean deleted = Utils.deleteDir(new File(baseDir));
        if (!deleted) {
            logger.warn("Was not able to completely remove the service before restoring the new one. "
                + "May be unused new files leftover.");
        }

        File introduceCache = new File(getServiceCachePath());
        introduceCache.mkdir();
        File cachedFile = new File(introduceCache.getAbsolutePath() + File.separator + serviceName + "_" + currentId
            + CACHE_POSTFIX);

        logger.debug("Restoring service from archive:" + cachedFile.getAbsolutePath());

        ZipUtilities.unzip(cachedFile, new File(baseDir));
    }
    
    
    public static String[] getBackups(String serviceName){
    	
    	File introduceCache = new File(getServiceCachePath());
        final String finalServiceName = serviceName;
        FilenameFilter f = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.indexOf(finalServiceName + "_") != -1;
            }
        };

        String[] cacheFiles = introduceCache.list(f);
        
        return cacheFiles;
    }


    public static synchronized void restoreLatest(String currentId, String serviceName, String baseDir)
        throws FileNotFoundException, IOException, Exception {
    	File introduceCache = new File(getServiceCachePath());
        String[] cacheFiles = getBackups(serviceName);
        
        long thisTime = Long.parseLong(currentId);
        long lastTime = 0;
        for (int i = 0; i < cacheFiles.length; i++) {
            StringTokenizer strtok = new StringTokenizer(cacheFiles[i], "_", false);
            strtok.nextToken();
            String timeS = strtok.nextToken();
            long time = Long.parseLong(timeS);
            if ((time > lastTime) && (time < thisTime)) {
                lastTime = time;
            }
        }

        File cachedFile = new File(introduceCache.getAbsolutePath() + File.separator + serviceName + "_"
            + String.valueOf(lastTime) + CACHE_POSTFIX);

        if (cachedFile.exists() && cachedFile.canRead()) {
            // remove the directory
            boolean deleted = Utils.deleteDir(new File(baseDir));
            if (!deleted) {
                logger.warn("Introduce was not able to completely remove the service before restoring the old one.  "
                    + "There may be unused new files leftover.");
            }
            
            ZipUtilities.unzip(cachedFile, new File(baseDir));
        } else {
            throw new Exception("Cache file does not exist or is not readable : " + cachedFile.getAbsolutePath());
        }

       
    }


    public static String promptDir(String defaultLocation) throws IOException {
        return promptDir(GridApplication.getContext().getApplication(), defaultLocation);
    }


    public static String promptDir(Component owner, String defaultLocation) throws IOException {
        JFileChooser chooser = null;
        if ((defaultLocation != null) && (defaultLocation.length() > 0) && new File(defaultLocation).exists()) {
            chooser = new JFileChooser(new File(defaultLocation));
        } else if (getStateProperty(LAST_DIRECTORY) != null) {
            chooser = new JFileChooser(new File(getStateProperty(LAST_DIRECTORY)));
        } else {
            chooser = new JFileChooser();
        }
        chooser.setApproveButtonText("Open");
        chooser.setDialogTitle("Select Directory");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        GridApplication.getContext().centerComponent(chooser);

        int returnVal = chooser.showOpenDialog(owner);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            setStateProperty(ResourceManager.LAST_DIRECTORY, chooser.getSelectedFile().getAbsolutePath());
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }


    public static String promptFile(String defaultLocation, FileFilter filter) throws IOException {
        return promptFile(GridApplication.getContext().getApplication(), defaultLocation, filter);
    }


    public static String promptFile(Component owner, String defaultLocation, FileFilter filter) throws IOException {
        String[] files = internalPromptFiles(owner, defaultLocation, filter, false, "Select File");
        if (files != null) {
            return files[0];
        }
        return null;
    }


    public static String[] promptMultiFiles(String defaultLocation, FileFilter filter) throws IOException {
        return promptMultiFiles(GridApplication.getContext().getApplication(), defaultLocation, filter);
    }


    public static String[] promptMultiFiles(Component owner, String defaultLocation, FileFilter filter)
        throws IOException {
        String[] files = internalPromptFiles(owner, defaultLocation, filter, true, "Select File(s)");
        return files;
    }


    private static String[] internalPromptFiles(Component owner, String defaultLocation, FileFilter filter,
        boolean multiSelect, String title) throws IOException {
        String[] fileNames = null;
        JFileChooser chooser = null;
        if ((defaultLocation != null) && (defaultLocation.length() != 0) && new File(defaultLocation).exists()) {
            chooser = new JFileChooser(new File(defaultLocation));
        } else if (getStateProperty(LAST_FILE) != null) {
            chooser = new JFileChooser(new File(getStateProperty(LAST_FILE)));
        } else {
            chooser = new JFileChooser();
        }
        chooser.setApproveButtonText("Open");
        chooser.setApproveButtonToolTipText("Open");
        chooser.setMultiSelectionEnabled(multiSelect);
        chooser.setDialogTitle(title);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        GridApplication.getContext().centerComponent(chooser);

        int choice = chooser.showOpenDialog(owner);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File[] files = null;
            if (multiSelect) {
                files = chooser.getSelectedFiles();
            } else {
                files = new File[]{chooser.getSelectedFile()};
            }
            setStateProperty(ResourceManager.LAST_FILE, files[0].getAbsolutePath());
            fileNames = new String[files.length];
            for (int i = 0; i < fileNames.length; i++) {
                fileNames[i] = files[i].getAbsolutePath();
            }
        }
        return fileNames;
    }


    public static void main(String[] args) {
        try {
            ResourceManager.createArchive(String.valueOf(System.currentTimeMillis()), "HelloWorld", "c:\\HelloWorld");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
