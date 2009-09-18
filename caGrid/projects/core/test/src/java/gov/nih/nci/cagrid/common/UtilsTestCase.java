package gov.nih.nci.cagrid.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;


public class UtilsTestCase extends TestCase {

	String a1 = "a";
	String a2 = "a";
	String b = "b";
	String aws = "a  ";
	String ws = "  ";


	public void testClean() {
		assertEquals(a1, Utils.clean(a1));
		assertEquals(aws, Utils.clean(aws));
		assertNull(Utils.clean(ws));
		assertNull(Utils.clean(new String()));
		assertNull(Utils.clean(null));
	}


	public void testEqualsObjectObject() {
		assertTrue(Utils.equals(null, null));
		assertTrue(Utils.equals(a1, a1));
		assertTrue(Utils.equals(a1, a2));
		assertTrue(Utils.equals(a2, a1));

		assertFalse(Utils.equals(a1, b));
		assertFalse(Utils.equals(b, a1));
		assertFalse(Utils.equals(null, a1));
		assertFalse(Utils.equals(a1, null));
	}


	public void testConcatenateArrays() {
		String arr1[] = new String[]{"0", "1", "2"};
		String arr2[] = new String[]{"3", "4", "5"};
		String gold[] = new String[]{"0", "1", "2", "3", "4", "5"};

		assertNull(Utils.concatenateArrays(String.class, null, null));
		assertEquals(arr2, Utils.concatenateArrays(String.class, arr2, null));
		assertEquals(arr2, Utils.concatenateArrays(String.class, null, arr2));
		assertTrue(Arrays.deepEquals(gold, (String[]) Utils.concatenateArrays(String.class, arr1, arr2)));
		assertFalse(Arrays.deepEquals(gold, (String[]) Utils.concatenateArrays(String.class, arr2, arr1)));
	}
	
	
	public void testArrayAppend() {
		String[] arr1 = new String[]{"0", "1", "2"};
		String[] gold = new String[]{"0", "1", "2", "3"};
		assertTrue(Arrays.deepEquals(gold, (String[]) Utils.appendToArray(arr1, "3")));
	}
	
	
	public void testArrayRemove() {
		String[] arr1 = new String[]{"0", "1", "2"};
		String[] gold = new String[]{"0", "2"};
		assertTrue(Arrays.deepEquals(gold, (String[]) Utils.removeFromArray(arr1, "1")));
	}
	
	
	public void testArrayTrimEnd() {
	    String[] arr1 = new String[] {"0", "1", "2"};
	    String[] gold1 = new String[] {"0", "1"};
	    String[] gold2 = new String[] {"0"};
	    
	    String[] trim1 = (String[]) Utils.trimArray(arr1, 0, arr1.length - 1);
	    String[] trim2 = (String[]) Utils.trimArray(arr1, 0, arr1.length - 2);
	    
	    assertTrue(Arrays.deepEquals(gold1, trim1));
	    assertTrue(Arrays.deepEquals(gold2, trim2));
	}
	
	
	public void testArrayTrimStart() {
	    String[] arr1 = new String[] {"0", "1", "2"};
        String[] gold1 = new String[] {"1", "2"};
        String[] gold2 = new String[] {"2"};
        
        String[] trim1 = (String[]) Utils.trimArray(arr1, 1, arr1.length);
        String[] trim2 = (String[]) Utils.trimArray(arr1, 2, arr1.length);
        
        assertTrue(Arrays.deepEquals(gold1, trim1));
        assertTrue(Arrays.deepEquals(gold2, trim2));
	}
	
	
	public void testArrayTrimBoth() {
	    String[] arr1 = new String[] {"0", "1", "2"};
        String[] gold1 = new String[] {"1"};
        
        String[] trim1 = (String[]) Utils.trimArray(arr1, 1, arr1.length - 1);
        
        assertTrue(Arrays.deepEquals(gold1, trim1));
	}
	
	
	public void testReadFile() {
		String garbageText = generateGarbageText();
		try {
			File f1 = File.createTempFile("test", "garbage");
			FileWriter writer = new FileWriter(f1);
			writer.write(garbageText);
			writer.close();
			assertTrue("Test file exists", f1.exists());
			String f1Text = Utils.fileToStringBuffer(f1).toString().trim();
			assertTrue("Test file contains test text", garbageText.equals(f1Text));
			f1.delete();
		} catch (IOException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	
	public void testCopyFile() {
		String garbageText = generateGarbageText();
		try {
			File f1 = File.createTempFile("test", "garbage");
			FileWriter writer = new FileWriter(f1);
			writer.write(garbageText);
			writer.close();
			assertTrue("Test file exists", f1.exists());
			String f1Text = Utils.fileToStringBuffer(f1).toString().trim();
			assertTrue("Test file contains test text", garbageText.equals(f1Text));
			File f2 = File.createTempFile("test", "garbage");
			Utils.copyFile(f1, f2);
			String f2Text = Utils.fileToStringBuffer(f2).toString().trim();
			assertTrue("Copied file contents match original", garbageText.equals(f2Text));
			f1.delete();
			f2.delete();
		} catch (IOException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	
	public void testCopyToSelf() {
		String garbageText = generateGarbageText();
		try {
			File f1 = File.createTempFile("test", "garbage");
			FileWriter writer = new FileWriter(f1);
			writer.write(garbageText);
			writer.close();
			assertTrue("Test file exists", f1.exists());
			String f1Text = Utils.fileToStringBuffer(f1).toString().trim();
			assertTrue("Test file contains test text", garbageText.equals(f1Text));
			File f2 = new File(f1.getAbsolutePath());
			Utils.copyFile(f1, f2);
			String f2Text = Utils.fileToStringBuffer(f2).toString().trim();
			assertTrue("Copied file contents match original", garbageText.equals(f2Text));
			f1.delete();
			f2.delete();
		} catch (IOException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

	
	private String generateGarbageText() {
		int start = 'a';
		StringBuilder buff = new StringBuilder();
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < 1024; i++) {
			char c = (char) (start + rand.nextInt(26));
			buff.append(c);
		}
		return buff.toString();
	}
}
