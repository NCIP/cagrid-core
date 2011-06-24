package org.cagrid.cql.test;

import java.util.NoSuchElementException;

import org.cagrid.cql.utilities.iterator.CQL2QueryResultsIterator;
import org.cagrid.cql2.results.CQLQueryResults;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class CQL2QueryResultsIteratorTestCase extends TestCase {

    public CQL2QueryResultsIteratorTestCase(String name) {
        super(name);
    }
    
    
    public void testIterateNothing() {
        CQLQueryResults results = new CQLQueryResults();
        CQL2QueryResultsIterator iter = new CQL2QueryResultsIterator(results);
        assertFalse("Iterator should not have had any items to iterate!", iter.hasNext());
        try {
            iter.next();
            fail("Iterator should have thrown an exception on next since there are no objects to iterate");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type (" + ex.getClass().getName() + ") thrown", 
                ex instanceof NoSuchElementException);
        }
    }
    
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CQL2QueryResultsIteratorTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
