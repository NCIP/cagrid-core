package org.cagrid.cql.test;

import gov.nih.nci.cagrid.common.Utils;

import java.io.InputStream;
import java.io.StringReader;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql.utilities.iterator.CQL2QueryResultsIterator;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.TargetAttribute;


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
        try {
            iter.remove();
            fail("Should not have been able to remove from the iterator");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type (" + ex.getClass().getName() + ") thrown",
                ex instanceof UnsupportedOperationException);
        }
    }
    
    
    public void testIterateAttributes() {
        int resultsCount = 100;
        int targetAttributeCount = 2;
        CQLQueryResults results = generateAttributeResults(resultsCount, targetAttributeCount);
        CQL2QueryResultsIterator iter = new CQL2QueryResultsIterator(results);
        int foundCount = 0;
        assertTrue("Iterator should have had results, but hasNext() was false", iter.hasNext());
        while (iter.hasNext()) {
            foundCount++;
            Object item = iter.next();
            assertNotNull("Item returned from the iterator was null", item);
            assertEquals("Object returned from the iterator should have been a TargetAttribute[]", 
                TargetAttribute[].class, item.getClass());
            TargetAttribute[] tas = (TargetAttribute[]) item;
            assertEquals("Unexpected number of target attributes found", targetAttributeCount, tas.length);
        }
        assertEquals("Unexpected number of results returned from the iterator", resultsCount, foundCount);
        try {
            iter.remove();
            fail("Should not have been able to remove from the iterator");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type (" + ex.getClass().getName() + ") thrown",
                ex instanceof UnsupportedOperationException);
        }
    }
    
    
    public void testIterateAttributesXml() {
        int resultsCount = 100;
        int targetAttributeCount = 2;
        CQLQueryResults results = generateAttributeResults(resultsCount, targetAttributeCount);
        CQL2QueryResultsIterator iter = new CQL2QueryResultsIterator(results, true);
        int foundCount = 0;
        assertTrue("Iterator should have had results, but hasNext() was false", iter.hasNext());
        while (iter.hasNext()) {
            foundCount++;
            Object item = iter.next();
            assertNotNull("Item returned from the iterator was null", item);
            assertEquals("Object returned from the iterator should have been a String", 
                String.class, item.getClass());
            String xml = (String) item;
            InputStream wsdd = getClass().getResourceAsStream("/org/cagrid/cql2/mapping/client-config.wsdd");
            assertNotNull("Couldn't get the CQL 2 client config wsdd", wsdd);
            try {
                CQLAttributeResult attrib = Utils.deserializeObject(new StringReader(xml), CQLAttributeResult.class, wsdd);
                assertNotNull("Attribute result was null", attrib);
                TargetAttribute[] tas = attrib.getAttribute();
                assertNotNull("Target Attributes returned were null", tas);
                assertEquals("Unexpected number of target attributes found", targetAttributeCount, tas.length);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error deserializing attribute results: " + ex.getMessage());
            }
        }
        assertEquals("Unexpected number of results returned from the iterator", resultsCount, foundCount);
        try {
            iter.remove();
            fail("Should not have been able to remove from the iterator");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type (" + ex.getClass().getName() + ") thrown",
                ex instanceof UnsupportedOperationException);
        }
    }
    
    
    private CQLQueryResults generateAttributeResults(int resultsCount, int numTargetAttributes) {
        CQLQueryResults results = new CQLQueryResults();
        results.setAttributeResult(new CQLAttributeResult[resultsCount]);
        for (int i = 0; i < resultsCount; i++) {
            CQLAttributeResult ar = new CQLAttributeResult();
            ar.setAttribute(new TargetAttribute[numTargetAttributes]);
            for (int j = 0; j < numTargetAttributes; j++) {
                TargetAttribute ta = new TargetAttribute("attribute_" + j + "_" + i, "value_" + j + "_" + i);
                ar.setAttribute(j, ta);
            }
            results.setAttributeResult(i, ar);
        }
        return results;
    }
    
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CQL2QueryResultsIteratorTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
