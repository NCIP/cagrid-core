/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
