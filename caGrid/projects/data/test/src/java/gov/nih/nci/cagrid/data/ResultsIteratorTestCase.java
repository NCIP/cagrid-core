package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.cqlresultset.CQLAttributeResult;
import gov.nih.nci.cagrid.cqlresultset.CQLCountResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.cqlresultset.TargetAttribute;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.projectmobius.bookstore.Book;

/** 
 *  ResultsIteratorTestCase
 *  Test case to iterate CQL Query results
 * 
 * @author David Ervin
 * 
 * @created Dec 12, 2007 1:02:25 PM
 * @version $Id: ResultsIteratorTestCase.java,v 1.4 2008-04-17 15:14:52 dervin Exp $ 
 */
public class ResultsIteratorTestCase extends TestCase {
    public static final int EXPECTED_ATTRIBUTE_PER_RESULT_COUNT = 2;
    public static final int EXPECTED_OBJECT_RESULT_COUNT = 5;
    public static final int EXPECTED_ATTRIBUTE_RESULT_COUNT = 5;

    // system property indicating where the result documents live
    public static final String RESULTS_XML_DIR = "results.xml.dir";
    
    public static final String BOOKSTORE_URI = "gme://projectmobius.org/1/BookStore";
    
    private static Log LOG = LogFactory.getLog(ResultsIteratorTestCase.class);
    
