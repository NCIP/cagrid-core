/*
 * Created on Aug 30, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.util.BeanFileUtil;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class BeanUtilTest
	extends TestCase
{
	private File outFile1;
	private File outFile2;
	private File caGridProjectsDir;
	
	public BeanUtilTest(String name)
	{
		super(name);

		outFile1 = new File("test", "resources" + File.separator + "BeanUtilTest" + File.separator + "test1.xml");
		outFile2 = new File("test", "resources" + File.separator + "BeanUtilTest" + File.separator + "test2.xml");
		caGridProjectsDir = new File(".." + File.separator + ".." + File.separator + ".." + File.separator + "caGrid" + File.separator + "projects");	
	}
	
	public void tearDown()
	{
		outFile1.delete();
		outFile2.delete();
	}
	
	private void performTest(File serviceDir, String clName, int depth, String qname) 
		throws Exception
	{
		BeanFileUtil bean = new BeanFileUtil(serviceDir, clName);
		bean.writeBean(depth, qname, outFile1);
		assertTrue(outFile1.exists());
		assertNotNull(bean.readBean(outFile1));
		assertTrue(outFile1.delete());
		
		BeanFileUtil.main(new String[] {
			"-dir", serviceDir.toString(),
			"-class", clName,
			"-depth", String.valueOf(depth),
			"-qname", qname,
			"-out", outFile2.toString(),
		});
		assertTrue(outFile2.exists());
		assertNotNull(bean.readBean(outFile2));
	}
	
	public void testDorianApplication() 
		throws Exception
	{
		performTest(
			new File(caGridProjectsDir, "dorian"),
			"gov.nih.nci.cagrid.dorian.idp.bean.Application",
			2,
			"application"
		);
	}
	
	public void testGridGrouperGroupDescription() 
		throws Exception
	{
		performTest(
			new File(caGridProjectsDir, "gridgrouper"),
			"gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor",
			2,
			"groupDescription"
		);
	}
	
	public static void main(String[] args) throws Exception
	{
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(BeanUtilTest.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
