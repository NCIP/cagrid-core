package gov.nih.nci.cagrid.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/** 
 *  ZipUtilities
 *  Utilities to maniuplate zip files
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Feb 21, 2007 
 * @version $Id: ZipUtilities.java,v 1.6 2008-01-11 15:49:48 dervin Exp $ 
 */
public class ZipUtilities {
	
	/**
	 * Unzips a zip compressed file to a specific directory
	 * 
	 * @param zip
	 * 		The zip compressed file
	 * @param location
	 * 		The location to unzip into
	 * @throws IOException
	 */
	public static void unzip(File zip, File location) throws IOException {
		FileInputStream zipFileInput = new FileInputStream(zip.getAbsoluteFile());
		ZipInputStream zipInput = new ZipInputStream(zipFileInput);
		ZipEntry entry = null;
		String baseDir = null;
		if (location == null) {
			baseDir = zip.getParentFile().getAbsolutePath();
		} else {
			baseDir = location.getAbsolutePath();
		}
		while ((entry = zipInput.getNextEntry()) != null) {
			String name = entry.getName();
			File outFile = new File(baseDir + File.separator + name);
			if (entry.isDirectory()) {
				outFile.mkdirs();
			} else {
				if (!outFile.getParentFile().exists()) {
					outFile.getParentFile().mkdirs();
				}
				outFile.createNewFile();
				BufferedOutputStream fileOut = new BufferedOutputStream(
					new FileOutputStream(outFile));
				copyStreams(zipInput, fileOut);
				fileOut.flush();
				fileOut.close();
			}
		}
		zipInput.close();
	}
	

	/**
	 * Unzips a zip compressed file in the directory it resides in
	 * 
	 * @param zip
	 * 		The zip compressed file
	 * @throws IOException
	 */
	public static void unzipInPlace(File zip) throws IOException {
		unzip(zip, null);
	}
	
	
	/**
	 * Applies zip compression to a directory and all its contents
	 * 
	 * @param dir
	 * 		The directory to compress
	 * @param zipFile
	 * 		The file to create the zip archive in
	 * @throws IOException
	 */
	public static void zipDirectory(File dir, File zipFile) throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		List<File> files = Utils.recursiveListFiles(dir, new FileFilter() {
			public boolean accept(File name) {
				return true;
			}
		});
		int baseDirNameLength = dir.getAbsolutePath().length();
		Iterator<File> fileIter = files.iterator();
		while (fileIter.hasNext()) {
			File fileToAdd = fileIter.next();
			String relativeFileName = fileToAdd.getAbsolutePath().substring(baseDirNameLength + 1);
			ZipEntry entry = new ZipEntry(relativeFileName);
			zipOut.putNextEntry(entry);
			if (!fileToAdd.isDirectory()) {
				BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(fileToAdd));
				copyStreams(fileInput, zipOut);
				fileInput.close();
			}
		}
		zipOut.flush();
		zipOut.close();
	}
	
	
	/**
	 * Extracts the contents of a zip file entry to a byte array
	 * 
	 * @param zipFile
	 * 		The zip file
	 * @param entryName
	 * 		The name of the entry to extract
	 * @return
	 * 		A byte array containing the (uncompressed) contents of the entry
	 * @throws IOException
	 */
	public static byte[] extractEntryContents(File zipFile, String entryName) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ZipFile zip = new ZipFile(zipFile);
		ZipEntry entry = zip.getEntry(entryName);
		InputStream stream = zip.getInputStream(entry);
		copyStreams(stream, output);
		stream.close();
		output.flush();
		output.close();
		return output.toByteArray();
	}
    
    
    /**
     * Inserts an entry in a Zip file.  If an entry with the given name is found,
     * it will be replaced with the new one.
     * 
     * @param zipFile
     *      The zip file to be appended
     * @param entryName
     *      The name of the new entry
     * @param data
     *      The data to add to the zip
     * @throws IOException
     */
    public static void insertEntry(File zipFile, String entryName, byte[] data) throws IOException {
        // create a temp file
        File tempZip = File.createTempFile(zipFile.getName(), "tmp");
        tempZip.delete();
        FileOutputStream tempOut = new FileOutputStream(tempZip);
        ZipOutputStream zipOut = new ZipOutputStream(tempOut);
        
        try {
            // start streaming the input stream over to the temp
            ZipFile zipIn = new ZipFile(zipFile);
            Enumeration<? extends ZipEntry> entries = zipIn.entries();
            while (entries.hasMoreElements()) {
                ZipEntry inputEntry = entries.nextElement();
                if (!inputEntry.getName().equals(entryName)) {
                    InputStream entryStream = zipIn.getInputStream(inputEntry);
                    zipOut.putNextEntry(inputEntry);
                    copyStreams(entryStream, zipOut);
                    zipOut.closeEntry();
                }
            }

            // create new entry
            ZipEntry insert = new ZipEntry(entryName);
            zipOut.putNextEntry(insert);
            copyStreams(new ByteArrayInputStream(data), zipOut);
            zipOut.closeEntry();
            zipOut.close();

            zipIn.close();
            zipFile.delete();

            Utils.copyFile(tempZip, zipFile);
        } finally {
            // should delete the temp file regardless of what happens 
            // in the try block, and just let any exceptions percolate
            // up the stack
            tempZip.delete();
        }
    }
	
	
	/**
	 * Copies the contents of an input stream into an output stream
	 * 
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	private static void copyStreams(InputStream input, OutputStream output) throws IOException {
		byte[] temp = new byte[8192];
		int read = -1;
		while ((read = input.read(temp)) != -1) {
			output.write(temp, 0, read);
		}
	}
}
