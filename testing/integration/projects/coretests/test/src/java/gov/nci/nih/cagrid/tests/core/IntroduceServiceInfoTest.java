/*
 * Created on Jun 11, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.util.IntroduceServiceInfo;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.xml.sax.SAXException;

/**
 * This is a unit test that validates the functionality of the IntroduceServiceInfo class, which is
 * used to pull some information out of an introduce.xml file.
 * @testType unit
 * @author Patrick McConnell
 */
public class IntroduceServiceInfoTest
	extends TestCase
{
	public IntroduceServiceInfoTest(String name)
	{
		super(name);
	}
	
	/**
	 * Loads an introduce.xml file and then checks that a number of fields are loaded properly.
	 */
	public void testServiceInfo() 
		throws ParserConfigurationException, SAXException, IOException
	{
		IntroduceServiceInfo info = new IntroduceServiceInfo(new File(System.getProperty(
			"IntroduceServiceInfoTest.file",
			"test" + File.separator + "resources" + File.separator + "IntroduceServiceInfoTest" + File.separator + "introduce.xml"
		)));
		
		assertEquals("http://tests.cagrid.nci.nih.gov/BasicAnalyticalServiceWithSecurity", info.getNamespace());
		assertEquals("gov.nih.nci.cagrid.tests", info.getPackageName());
		assertEquals("BasicAnalyticalServiceWithSecurity", info.getServiceName());
		String[] methodNames = info.getMethodNames();
		assertEquals(1, methodNames.length);
		assertEquals("reverseTranslate", methodNames[0]);
		assertEquals(true, info.isTransportSecurity());
	}
	
	public static void main(String[] args) throws Exception
	{
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(IntroduceServiceInfoTest.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
