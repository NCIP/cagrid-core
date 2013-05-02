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
package org.cagrid.cacore.sdk4x.cql2.test.sdk40;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cacore.sdk4x.cql2.test.AbstractCQL2toHQLQueryTestCase;

public class SDK40CQL2QueryTestCase extends AbstractCQL2toHQLQueryTestCase {
    
    public static final String DOMAIN_MODEL_LOCATION = "test/docs/models/sdk40example_DomainModel.xml";

    public SDK40CQL2QueryTestCase(String name) {
        super(name);
    }


    protected String getDomainModelFilename() {
        return DOMAIN_MODEL_LOCATION;
    }


    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDK40CQL2QueryTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
