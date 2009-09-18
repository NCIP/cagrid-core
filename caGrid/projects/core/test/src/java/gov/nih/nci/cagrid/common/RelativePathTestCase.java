package gov.nih.nci.cagrid.common;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelativePathTestCase extends TestCase {
    private static final Log LOG = LogFactory.getLog(RelativePathTestCase.class);

    public void testSameDirSameFileName() {
        File f1 = null;
        File f2 = null;
        try {
            f1 = new File("./foo");
            f2 = new File(f1.getCanonicalPath());
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error setting up test file: " + ex.getMessage());
        }
        String relPath = getRelativePath(f1, f2);
        assertTrue("Relative path was not just the file name", f2.getName().equals(relPath));
    }
    
    
    public void testSameDirDifferentFileName() {
        File f1 = null;
        File f2 = null;
        try {
            f1 = new File("./foo");
            f2 = new File("./bar").getCanonicalFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error setting up test file: " + ex.getMessage());
        }
        String relPath = getRelativePath(f1, f2);
        assertTrue("Relative path was not just the file name", f2.getName().equals(relPath));
    }
    
    
    public void testCompletelyDifferentUnixPaths() {
        File f1 = null;
        File f2 = null;
        try {
            f1 = new File("/foo/bar/dir");
            f2 = new File("/abc/xyz/dir").getCanonicalFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error setting up test file: " + ex.getMessage());
        }
        String relPath = getRelativePath(f1, f2);
        assertEquals("Relative path was not the full path name", f2.getAbsolutePath(), relPath);
    }
    
    
    public void testNestedPath() {
        String nest = "bar" + File.separator + "xyz.txt";
        File f1 = null;
        File f2 = null;
        try {
            f1 = new File("." + File.separator + "foo") {
                public boolean isDirectory() {
                    return true;
                }
            };
            f2 = new File(f1.getCanonicalPath() + File.separator + nest);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error setting up test file: " + ex.getMessage());
        }
        String relPath = getRelativePath(f1, f2);
        assertEquals("Relative path was not the nested path", nest, relPath);
    }
    
    
    private String getRelativePath(File f1, File f2) {
        String relPath = null;
        try {
            LOG.debug("Finding relative path between " 
                + f1.getCanonicalPath() + " and " + f2.getCanonicalPath());
            relPath = Utils.getRelativePath(f1, f2);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error obtaining relative path: " + ex.getMessage());
        }
        LOG.debug("Relative path found to be: " + relPath);
        return relPath;
    }
}
