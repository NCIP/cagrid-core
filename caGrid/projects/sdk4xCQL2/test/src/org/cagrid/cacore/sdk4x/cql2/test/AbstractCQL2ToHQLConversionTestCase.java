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
package org.cagrid.cacore.sdk4x.cql2.test;

import org.cagrid.cacore.sdk4x.cql2.processor.ParameterizedHqlQuery;
import org.cagrid.cql2.CQLQuery;

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
