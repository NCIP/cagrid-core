package gov.nih.nci.cagrid.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.TestCase;

/** 
 *  JarUtilitiesTestCase
 *  Tests functionality of the Jar utilities
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Feb 21, 2007 
 * @version $Id: JarUtilitiesTestCase.java,v 1.1 2008-01-11 15:49:48 dervin Exp $ 
 */
public class JarUtilitiesTestCase extends TestCase {
	public static final String SOURCE_DIR = "src" + File.separator + "java";
	public static final String TESTING_JAR_PREFIX = "JAR_UTILS_TEST_FILE";
    
    private File jarFile = null;
    
    public JarUtilitiesTestCase() {
        System.out.println("Construction");
    }
    
    
    public void setUp() {
        System.out.println("SET UP");
        jarFile = null;
        try {
            jarFile = File.createTempFile(TESTING_JAR_PREFIX, ".jar");
            System.out.println("Creating jar " + jarFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating temporary jar file");
        }
        if (jarFile.exists()) {
            boolean deleted = jarFile.delete();
            assertTrue("Old jar file existed, and was deleted", deleted);
        }
    }
    
    
    public void tearDown() {
        System.out.println("TEAR DOWN");
        jarFile.deleteOnExit();
    }
    

	public void testJar() {
        System.out.println("testJar()");
        // Jar the source directory
		jarSource();
	}
	
	
	public void testUnjar() {
        System.out.println("testUnjar()");
        jarSource();
		File outDir = new File("test" + File.separator + "jartest");
		if (!outDir.exists()) {
			boolean created = outDir.mkdirs();
			assertTrue("Directory " + outDir.getName() + " did not exist and was created", created);
		}
		try {
            // use Zip Utilities to unpack the jar
			ZipUtilities.unzip(jarFile, outDir);
		} catch (IOException ex) {
			ex.printStackTrace();
			fail("Failed to unpack the jar: " + ex.getMessage());
		}
		// assert that the directory structures are the same
		// list files in the original source dir
		FilenameFilter everythingFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return true;
			}
		};
		Set<String> expectedFiles = new HashSet<String>();
		File sourceDir = new File(SOURCE_DIR);
		String[] expectedFileNames = sourceDir.list(everythingFilter);
		Collections.addAll(expectedFiles, expectedFileNames);
		// list files in the extracted jar
		Set<String> extractedFiles = new HashSet<String>();
		String[] extractedFileNames = outDir.list(everythingFilter);
		Collections.addAll(extractedFiles, extractedFileNames);
		// check for congruence
		assertTrue("Expected files contains all extracted files", expectedFiles.containsAll(extractedFiles));
		assertTrue("Extracted files contain all expected files", extractedFiles.containsAll(expectedFiles));
		// blow away the extracted files directory
		Utils.deleteDir(outDir);
	}
	
	
	public void testExtractFromJar() {
        System.out.println("testExtractFromJar()");
        jarSource();
		// convert the name of a class to a file name
		String filePart = JarUtilities.class.getName().replace('.', File.separatorChar) + ".java";
		// get the original file contents
		String sourceFileName = SOURCE_DIR + File.separator + filePart;
		StringBuffer sourceFileContents = null;
		try {
            FileInputStream sourceStream = new FileInputStream(sourceFileName);
            sourceFileContents = Utils.inputStreamToStringBuffer(sourceStream);
            sourceStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			fail("Error loading source file contents: " + ex.getMessage());
		}
		// get the contents of the same file from the jar
		StringBuffer extractedContent = null;
		try {
			extractedContent = JarUtilities.getFileContents(new JarFile(jarFile), filePart);
		} catch (IOException ex) {
			ex.printStackTrace();
			fail("Error loading JARed file's contents: " + ex.getMessage());
		}
		// compare the extracted to the original
		assertEquals("Source and extracted file lengths differ", 
            sourceFileContents.length(), extractedContent.length());
		assertEquals("Source and extracted file conetnts differ",
            sourceFileContents.toString().trim(), extractedContent.toString().trim());
	}
    
    
    public void testInsertJarEntry() {   
        System.out.println("testInsertJarEntry()");
        jarSource();
        String insertText = "I am the very model of a modern major general";
        byte[] insertData = insertText.getBytes();
        String entryPath = "test" + File.separator + "insert" 
            + File.separator + "entry" + File.separator + "majorGeneral.txt";
        
        try {
            JarUtilities.insertEntry(jarFile, entryPath, insertData);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Failed to insert: " + ex.getMessage());
        }
        
        StringBuffer extractedData = null;
        try {
            extractedData = JarUtilities.getFileContents(new JarFile(jarFile), entryPath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error extracting entry: " + ex.getMessage());
        }
        
        String extractText = extractedData.toString();
        assertEquals("Extracted text did not match input text", insertText, extractText);
    }
    
    
    public void testRemoveJarEntry() {
        System.out.println("testRemoveJarEntry()");
        jarSource();
        // get an entry name to remove
        String filePart = JarUtilities.class.getName().replace('.', File.separatorChar) + ".java";
        
        // perform the removal
        byte[] cleanedJarData = null;
        try {
            cleanedJarData = JarUtilities.removeJarEntry(new JarFile(jarFile), filePart);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error removing jar entry: " + ex.getMessage());
        }
        
        // dump the cleaned jar to a temp jar
        File cleanedJar = null;
        try {
            cleanedJar = File.createTempFile(TESTING_JAR_PREFIX + "post-removal", ".jar");
            FileOutputStream fos = new FileOutputStream(cleanedJar);
            fos.write(cleanedJarData);
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            if (cleanedJar.exists()) {
                cleanedJar.delete();
            }
            fail("Error creating temporary jar: " + ex.getMessage());
        }
        
        // open the jar and search for the entry
        JarFile cleaned = null;
        try {
            cleaned = new JarFile(cleanedJar);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error opening cleaned jar file: " + ex.getMessage());
        }
        JarEntry removedEntry = cleaned.getJarEntry(filePart);
        assertNull("The entry was not removed", removedEntry);
        try {
            cleaned.close();
            cleanedJar.delete();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error closing or deleting temporary jar: " + ex.getMessage());
        }
    }
    
    
    private void jarSource() {
        try {
            JarUtilities.jarDirectory(new File(SOURCE_DIR), jarFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Failed to create jar file: " + ex.getMessage());
        }
    }
}
