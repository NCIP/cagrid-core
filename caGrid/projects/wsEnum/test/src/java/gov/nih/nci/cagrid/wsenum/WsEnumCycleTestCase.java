package gov.nih.nci.cagrid.wsenum;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  WsEnumCycleTestCase
 *  Test case for cycles in the ws-enum project
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 17, 2006 
 * @version $Id$ 
 */
public class WsEnumCycleTestCase extends CycleTestCase {
	
	public WsEnumCycleTestCase(String name) {
		super(name);
	}
	

	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(WsEnumCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
