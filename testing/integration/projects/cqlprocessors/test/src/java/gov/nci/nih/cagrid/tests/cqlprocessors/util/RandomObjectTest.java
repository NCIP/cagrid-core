/*
 * Created on Aug 1, 2006
 */
package gov.nci.nih.cagrid.tests.cqlprocessors.util;

import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This unit test validates the functionality of RandomObject, which creates objects with random content
 * @testType unit
 * @author Patrick McConnell
 */
public class RandomObjectTest
	extends TestCase
{
	public RandomObjectTest(String name)
	{
		super(name);
	}
	
	/**
	 * This tests whether a RandomObject can generate an int correctly
	 */
	public void testInt() throws Exception
	{
		Object obj = new RandomObject().next(int.class);
		assertTrue(obj instanceof Integer);
	}
	
	/**
	 * This tests whether a RandomObject can generate a Double correctly
	 */
	public void testDoubleObject() throws Exception
	{
		Object obj = new RandomObject().next(Double.class);
		assertTrue(obj instanceof Double);
	}
	
	/**
	 * This tests whether a RandomObject can generate a String correctly
	 */
	public void testString() throws Exception
	{
		Object obj = new RandomObject().next(String.class);
		assertTrue(obj instanceof String);
		assertTrue(((String) obj).length() >= 10);
		assertTrue(((String) obj).length() < 20);
		for (char c : ((String) obj).toCharArray()) {
			assertTrue(Character.isLetterOrDigit(c));
		}
	}
	
	/**
	 * This tests whether a RandomObject can generate an Object with subobjects correctly
	 */
	public void testObject() throws Exception
	{
		Object obj = new RandomObject().next(TestClass.class, 2);
		assertTrue(obj instanceof TestClass);
		assertNotNull(((TestClass) obj).b);
		assertNotNull(((TestClass) obj).i);
		assertNotNull(((TestClass) obj).o);
		assertNotNull(((TestClass) obj).o.b);
		assertNotNull(((TestClass) obj).o.o);
		assertNotNull(((TestClass) obj).o.o.b);
		assertNull(((TestClass) obj).o.o.o);
	}
	
	/**
	 * This tests whether a RandomObject can generate an Object with an array of Objects correctly
	 */
	public void testArray() throws Exception
	{
		Object obj = new RandomObject().next(TestClass2.class, 2);
		assertTrue(obj instanceof TestClass2);
		
		assertNotNull(((TestClass2) obj).b);
		assertNotNull(((TestClass2) obj).i);
		assertNotNull(((TestClass2) obj).o);
		assertNotNull(((TestClass2) obj).o.b);
		assertNotNull(((TestClass2) obj).a);
		assertNotNull(((TestClass2) obj).a[0][0]);
		assertNotNull(((TestClass2) obj).a[0][0].o);		
		assertNotNull(((TestClass2) obj).o.o);
		assertNotNull(((TestClass2) obj).o.o.b);
		assertNull(((TestClass2) obj).o.o.o);
	}
	
	/**
	 * This tests whether a RandomObject can generate an Object with a Collection correctly
	 */
	public void testCollection() throws Exception
	{
		Object obj = new RandomObject().next(TestClass3.class, 2);
		assertTrue(obj instanceof TestClass3);
		assertNotNull(((TestClass3) obj).b);
		assertNotNull(((TestClass3) obj).i);
		assertNotNull(((TestClass3) obj).o);
		assertNotNull(((TestClass3) obj).o.b);
		assertNotNull(((TestClass3) obj).o.o);
		assertNotNull(((TestClass3) obj).o.o.b);
		assertNull(((TestClass3) obj).o.o.o);
		assertNull(((TestClass3) obj).a);
	}
	
	/**
	 * This tests whether a RandomObject can generate an Object with a constructor correctly
	 */
	public void testConstructor() throws Exception
	{
		Object obj = new RandomObject().next(TestClass4.class, 2);
		assertTrue(obj instanceof TestClass4);
		assertNotNull(((TestClass4) obj).s);
	}
	
	/**
	 * This tests whether a RandomObject can generate an Object with a public static field correctly
	 */
	public void testPublicStaticField() throws Exception
	{
		Object obj = new RandomObject().next(TestClass5.class, 2);
		assertTrue(obj instanceof TestClass5);
		assertNotNull(((TestClass5) obj).s);
	}
	
	public static class TestClass
	{
		private Boolean b;
		private Integer i;
		private TestClass o;
		
		public void setB(Boolean b) { this.b = b; }
		public void setI(Integer i) { this.i = i; }
		public void setO(TestClass o) { this.o = o; }
	}
	
	public static class TestClass2
	{
		private Boolean b;
		private Integer i;
		private TestClass2 o;
		private TestClass2[][] a;
		
		public void setB(Boolean b) { this.b = b; }
		public void setI(Integer i) { this.i = i; }
		public void setO(TestClass2 o) { this.o = o; }
		public void setA(TestClass2[][] a) { this.a = a; }
	}
	
	public static class TestClass3
	{
		private Boolean b;
		private Integer i;
		private TestClass o;
		private ArrayList a;
		
		public void setB(Boolean b) { this.b = b; }
		public void setI(Integer i) { this.i = i; }
		public void setO(TestClass o) { this.o = o; }
		public void setA(ArrayList a) { this.a = a; }
	}
	
	public static class TestClass4
	{
		private String s;
		private Boolean b;
		private Integer i;
		private TestClass4 o;
		
		public TestClass4(String s) { this.s = s; }
		public void setB(Boolean b) { this.b = b; }
		public void setI(Integer i) { this.i = i; }
		public void setO(TestClass4 o) { this.o = o; }
	}
	
	public static class TestClass5
	{
		public static TestClass5 instance = new TestClass5("test"); 
		private String s;
		
		private TestClass5(String s) { this.s = s; }		
	}
	
	public static void main(String[] args) throws Exception
	{
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(RandomObjectTest.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