    public ResultsIteratorTestCase(String name) {
        super(name);
    }
    
    
    public void testObjectIteration() {
        CQLQueryResults results = loadResults("objectResults.xml");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results, false);
        assertTrue("Object iterator had no results", iter.hasNext());
        int iterationCount = 0;
        while (iter.hasNext()) {
            Object o = iter.next();
            assertNotNull("Object iteration result was null", o);
            assertEquals("Object iteration result was of unexpected type", Book.class, o.getClass());
            iterationCount++;
        }
        assertEquals("Object iterator returned unexpected number of results", EXPECTED_OBJECT_RESULT_COUNT, iterationCount);
        try {
            iter.next();
            fail("Object iterator did not throw NoSuchElementException with no results to return");
        } catch (NoSuchElementException ex) {
            // expected
        }
    }
    
    
    public void testObjectXmlIteration() {
        CQLQueryResults results = loadResults("objectResults.xml");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results, true);
        assertTrue("Object XML iterator had no results", iter.hasNext());
        
        // deserialize the result and compare to the deserialized doc from the original XML
        Element resultsRoot = loadElement("objectResults.xml");
        List objectResultElements = resultsRoot.getChildren("ObjectResult", resultsRoot.getNamespace());
        int iterationCount = 0;
        while (iter.hasNext()) {
            Object o = iter.next();
            assertNotNull("Object XML iteratation result was null", o);
            assertEquals("Object XML iteration result was of unexpected type", String.class, o.getClass());
            assertTrue("Iterator went past number of results in document", iterationCount < objectResultElements.size());
            Element objectResultElement = (Element) objectResultElements.get(iterationCount);
            Element bookElement = objectResultElement.getChild("Book", Namespace.getNamespace(BOOKSTORE_URI));
            String originalString = XMLUtilities.elementToString(bookElement);
            String resultString = (String) o;
            
            Book originalBook = null;
            Book resultBook = null;
            try {
                originalBook = (Book) Utils.deserializeObject(new StringReader(originalString), Book.class);
                resultBook = (Book) Utils.deserializeObject(new StringReader(resultString), Book.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error deserializing XML: " + ex.getMessage());
            }
            assertEquals("Original book and result book objects did not match", originalBook, resultBook);
            
            iterationCount++;
        }
        assertEquals("Object XML iterator returned unexpected number of results", EXPECTED_OBJECT_RESULT_COUNT, iterationCount);
        try {
            iter.next();
            fail("Object XML iterator did not throw NoSuchElementException with no results to return");
        } catch (NoSuchElementException ex) {
            // expected
        }
    }
    
    
    public void testAttributeIteration() {
        CQLQueryResults results = loadResults("attributeResults.xml");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results, false);
        assertTrue("Attribute iterator had no results", iter.hasNext());
        int iterationCount = 0;
        while (iter.hasNext()) {
            Object o = iter.next();
            assertNotNull("Attribute iteration result was null", o);
            assertEquals("Attribute iteration result was of unexpected type", TargetAttribute[].class, o.getClass());
            iterationCount++;
        }
        assertEquals("Attribute iterator returned unexpected number of results", EXPECTED_ATTRIBUTE_RESULT_COUNT, iterationCount);
        try {
            iter.next();
            fail("Attribute iterator did not throw NoSuchElementException with no results to return");
        } catch (NoSuchElementException ex) {
            // expected
        }
    }
    
    
    public void testAttributeXmlIteration() {
        CQLQueryResults results = loadResults("attributeResults.xml");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results, true);
        assertTrue("Attribute XML iterator had no results", iter.hasNext());
        
        // deserialize the result and compare to the deserialized doc from the original XML
        Element resultsRoot = loadElement("attributeResults.xml");
        List attributeResultElements = resultsRoot.getChildren("AttributeResult", resultsRoot.getNamespace());
        int iterationCount = 0;
        while (iter.hasNext()) {
            Object o = iter.next();
            assertNotNull("Attribute XML iteratation result was null", o);
            assertEquals("Attribute XML iteration result was of unexpected type", String.class, o.getClass());
            assertTrue("Iterator went past number of results in document", iterationCount < attributeResultElements.size());
            Element attributeResultElement = (Element) attributeResultElements.get(iterationCount);
            /*
            List attributeElements = attributeResultElement.getChildren("Attribute", attributeResultElement.getNamespace());
            assertEquals("Unexpected number of attribute elements", EXPECTED_ATTRIBUTE_PER_RESULT_COUNT, attributeElements.size());
            */
            
            String originalString = XMLUtilities.elementToString(attributeResultElement);
            String resultString = (String) o;
            
            CQLAttributeResult originalAttributes = null;
            CQLAttributeResult resultAttributes = null;
            try {
                originalAttributes = (CQLAttributeResult) Utils.deserializeObject(new StringReader(originalString), CQLAttributeResult.class);
                resultAttributes = (CQLAttributeResult) Utils.deserializeObject(new StringReader(resultString), CQLAttributeResult.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error deserializing XML: " + ex.getMessage());
            }
            assertEquals("Original and result attributes did not match", originalAttributes, resultAttributes);
            
            iterationCount++;
        }
        assertEquals("Attribute XML iterator returned unexpected number of results", EXPECTED_ATTRIBUTE_RESULT_COUNT, iterationCount);
        try {
            iter.next();
            fail("Attribute XML iterator did not throw NoSuchElementException with no results to return");
        } catch (NoSuchElementException ex) {
            // expected
        }
    }
    
    
    public void testCountIteration() {
        CQLQueryResults results = loadResults("countResults.xml");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results, false);
        assertTrue("Count iterator had no results", iter.hasNext());
        Object o = iter.next();
        assertNotNull("Count iteration result was null", o);
        assertEquals("Count iteration result was of unexpected type", Long.class, o.getClass());
        assertEquals("Count iteration result was of unexpected value", ((Long) o).longValue(), 200);
        assertFalse("Count iterator claims to have more results", iter.hasNext());
        try {
            iter.next();
            fail("Count iterator did not throw NoSuchElementException with no results to return");
        } catch (NoSuchElementException ex) {
            // expected
        }
    }
    
    
    public void testCountXmlIteration() {
        CQLQueryResults results = loadResults("countResults.xml");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results, true);
        assertTrue("Count XML iterator had no results", iter.hasNext());
        Object o = iter.next();
        assertNotNull("Count XML iteratation result was null", o);
        assertEquals("Count XML iteration result was of unexpected type", String.class, o.getClass());
        // deserialize the result and compare to the deserialized doc from the original XML
        Element resultsRoot = loadElement("countResults.xml");
        Element countResultElement = resultsRoot.getChild("CountResult", resultsRoot.getNamespace());
        String originalString = XMLUtilities.elementToString(countResultElement);
        String resultString = (String) o;
        
        CQLCountResult originalCount = null;
        CQLCountResult resultCount = null;
        try {
            originalCount = (CQLCountResult) Utils.deserializeObject(new StringReader(originalString), CQLCountResult.class);
            resultCount = (CQLCountResult) Utils.deserializeObject(new StringReader(resultString), CQLCountResult.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing XML: " + ex.getMessage());
        }
        assertEquals("Original count and result count objects did not match", originalCount, resultCount);
        assertFalse("Count XML iterator claims to have more results", iter.hasNext());
        try {
            iter.next();
            fail("Count XML iterator did not throw NoSuchElementException with no results to return");
        } catch (NoSuchElementException ex) {
            // expected
        }
    }
    
    
    /*
    public void testIdentifierIteration() {
        // TODO: uncomment and use when identifiers are implemented
    }
    
    
    public void testIdentifierXmlIteration() {
        // TODO: uncomment and use when identifiers are implemented
    }
    */
    
    
    private CQLQueryResults loadResults(String filename) {
        CQLQueryResults results = null;
        try {
            String dir = System.getProperty(RESULTS_XML_DIR);
            LOG.debug("Results dir: " + dir);
            File dirFile = new File(dir);
            FileReader reader = new FileReader(new File(dirFile, filename));
            results = (CQLQueryResults) Utils.deserializeObject(reader, CQLQueryResults.class);
            reader.close();
        } catch (Exception ex) {
            LOG.error("Error loading results: " + ex.getMessage(), ex);
            fail("Error loading results: " + ex.getMessage());
        }
        
        return results;
    }
    
    
    private Element loadElement(String filename) {
        Element element = null;
        try {
            String dir = System.getProperty(RESULTS_XML_DIR);
            File dirFile = new File(dir);
            Document doc = XMLUtilities.fileNameToDocument(new File(dirFile, filename).getAbsolutePath());
            element = doc.detachRootElement();
        } catch (Exception ex) {
            LOG.error("Error loading results as Element: " + ex.getMessage(), ex);
            fail("Error loading results as Element: " + ex.getMessage());
        }
        return element;
    }
    
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(ResultsIteratorTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
