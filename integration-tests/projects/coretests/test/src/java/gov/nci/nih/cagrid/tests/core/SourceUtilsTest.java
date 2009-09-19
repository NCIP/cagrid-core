/*
 * Created on Jun 8, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.util.SourceUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This is a unit test that tests the functionality of the SourceUtils class, 
 * which is used to modify Java source code programmatically.
 * @testType unit
 * @author Patrick McConnell
 */
public class SourceUtilsTest
	extends TestCase
{
	public SourceUtilsTest(String name)
	{
		super(name);
	}
	
//	public void testReplaceMethodBodyWithJAXME() 
//		throws IOException, RecognitionException, TokenStreamException
//	{
//		File inFile = new File(
//			"test" + File.separator + "resources" + File.separator + "SourceUtilsTest" + File.separator + "Test.java"
//		);
//		File outFile = new File(
//			"test" + File.separator + "resources" + File.separator + "SourceUtilsTest" + File.separator + "generated" + File.separator + "Test.java"
//		);
//		
//		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
//		out.println("public class Test\r\n" + 
//				"	public void helloWorld()\r\n" + 
//				"		throws Exception\r\n" + 
//				"	{\r\n" + 
//				"		System.out.println(\"test world\");\r\n" + 
//				"		System.out.println(\"test world again\");\r\n" + 
//				"	}\r\n" + 
//				"}"
//		);
//		out.flush();
//		out.close();
//		
//		SourceUtils.replaceMethodBodyWithJAXME(inFile, "helloWorld", outFile);
//		
//		assertEquals(0, countInFile(outFile, "hello world"));
//		assertEquals(2, countInFile(outFile, "test world"));
//		assertEquals(1, countInFile(outFile, "main"));
//	}
	
	/**
	 * Tests the modifyImpl method by adding some functionality to Java source and then 
	 * testing that the mods actually made it in.
	 */
	public void testModifyImpl() 
		throws IOException
	{
		File inFile = new File(
			"test" + File.separator + "resources" + File.separator + "SourceUtilsTest" + File.separator + "Test.java"
		);
		File outFile = new File(
			"test" + File.separator + "resources" + File.separator + "SourceUtilsTest" + File.separator + "generated" + File.separator + "Test.java"
		);
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
		out.println("public class Test {\r\n" + 
				"	public void myOtherMethod() {\r\n" + 
				"		System.out.println(\"my other method\");\r\n" + 
				"		helloWorld();\r\n" +
				"	}\r\n" + 
				"	public void helloWorld()\r\n" + 
				"		throws Exception\r\n" + 
				"	{\r\n" + 
				"		for (int i = 0; i < 10; i++) {" +
				"			System.out.println(\"test world\");\r\n" + 
				"			System.out.println(\"test world again\");\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"}"
		);
		out.flush();
		out.close();
		
		SourceUtils.modifyImpl(inFile, outFile, "helloWorld");
		
		assertEquals(2, countInFile(outFile, "hello world"));
		assertEquals(2, countInFile(outFile, "helloWorld"));
		assertEquals(0, countInFile(outFile, "test world"));
		assertEquals(1, countInFile(outFile, "12"));
		assertEquals(0, countInFile(outFile, "10"));
		assertEquals(1, countInFile(outFile, "myOtherMethod"));
	}
	
	private int countInFile(File file, String s) 
		throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		int count = 0;
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.indexOf(s) != -1) count++;
		}
		br.close();
		return count;
	}
	
	public static void main(String[] args) throws Exception
	{
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(SourceUtilsTest.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
