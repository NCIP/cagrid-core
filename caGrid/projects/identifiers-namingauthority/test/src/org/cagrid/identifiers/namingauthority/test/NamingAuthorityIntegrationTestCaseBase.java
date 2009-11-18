package org.cagrid.identifiers.namingauthority.test;

import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;


public abstract class NamingAuthorityIntegrationTestCaseBase extends AbstractAnnotationAwareTransactionalTests {

    public NamingAuthorityIntegrationTestCaseBase() {
        setPopulateProtectedVariables(true);
    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{SpringTestApplicationContextConstants.NA_BASE_LOCATION,
                SpringTestApplicationContextConstants.TEST_BASE_LOCATION};
    }
}
