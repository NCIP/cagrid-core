package org.cagrid.cacore.sdk4x.cql2.test;

import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cacore.sdk4x.cql2.processor.ParameterizedHqlQuery;


public abstract class AbstractCQL2toHQLQueryTestCase extends AbstractCQL2ExamplesTestCase {
    
    public static final String APPSERVICE_URL_PROPERTY = "sdk.application.url";
    public static final Log LOG = LogFactory.getLog(AbstractCQL2toHQLQueryTestCase.class);
    
    protected ApplicationService appservice = null;
    
    public AbstractCQL2toHQLQueryTestCase(String name) {
        super(name);
    }
    
    
    protected String getSdkApplicationUrl() {
        return System.getProperty(APPSERVICE_URL_PROPERTY);
    }
    
    
    public void setUp() {
        super.setUp();
        String url = getSdkApplicationUrl();
        try {
            appservice = ApplicationServiceProvider.getApplicationServiceFromUrl(url);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating application service instance: " + ex.getMessage());
        }
    }
    
    
    protected void testQuery(String filename) {
        CQLQuery query = loadQuery(filename);
        ParameterizedHqlQuery parameterizedHQL = null;
        try {
            parameterizedHQL = cqlTranslator.convertToHql(query);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error translating CQL to HQL: " + ex.getMessage());
        }
        LOG.debug("Executing HQL: " + parameterizedHQL.toString());
        HQLCriteria criteria = new HQLCriteria(parameterizedHQL.getHql(), parameterizedHQL.getParameters());
        List<?> results = null;
        try {
            results = appservice.query(criteria);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error queying application service: " + ex.getMessage());
        }
        assertNotNull(results);
        // TODO: validate results
    }
}
