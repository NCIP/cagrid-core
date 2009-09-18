package gov.nih.nci.cagrid.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/** 
 *  ZipUtilitiesTestCase
 *  Tests functionality of the zip utilities
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Feb 21, 2007 
 * @version $Id: ZipUtilitiesTestCase.java,v 1.9 2008-01-10 20:27:46 dervin Exp $ 
 */
public class ZipUtilitiesTestCase extends TestCase {
	public static final String SOURCE_DIR = "src" + File.separator + "java";
	public static final String TESTING_ZIP_PREFIX = "ZIP_UTILS_TEST_FILE";
    
    private File zipFile = null;
    
    public ZipUtilitiesTestCase() {
        System.out.println("Construction");
    }
    
    
    public void setUp() {
        System.out.println("SET UP");
        zipFile = null;
        try {
            zipFile = File.createTempFile(TESTING_ZIP_PREFIX, ".zip");
            System.out.println("Creating zip " + zipFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating temporary zip file");
        }
        if (zipFile.exists()) {
            boolean deleted = zipFile.delete();
            assertTrue("Old zip file existed, and was deleted", deleted);
        }
    }
    
    
    public void tearDown() {
        System.out.println("TEAR DOWN");
        zipFile.deleteOnExit();
    }
    

	public void testZip() {
        System.out.println("testZip()");
        // zip the source directory
		zipSource();
	}
	
	
	public void testUnzip() {
        System.out.println("testUnzip()");
        zipSource();
		File outDir = new File("test" + File.separator + "ziptest");
		if (!outDir.exists()) {
			boolean created = outDir.mkdirs();
			assertTrue("Directory " + outDir.getName() + " did not exist and was created", created);
		}
		try {
			ZipUtilities.unzip(zipFile, outDir);
		} catch (IOException ex) {
			ex.printStackTrace();
			fail("Failed to unzip: " + ex.getMessage());
		}
		// assert that the directory structures are the same
		// list files in the original source dir
		FilenameFilter everythingFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return true;
			}
		};
		Set expectedFiles = new HashSet();
		File sourceDir = new File(SOURCE_DIR);
		String[] expectedFileNames = sourceDir.list(everythingFilter);
		Collections.addAll(expectedFiles, (Object[]) expectedFileNames);
		// list files in the extracted zip
		Set extractedFiles = new HashSet();
		String[] extractedFileNames = outDir.list(everythingFilter);
		Collections.addAll(extractedFiles, (Object[]) extractedFileNames);
		// check for congruency
		assertTrue("Expected files contains all extracted files", expectedFiles.containsAll(extractedFiles));
		assertTrue("Extracted files contain all expected files", extractedFiles.containsAll(expectedFiles));
		// blow away the extracted files directory
		Utils.deleteDir(outDir);
	}
	
	
	public void testExtractFromZip() {
        System.out.println("testExtractFromZip()");
        zipSource();
		// convert the name of a class to a file name
		String filePart = ZipUtilities.class.getName().replace('.', File.separatorChar) + ".java";
		// get the original file contents
		String sourceFileName = SOURCE_DIR + File.separator + filePart;
		byte[] sourceFileContents = null;
		try {
			BufferedInputStream sourceInput = new BufferedInputStream(new FileInputStream(sourceFileName));
			ByteArrayOutputStream contents = new ByteArrayOutputStream();
			byte[] temp = new byte[8192];
			int read = -1;
			while ((read = sourceInput.read(temp)) != -1) {
				contents.write(temp, 0, read);
			}
			sourceInput.close();
			contents.flush();
			sourceFileContents = contents.toByteArray();
		} catch (IOException ex) {
			ex.printStackTrace();
			fail("Error loading source file contents: " + ex.getMessage());
		}
		// get the contents of the same file from the zip
		byte[] extractedContentBytes = null;
		try {
			extractedContentBytes = ZipUtilities.extractEntryContents(zipFile, filePart);
		} catch (IOException ex) {
			ex.printStackTrace();
			fail("Error loading zipped file's contents: " + ex.getMessage());
		}
		// compare the extracted to the original
		assertEquals(sourceFileContents.length, extractedContentBytes.length);
		for (int i = 0; i < sourceFileContents.length; i++) {
			assertTrue("Byte position " + i + "(" + sourceFileContents[i] + ") == (" + extractedContentBytes[i] + ")",
				sourceFileContents[i] == extractedContentBytes[i]);
		}
	}
    
    
    public void testInsertZipEntry() {   
        System.out.println("testInsertZipEntry()");
        zipSource();
        String insertText = "I am the very model of a modern major general";
        byte[] insertData = insertText.getBytes();
        String entryPath = "test" + File.separator + "insert" + File.separator + "entry" + File.separator + "majorGeneral.txt";
        
        try {
            ZipUtilities.insertEntry(zipFile, entryPath, insertData);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Failed to insert: " + ex.getMessage());
        }
        
        byte[] extractedData = null;
        try {
            extractedData = ZipUtilities.extractEntryContents(zipFile, entryPath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error extracting entry: " + ex.getMessage());
        }
        
        String extractText = new String(extractedData, 0, extractedData.length);
        assertEquals("Extracted bytes did not match input", insertText, extractText);
    }
    
    
    private void zipSource() {
        try {
            ZipUtilities.zipDirectory(new File(SOURCE_DIR), zipFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Failed to create zip file: " + ex.getMessage());
        }
    }
}
