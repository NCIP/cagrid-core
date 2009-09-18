package org.cagrid.gme.test;

import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;


public abstract class GMEIntegrationTestCaseBase extends AbstractAnnotationAwareTransactionalTests {

    public GMEIntegrationTestCaseBase() {
        setPopulateProtectedVariables(true);
    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{SpringTestApplicationContextConstants.GME_BASE_LOCATION,
                SpringTestApplicationContextConstants.TEST_BASE_LOCATION};
    }
}
