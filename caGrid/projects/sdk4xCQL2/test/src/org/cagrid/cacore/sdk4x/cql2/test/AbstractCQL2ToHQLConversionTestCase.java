package org.cagrid.cacore.sdk4x.cql2.test;

import gov.nih.nci.cagrid.cql2.components.CQLQuery;

import org.cagrid.cacore.sdk4x.cql2.processor.ParameterizedHqlQuery;

public abstract class AbstractCQL2ToHQLConversionTestCase extends AbstractCQL2ExamplesTestCase {
    
    public AbstractCQL2ToHQLConversionTestCase(String name) {
        super(name);
    }
    
    
    protected void testQuery(String filename) {
        translateQuery(filename);
    }
    
    
    protected void translateQuery(String filename) {
        CQLQuery query = loadQuery(filename);
        ParameterizedHqlQuery hql = null;
        try {
            hql = cqlTranslator.convertToHql(query);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error converting CQL2 to HQL: " + ex.getMessage());
        }
        System.out.println("Converted CQL2 (" + filename + ") to HQL");
        System.out.println("\t" + hql);
    }
}
