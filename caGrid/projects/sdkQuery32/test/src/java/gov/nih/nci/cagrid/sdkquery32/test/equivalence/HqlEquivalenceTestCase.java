package gov.nih.nci.cagrid.sdkquery32.test.equivalence;

import gov.nih.nci.cabio.domain.Gene;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.data.sdk32query.CQL2HQL;
import gov.nih.nci.cagrid.sdkquery32.test.TestConstants;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * HqlEquivalenceTestCase Test to ensure the legacy CQL2HQL and the one with new
 * features do, in fact, generate equivalent HQL which returns the same target
 * objects
 * 
 * @author David Ervin
 * @created Jun 12, 2007 4:26:16 PM
 * @version $Id: HqlEquivalenceTestCase.java,v 1.1 2007/06/13 14:25:15 dervin
 *          Exp $
 */
public class HqlEquivalenceTestCase extends TestCase {
    public static final String SDK_APPSERVICE_URL = "sdk.appservice.url";


    private CQLQuery deserializeQuery(String filename) {
        try {
            return (CQLQuery) Utils.deserializeDocument(filename, CQLQuery.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing CQL query: " + ex.getMessage());
        }
        // unreachable, but Java doesn't know that
        return null;
    }


    private List executeOldQuery(CQLQuery query) {
        List results = null;
        try {
            long start = System.currentTimeMillis();
            String hql = CQL2HQL.translate(query, false, false);
            System.out.println("OLD HQL: " + hql);
            ApplicationService service = getAppService();
            results = service.query(new HQLCriteria(hql), query.getTarget().getName());
            // materialize the list
            results.size();
            System.out.println("Old query executed in " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error executing old query: " + ex.getMessage());
        }
        return results;
    }


    private List executeNewQuery(CQLQuery query) {
        List results = null;
        try {
            long start = System.currentTimeMillis();
            String hql = gov.nih.nci.cagrid.data.sdk32query.experimental.hql313.CQL2HQL.convertToHql(query, false,
                false);
            System.out.println("NEW HQL: " + hql);
            ApplicationService service = getAppService();
            results = service.query(new HQLCriteria(hql), query.getTarget().getName());
            // materialize the list
            results.size();
            System.out.println("New query executed in " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error executing new query: " + ex.getMessage());
        }
        return results;
    }


    private void checkForEquivalence(CQLQuery query) {
        List oldWay = executeOldQuery(query);
        List newWay = executeNewQuery(query);
        assertEquals("Old and new query returned different number of results", oldWay.size(), newWay.size());
        try {
            for (Object o1 : oldWay) {
                boolean matchingObjectFound = false;
                for (Object o2 : newWay) {
                    if (FieldEqualityTest.fieldsEqual(o1, o2)) {
                        matchingObjectFound = true;
                        break;
                    }
                }
                assertTrue("Old query method returned object not found with new", matchingObjectFound);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error checking equality: " + ex.getMessage());
        }
    }


    public void testObjectWithAttribute() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(Gene.class.getName());
        Attribute symbolAttrib = new Attribute("symbol", Predicate.LIKE, "IL%");
        target.setAttribute(symbolAttrib);
        query.setTarget(target);

        checkForEquivalence(query);
    }


    public void testGroup() {
        checkForEquivalence(deserializeQuery(TestConstants.TEST_QUERIES_DIR + "objectWithGroup.xml"));
    }


    public void testNestedGroup() {
        checkForEquivalence(deserializeQuery(TestConstants.TEST_QUERIES_DIR + "objectWithNestedGroup.xml"));
    }


    private ApplicationService getAppService() {
        String url = System.getProperty(SDK_APPSERVICE_URL);
        if (url == null) {
            fail("SDK application service url property " + SDK_APPSERVICE_URL + " is required");
        }
        return ApplicationService.getRemoteInstance(url);
    }


    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(HqlEquivalenceTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
