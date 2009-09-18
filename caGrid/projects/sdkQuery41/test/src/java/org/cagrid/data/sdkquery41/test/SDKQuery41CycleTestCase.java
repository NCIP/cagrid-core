package org.cagrid.data.sdkquery41.test;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  SDKQuery41CycleTestCase
 *  Tests for cycles in the SDKQuery41 project
 * 
 * @author David Ervin
 * 
 * @created Oct 2, 2007 12:49:29 PM
 * @version $Id: SDKQuery41CycleTestCase.java,v 1.1 2008-11-18 21:11:41 dervin Exp $ 
 */
public class SDKQuery41CycleTestCase extends CycleTestCase {

    public SDKQuery41CycleTestCase(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDKQuery41CycleTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
