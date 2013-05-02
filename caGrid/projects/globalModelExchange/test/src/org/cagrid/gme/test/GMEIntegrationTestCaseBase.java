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
