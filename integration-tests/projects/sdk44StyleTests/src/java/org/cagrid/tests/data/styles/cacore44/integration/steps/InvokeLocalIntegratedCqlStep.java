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
package org.cagrid.tests.data.styles.cacore44.integration.steps;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.util.List;

public class InvokeLocalIntegratedCqlStep extends AbstractLocalCqlInvocationStep {
    
    public InvokeLocalIntegratedCqlStep() {
        super();
    }


    protected List<?> executeQuery(CQLQuery query) throws Exception {
        return getService().query(query);
    }
}
