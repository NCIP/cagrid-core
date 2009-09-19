/**
 * Created on Feb 14, 2005 by MCCON012
 */
package gov.nci.nih.cagrid.tests.core.compare;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Base class for implementing comparators.  You should implement 
 * isEqual(InputStream[]) and you get a bunch of the other isEqual methods for
 * free.
 * 
 * @author MCCON012
 */
public abstract class AbstractComparator 
{
	/**
	 * Perform a comma-delimited comparison - csv
	 */
	public static final String CSV_TYPE = "csv";
	/**
	 * Perform an XML comparison - xml
	 */
	public static final String XML_TYPE = "xml";
	/**
	 * Perform a character-by-character comparison - character
	 */
	public static final String CHARACTER_TYPE = "character";
	
	/**
	 * Compare content in the InputStreams.  Implement this method to provide
	 * different comparisons for different types of content.
	 * 
	 * @param is The content to compare
	 * @return Whether they are equal
	 * @throws Exception
	 */
	public abstract boolean isEqual(InputStream[] content)
		throws Exception;

	/**
	 * Compare whether the content in two files is equal
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public boolean isEqual(File[] files)
		throws Exception
	{
		BufferedInputStream[] is = new BufferedInputStream[files.length];
		try {
			for (int i = 0; i < files.length; i++) {
				is[i] = new BufferedInputStream(new FileInputStream(files[i]));
			}
			return isEqual(is);
		} catch (Exception e) {
			throw e;
		} finally {
			for (int i = 0; i < is.length; i++) {
				if (is[i] != null) is[i].close();
			}
		}
	}

	/**
	 * Compare whether the content in the Strings is equal
	 * 
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public boolean isEqual(String[] values)
		throws Exception
	{
		ByteArrayInputStream[] is = new ByteArrayInputStream[values.length];
		try {
			for (int i = 0; i < values.length; i++) {
				is[i] = new ByteArrayInputStream(values[i].getBytes());
			}
			return isEqual(is);
		} catch (Exception e) {
			throw e;
		} finally {
			for (int i = 0; i < is.length; i++) {
				if (is[i] != null) is[i].close();
			}
		}
	}

	/**
	 * Determine whether a set of integers are all equal to each other\
	 * 
	 * @param values
	 * @return
	 */
	public static boolean valuesEqual(int[] values)
	{
		for (int i = 0; i < values.length; i++) {
			for (int j = i+1; j < values.length; j++) {
				if (values[i] != values[j]) return false;
			}
		}
		return true;
	}
	
	/**
	 * Determine whether a set of Strings are equal to each other
	 * 
	 * @param values
	 * @return
	 */
	public static boolean valuesEqual(String[] values)
	{
		for (int i = 0; i < values.length; i++) {
			for (int j = i+1; j < values.length; j++) {
				if (! values[i].equals(values[j])) return false;
			}
		}
		return true;
	}
	
	/**
	 * Determine whether a set of Objects are equal to each other
	 * 
	 * @param values
	 * @return
	 */
	public static boolean valuesEqual(Object[] values)
	{
		for (int i = 0; i < values.length; i++) {
			for (int j = i+1; j < values.length; j++) {
				if (! values[i].equals(values[j])) return false;
			}
		}
		return true;
	}
}