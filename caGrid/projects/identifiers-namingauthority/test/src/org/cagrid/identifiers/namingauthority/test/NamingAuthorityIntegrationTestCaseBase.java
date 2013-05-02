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
