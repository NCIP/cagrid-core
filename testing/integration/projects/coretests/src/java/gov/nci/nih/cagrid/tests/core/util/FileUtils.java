/*
 * Created on May 16, 2005
 */
package gov.nci.nih.cagrid.tests.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author MCCON012
 */
public class FileUtils
{
	/**
	 * Only static methods
	 */
	private FileUtils() { }
	
	public static File[] listRecursively(File file, FileFilter filter)
	{
		if (! file.isDirectory()) {
			if (filter == null || filter.accept(file)) return new File[] { file };
			else return new File[0];
		}
		
		ArrayList<File> fileList = new ArrayList<File>();
		File[] files = file.listFiles();
		for (File f : files) {
			File[] fs = listRecursively(f, filter);
			Collections.addAll(fileList, fs);
		}
		
		return fileList.toArray(new File[0]);
	}
	
	public static String readText(File file) 
		throws IOException
	{
		StringBuffer buf = new StringBuffer((int) file.length());
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			char[] ch = new char[1024];
			int count = 0;
			while ((count = br.read(ch)) != -1) {
				buf.append(ch, 0, count);
			}			
		} finally {
			br.close();
		}
		
		return buf.toString();
	}
	
	public static void deleteRecursive(File file)
	{
		if (file.isFile()) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			for (int i = 0; files != null && i < files.length; i++) {
				deleteRecursive(files[i]);
			}
			file.delete();
		}
	}

	public static void copyRecursive(File in, File out)
		throws IOException
	{
		copyRecursive(in, out, null);
	}

	public static void copyRecursive(File in, File out, FileFilter filter)
		throws IOException
	{
		if (filter != null && ! filter.accept(in)) return;
		
		if (in.isDirectory()) {
			if (! out.exists()) out.mkdir();
			File[] files = in.listFiles();
			for (int i = 0; i < files.length; i++) {
				copyRecursive(files[i], new File(out, files[i].getName()), filter);
			}
		} else if (in.isFile()) {
			copy(in, out);
		}
	}

	public static void copy(File in, File out)
		throws IOException
	{
		copy(in, new File[] { out });
	}

	public static void copy(File in, File[] out)
		throws IOException
	{
		// open
		BufferedOutputStream[] os = new BufferedOutputStream[out.length];
		for (int i = 0; i < os.length; i++) {
			if (out[i].isDirectory()) out[i] = new File(out[i], in.getName());
			os[i] = new BufferedOutputStream(new FileOutputStream(out[i]));
		}
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(in));
		
		// copy
		byte[] buf = new byte[10240];
		int count = 0;
		while ((count = is.read(buf)) != -1) {
			for (int i = 0; i < os.length; i++) os[i].write(buf, 0, count);
		}
		
		// close
		for (int i = 0; i < os.length; i++) os[i].close();
	}

	public static void copyContents(File in, File out) 
		throws IOException
	{
		copyContents(in, out, null);
	}

	public static void copyContents(File in, File outDir, FileFilter filter) 
		throws IOException
	{
		if (filter != null && ! filter.accept(in)) return;

		if (in.isFile()) {
			copy(in, new File(outDir, in.getName())); 
		} else if (in.isDirectory()) {
			in.mkdir();
			
			File[] files = in.listFiles();
			for (int i = 0; i < files.length; i++) {
				copyContents(files[i], outDir, filter);
			}
		}
	}
	
	public static File makeFileRelative(File file, File cwd)
	{
		String path = file.toString();
		int count = 0;
		while (! path.startsWith(cwd.toString())) {
			count++;
			cwd = cwd.getParentFile();
		}
		
		path = "";
		for (int i = 0; i < count; i++) {
			path += ".." + File.separatorChar;
		}
		file = new File(file.toString().substring(cwd.toString().length()));
		return new File(path + file.toString());
	}

	public static void delete(File file)
	{
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) delete(files[i]);
		}
		file.delete();
	}
	
	public static File createTempDir(String prefix, String suffix) 
		throws IOException
	{
		File temp = File.createTempFile(prefix, suffix);
		temp.delete();
		temp.mkdir();
		return temp;
	}
	
	public static File createTempDir(String prefix, String suffix, File dir) 
		throws IOException
	{
		if (dir == null) return createTempDir(prefix, suffix);
		File temp = File.createTempFile(prefix, suffix, dir);
		temp.delete();
		temp.mkdir();
		return temp;
	}

	public static void replace(File file, String search, String replace) 
		throws IOException
	{
		File tmpFile = File.createTempFile("FileUtils.replace", ".txt");
		tmpFile.deleteOnExit();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tmpFile)));
		
		String line = null;
		int index = -1;
		while ((line = br.readLine()) != null) {
			if ((index = line.indexOf(search)) != -1) {
				line = line.substring(0, index) + replace + line.substring(index+search.length());
			}
			out.println(line);
		}
		
		out.flush();
		out.close();
		br.close();
		
		FileUtils.copy(tmpFile, file);
		
		tmpFile.delete();		
	}
}
