package gov.nih.nci.cagrid.sdk.equivalence.test;

import gov.nih.nci.cabio.domain.Gene;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.sdk32query.CQL2HQL;
import gov.nih.nci.cagrid.sdkquery32.test.equivalence.FieldEqualityTest;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  QueryEquivalenceTestCase
 *  Test case to verify the 'old' and 'new' query processors produce
 *  the same result set given the same CQL query
 * 
 * @author David Ervin
 * 
 * @created Jul 5, 2007 2:45:01 PM
 * @version $Id: QueryEquivalenceTestCase.java,v 1.1 2007-07-06 18:45:49 dervin Exp $ 
 */
public class QueryEquivalenceTestCase extends TestCase {
    
    public static final String APPSERVICE_URL_PROPERTY = "application.service.url";
    
    private ApplicationService appservice = null;
    
    public QueryEquivalenceTestCase(String name) {
        super(name);
        getApplicationService();
    }
    
    
    private CQLQuery loadQuery(String filename) {
        CQLQuery query = null;
        try {
            query = (CQLQuery) Utils.deserializeDocument(filename, CQLQuery.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading query from file " + filename + " : " + ex.getMessage());
        }
        return query;
    }
    
    
    private List executeOldQuery(CQLQuery query) {
        long start = System.currentTimeMillis();
        String hql = null;
        try {
            hql = CQL2HQL.translate(query, false, false);
        } catch (QueryProcessingException ex) {
            ex.printStackTrace();
            fail("Error converting query to old HQL: " + ex.getMessage());
        }
        System.out.println("Time to convert CQL to HQL: " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("Generated HQL:");
        System.out.println("\t" + hql);
        start = System.currentTimeMillis();
        List values = null;
        try {
            values = getApplicationService().query(new HQLCriteria(hql), query.getTarget().getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error querying application service: " + ex.getMessage());
        }
        System.out.println("Time to query with old HQL: " + (System.currentTimeMillis() - start) + "ms");
        return values;
    }
    
    
    private List executeNewQuery(CQLQuery query) {
        long start = System.currentTimeMillis();
        String hql = null;
        try {
            hql = gov.nih.nci.cagrid.data.sdk32query.experimental.hql313.CQL2HQL.convertToHql(query, false, false);
        } catch (QueryProcessingException ex) {
            ex.printStackTrace();
            fail("Error converting query to new HQL: " + ex.getMessage());
        }
        System.out.println("Time to convert CQL to HQL: " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("Generated HQL:");
        System.out.println("\t" + hql);
        start = System.currentTimeMillis();
        List values = null;
        try {
            values = getApplicationService().query(new HQLCriteria(hql), query.getTarget().getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error querying application service: " + ex.getMessage());
        }
        System.out.println("Time to query with new HQL: " + (System.currentTimeMillis() - start) + "ms");
        return values;
    }
    
    
    private ApplicationService getApplicationService() {
        if (appservice == null) {
            String url = System.getProperty(APPSERVICE_URL_PROPERTY);
            if (url == null) {
                fail("The property " + APPSERVICE_URL_PROPERTY + " must be supplied!");
            }
            try {
                appservice = ApplicationService.getRemoteInstance(url);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error initializing application service: " + ex.getMessage());
            }
        }
        return appservice;
    }
    
    
    private void checkForMatchingValues(CQLQuery query) {
        List oldResults = executeOldQuery(query);
        List newResults = executeNewQuery(query);
        int oldSize = oldResults.size();
        int newSize = newResults.size();
        assertTrue("Old query returned no results", oldSize != 0);
        assertTrue("New query returned no results", newSize != 0);
        assertEquals("Old and new result sizes differed!", oldSize, newSize);
        try {
            for (int i = 0; i < oldResults.size(); i++) {
                Object oldItem = oldResults.get(i);
                boolean matchFound = false;
                for (int j = 0; !matchFound && j < newResults.size(); j++) {
                    Object newItem = newResults.get(j);
                    
                    matchFound = FieldEqualityTest.fieldsEqual(oldItem, newItem);
                }
                if (!matchFound) {
                    fail("No match found for item " + i + " of old result set");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error comparing old and new results: " + ex.getMessage());
        }
    }
    
    
    // -----
    // tests
    // -----
    
    
    public void testSimpleQuery() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(Gene.class.getName());
        Attribute attrib = new Attribute("symbol", Predicate.LIKE, "IL%");
        target.setAttribute(attrib);
        query.setTarget(target);
        
        checkForMatchingValues(query);
    }
    
    
    public void testObjectWithAssociation() {
        CQLQuery query = loadQuery("test/resources/objectWithAssociation.xml");
        checkForMatchingValues(query);
    }
    
    
    public void testObjectWithAssociationNoRoleName() {
        CQLQuery query = loadQuery("test/resources/objectWithAssociationNoRoleName.xml");
        checkForMatchingValues(query);
    }
    
    
    public void testObjectWithAttribute() {
        CQLQuery query = loadQuery("test/resources/objectWithAttribute.xml");
        checkForMatchingValues(query);
    }
    
    
    public void testObjectWithGroup() {
        CQLQuery query = loadQuery("test/resources/objectWithGroup.xml");
        checkForMatchingValues(query);
    }
    
    
    public void testObjectWithNestedGroup() {
        CQLQuery query = loadQuery("test/resources/objectWithNestedGroup.xml");
        checkForMatchingValues(query);
    }
    
    
    public void testReturnAllOfType() {
        CQLQuery query = loadQuery("test/resources/returnAllOfType.xml");
        checkForMatchingValues(query);
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(QueryEquivalenceTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
