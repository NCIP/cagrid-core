/*
 * Created on Jun 5, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.compare.BeanComparator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This is a unit test that validates the functionality of the BeanComparator class, which is used
 * to compare the values of two Java bean objects. 
 * @testType unit
 * @author Patrick McConnell
 */
public class BeanComaparatorTest
	extends TestCase
{
	public BeanComaparatorTest(String name)
	{
		super(name);
	}
	
	/**
	 * Tests the comparison of a simple bean that has a string value and an array list with
	 * a Boolean and a Float. 
	 */
	public void testFlat() 
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		TestBean b1 = new TestBean();
		TestBean b2 = new TestBean();
		
		b1.value1 = b2.value1 = "test";
		b1.list1 = b2.list1 = new ArrayList<Object>();
		b1.list1.add(Boolean.TRUE);
		b1.list1.add(new Float(12.12));
		
		new BeanComparator(this).assertEquals(b1, b2);
	}
	
	/**
	 * Test the comparison of a more complex bean that has some simple types, a user object
	 * in an array list, and an array of user objects.
	 */
	public void testEmbedded() 
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		TestBean b1 = new TestBean();
		TestBean b2 = new TestBean();
		
		b1.value1 = b2.value1 = null;
		b1.list1 = b2.list1 = new ArrayList<Object>();
		b1.list1.add(Boolean.TRUE);
		b1.list1.add(new Float(12.12));
		b1.list1.add(new TestSubBean("subbean0"));
		
		b1.array1 = new TestSubBean[] {
			new TestSubBean("subbean1"),
			new TestSubBean("subbean2"),
			new TestSubBean("subbean3"),
		};
		b2.array1 = new TestSubBean[] {
			new TestSubBean("subbean1"),
			new TestSubBean("subbean2"),
			new TestSubBean("subbean3"),
		};
		
		new BeanComparator(this).assertEquals(b1, b2);
	}
	
	public static class TestSubBean
	{
		public String value1;
		
		public TestSubBean(String value1) {
			super();
			this.value1 = value1;
		}
		
		public String getValue1() { return value1; }
	}
	
	public static class TestBean
	{
		public String value1;
		public ArrayList<Object> list1;
		public TestSubBean[] array1;
		
		public String getValue1() { return value1; }
		public ArrayList<Object> getList1() { return list1; }
		public TestSubBean[] getArray1() { return array1; }
	}
	
	public static void main(String[] args) throws Exception
	{
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(BeanComaparatorTest.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
