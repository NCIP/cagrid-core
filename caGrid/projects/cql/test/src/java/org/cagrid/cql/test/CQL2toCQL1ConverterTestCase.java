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
package org.cagrid.cql.test;

import org.cagrid.cql.utilities.CQL2toCQL1Converter;
import org.cagrid.cql.utilities.QueryConversionException;
import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.DistinctAttribute;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class CQL2toCQL1ConverterTestCase extends TestCase {
    
    public CQL2toCQL1ConverterTestCase(String name) {
        super(name);
    }
    
    
    public void testConvertUnsupportedAggregation() {
        CQLQuery query = new CQLQuery();
        CQLQueryModifier mods = new CQLQueryModifier();
        DistinctAttribute da = new DistinctAttribute();
        da.setAttributeName("name");
        da.setAggregation(Aggregation.MAX);
        mods.setDistinctAttribute(da);
        query.setCQLQueryModifier(mods);
        
        try {
            CQL2toCQL1Converter.convertToCql1Query(query);
            // shouldn't work
            fail("Conversion of aggregation " + Aggregation.MAX.getValue() + " to CQL 1 should have failed");
        } catch (QueryConversionException ex) {
            // expected
        } catch (Exception ex) {
            // not expected
            ex.printStackTrace();
            fail("Unexpected error converting CQL 2 to 1: " + ex.getMessage());
        }
    }
    
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CQL2toCQL1ConverterTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
