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
