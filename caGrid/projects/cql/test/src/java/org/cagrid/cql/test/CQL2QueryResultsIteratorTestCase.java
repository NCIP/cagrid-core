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

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql.utilities.AttributeFactory;
import org.cagrid.cql.utilities.CQL2ResultsCreationUtil;
import org.cagrid.cql.utilities.DCQL2Constants;
import org.cagrid.cql.utilities.iterator.CQL2QueryResultsIterator;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.TargetAttribute;
import org.cagrid.data.dcql.DCQLGroup;
import org.cagrid.data.dcql.DCQLObject;
import org.cagrid.data.dcql.DCQLQuery;
import org.jdom.Element;


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
        try {
            iter.next();
            fail("Iterator should not have been able to move next");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type (" + ex.getClass().getName() + ") thrown",
                ex instanceof NoSuchElementException);
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
    
    
    public void testIterateObjects() {
        int resultsCount = 100;
        int attributeCount = 3;
        CQLQueryResults results = generateObjectResults(resultsCount, attributeCount);
        InputStream wsdd = getClass().getResourceAsStream("/org/cagrid/data/dcql/mapping/client-config.wsdd");
        CQL2QueryResultsIterator iter = new CQL2QueryResultsIterator(results, wsdd);
        assertTrue("Iterator should have had results, but hasNext() was false", iter.hasNext());
        int foundCount = 0;
        while (iter.hasNext()) {
            foundCount++;
            Object item = iter.next();
            assertNotNull("Item returne from iterator was null", item);
            assertEquals("Object returned from the iterator should have been a DCQLQuery", 
                DCQLQuery.class, item.getClass());
        }
        assertEquals("Unexpected number of results returned from the iterator", resultsCount, foundCount);
        try {
            iter.next();
            fail("Iterator should not have been able to move next");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type (" + ex.getClass().getName() + ") thrown",
                ex instanceof NoSuchElementException);
        }
    }
    
    
    public void testIterateObjectsXml() {
        int resultsCount = 100;
        int attributeCount = 3;
        CQLQueryResults results = generateObjectResults(resultsCount, attributeCount);
        CQL2QueryResultsIterator iter = new CQL2QueryResultsIterator(results, true);
        assertTrue("Iterator should have had results, but hasNext() was false", iter.hasNext());
        int foundCount = 0;
        while (iter.hasNext()) {
            foundCount++;
            Object item = iter.next();
            assertNotNull("Item returne from iterator was null", item);
            assertEquals("Object returned from the iterator should have been a String", 
                String.class, item.getClass());
            String xml = (String) item;
            try {
                Element element = XMLUtilities.stringToDocument(xml).getRootElement();
                assertEquals("XML element name did not match expected", "DCQLQuery", element.getName());
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error converting the string to XML element (" + xml + ")");
            }
        }
        assertEquals("Unexpected number of results returned from the iterator", resultsCount, foundCount);
    }
    
    
    private CQLQueryResults generateObjectResults(int resultsCount, int numAttributes) {
        List<DCQLQuery> data = new ArrayList<DCQLQuery>();
        for (int i = 0; i < resultsCount; i++) {
            DCQLQuery q = new DCQLQuery();
            DCQLObject target = new DCQLObject();
            target.setName("FakeClass" + i);
            DCQLGroup group = new DCQLGroup();
            group.setLogicalOperation(GroupLogicalOperator.AND);
            group.setAttribute(new CQLAttribute[numAttributes]);
            for (int j = 0; j < numAttributes; j++) {
                CQLAttribute a = AttributeFactory.createAttribute(
                    "attribute_" + i + "_" + j, BinaryPredicate.EQUAL_TO, "value_" + i + "_" + j);
                group.setAttribute(j, a);
            }
            target.setGroup(group);
            q.setTargetObject(target);
            data.add(q);
        }
        InputStream wsdd = getClass().getResourceAsStream("/org/cagrid/data/dcql/mapping/client-config.wsdd");
        CQLQueryResults results = null;
        try {
            results = CQL2ResultsCreationUtil.createObjectResults(
                data, DCQLQuery.class.getName(), DCQL2Constants.DCQL2_QUERY_QNAME, wsdd);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating object results: " + ex.getMessage());
        }
        return results;
    }
    
    
    private CQLQueryResults generateAttributeResults(int resultsCount, int numTargetAttributes) {
        List<Object[]> values = new ArrayList<Object[]>();
        String[] names = new String[numTargetAttributes];
        for (int i = 0; i < numTargetAttributes; i++) {
            names[i] = "attribute_" + i;
        }
        for (int i = 0; i < resultsCount; i++) {
            String[] val = new String[numTargetAttributes];
            for (int j = 0; j < numTargetAttributes; j++) {
                val[j] = "value_" + i + "_" + j;
            }
            values.add(val);
        }
        CQLQueryResults results = 
            CQL2ResultsCreationUtil.createAttributeResults(values, "FakeClass", names);
        return results;
    }
    
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CQL2QueryResultsIteratorTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
