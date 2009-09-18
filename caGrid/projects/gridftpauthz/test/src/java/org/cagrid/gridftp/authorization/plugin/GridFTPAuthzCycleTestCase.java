package org.cagrid.gridftp.authorization.plugin;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * GridFTPAuthzCycleTestCase
 * 
 * @author oster
 * @created Mar 21, 2007 12:07:08 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class GridFTPAuthzCycleTestCase extends CycleTestCase {
    public GridFTPAuthzCycleTestCase(String name) {
        super(name);
    }


    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(GridFTPAuthzCycleTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
