package gov.nih.nci.cagrid.sdkquery4.test;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  SDKQuery4CycleTestCase
 *  Tests for cycles in SDKQuery4
 * 
 * @author David Ervin
 * 
 * @created Oct 2, 2007 12:49:29 PM
 * @version $Id: SDKQuery4CycleTestCase.java,v 1.1 2007-10-02 16:50:46 dervin Exp $ 
 */
public class SDKQuery4CycleTestCase extends CycleTestCase {

    public SDKQuery4CycleTestCase(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDKQuery4CycleTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
