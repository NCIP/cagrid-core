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
