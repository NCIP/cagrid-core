package gov.nih.nci.cagrid.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ProjectPropertiesVersionSetter {
    
    public static final String PROJECT_PROPERTIES_FILE = "project.properties";
    public static final String PROJECT_VERSION_PROPERTY = "project.version";

    public static void main(String[] args) {
        File basedir = new File(args[0]);
        String version = args[1];
        List<File> propsFiles = Utils.recursiveListFiles(basedir, new FileFilter() {
            
            public boolean accept(File pathname) {
                return pathname.getName().equals(PROJECT_PROPERTIES_FILE);
            }
        });
        for (File f : propsFiles) {
            if (f.isFile()) {
                PropertiesPreservingComments properties = new PropertiesPreservingComments();
                try {
                    properties.load(f);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
                properties.setProperty(PROJECT_VERSION_PROPERTY, version);
                try {
                    FileOutputStream fos = new FileOutputStream(f);
                    properties.store(fos);
                    fos.close();
                    System.out.println("Updated " + f.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }
        System.out.println("Done");
    }
}
