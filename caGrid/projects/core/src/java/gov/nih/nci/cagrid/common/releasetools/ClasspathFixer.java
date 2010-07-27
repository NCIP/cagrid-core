package gov.nih.nci.cagrid.common.releasetools;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;



public class ClasspathFixer {
    
    private String baseSearchDir = null;
    
    public ClasspathFixer(String baseSearchDir) {
        this.baseSearchDir = baseSearchDir;
    }
    
    
    public void startFixing() {
        List<File> classpathFiles = recursiveListFiles(new File(baseSearchDir),
            new FileFilter() {
                
                public boolean accept(File pathname) {
                    return pathname.getName().equals(".classpath");
                }
            });
        for (File f : classpathFiles) {
            if (f.isFile()) {
                System.out.println("Working on " + f.getAbsolutePath());
                try {
                    Document doc = fileToDocument(f);
                    List libElements = doc.getRootElement().getContent(new Filter() {
                        public boolean matches(Object o) {
                            if (o instanceof Element) {
                                Element e = (Element) o;
                                if (e.getName().equals("classpathentry") &&
                                    "lib".equals(e.getAttributeValue("kind"))) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                    Iterator libElemIter = libElements.iterator();
                    while (libElemIter.hasNext()) {
                        Element entryElem = (Element) libElemIter.next();
                        File projectBase = f.getParentFile();
                        String libPath = entryElem.getAttributeValue("path");
                        File libFile = new File(projectBase, libPath);
                        String libName = libFile.getName();
                        if (libName.startsWith("caGrid-") && libName.endsWith("-1.4-dev.jar")) {
                            System.out.println("Found a library to fix up ("  + libPath + ")");
                            int endIndex = libPath.lastIndexOf("-1.4-dev.jar");
                            libPath = libPath.substring(0, endIndex);
                            libPath += "-1.4.jar";
                            System.out.println("\tFixed up to " + libPath);
                            entryElem.setAttribute("path", libPath);
                        }
                    }
                    saveDocument(doc, f);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    

    public static void main(String[] args) {
        ClasspathFixer fixer = new ClasspathFixer("w:/Projects/cagrid/caGrid-1_4_release/Software/core");
        fixer.startFixing();
    }
    
    
    public static List<File> recursiveListFiles(File baseDir, final FileFilter filter) {
        FileFilter dirFilter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() || filter.accept(pathname);
            }
        };
        File[] fileArray = baseDir.listFiles(dirFilter);
        List<File> files = new ArrayList<File>(fileArray.length);
        for (int i = 0; i < fileArray.length; i++) {
            if (fileArray[i].isDirectory()) {
                files.addAll(recursiveListFiles(fileArray[i], filter));
            } else {
                files.add(fileArray[i]);
            }
        }
        return files;
    }
    
    
    public static Document fileToDocument(File f) throws Exception {
        try {
            InputStream fis = new FileInputStream(f);
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(fis);
            fis.close();
            return doc;
        } catch (Exception e) {
            throw new Exception("Document construction failed: " + e.getMessage(), e);
        }
    }
    
    
    public static void saveDocument(Document doc, File out) throws Exception {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        FileWriter writer = new FileWriter(out);
        outputter.output(doc, writer);
        writer.close();
    }
}
