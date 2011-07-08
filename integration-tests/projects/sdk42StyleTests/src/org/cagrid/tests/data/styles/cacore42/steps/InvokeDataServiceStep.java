package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.cqlresultset.TargetAttribute;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.globus.common.CoGProperties;
import org.globus.gsi.GlobusCredential;
import org.oasis.wsrf.faults.BaseFaultType;

public class InvokeDataServiceStep extends Step {
    
    // credential filename
    public static final String PROXY_FILENAME = "user.proxy";

    public static final String TEST_RESOURCES_DIR = "/resources/";
    public static final String TEST_QUERIES_DIR = TEST_RESOURCES_DIR + "testQueries/";
    public static final String TEST_RESULTS_DIR = TEST_RESOURCES_DIR + "testGoldResults/";
    
    public static final String[] JDK6_SPRING_ERROR_MESSAGES = {
        "nested exception is java.lang.ClassNotFoundException: [Ljava.lang.Object;",
        "cannot assign instance of org.hibernate.proxy.pojo.cglib.SerializableProxy to field "
    };
    
    private static Log LOG = LogFactory.getLog(InvokeDataServiceStep.class);
    
    private DataTestCaseInfo testInfo = null;
    private ServiceContainer container = null;

    public InvokeDataServiceStep(DataTestCaseInfo testInfo, ServiceContainer container) {
        super();
        this.testInfo = testInfo;
        this.container = container;
    }

    
    public void runStep() throws Throwable {
        // valid queries
        testUndergraduateStudentWithName();
        testAllPayments();
        testDistinctAttributeFromCash();
        testAssociationNotNull();
        testCountAssociationNotNull();
        testAssociationWithAttributeEqual();
        testGroupOfAttributesUsingAnd();
        testGroupOfAttributesUsingOr();
        testGroupOfAssociationsUsingAnd();
        testGroupOfAssociationsUsingOr();
        testNestedAssociations();
        testNestedAssociationsNoRoleNames();
        testAssociationWithGroup();
        testNestedGroups();
        testSingleAttributeFromCash();
        testAllSuperclass();
        
        // invalid queries
        testNonExistantTarget();
        testNonExistantAssociation();
        testNonExistantAttribute();
        testAssociationWithWrongAttributeDatatype();
    }
    
    
    private void testUndergraduateStudentWithName() {
        LOG.debug("testUndergraduateStudentWithName");
        CQLQuery query = loadQuery("undergraduateStudentWithName.xml");
        CQLQueryResults results = loadQueryResults("goldUndergraduateStudentWithName.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testAllPayments() {
        LOG.debug("testAllPayments");
        CQLQuery query = loadQuery("allPayments.xml");
        CQLQueryResults results = loadQueryResults("goldAllPayments.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testDistinctAttributeFromCash() {
        LOG.debug("testDistinctAttributeFromCash");
        CQLQuery query = loadQuery("distinctAttributeFromCash.xml");
        CQLQueryResults results = loadQueryResults("goldDistinctAttributeFromCash.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testAssociationNotNull() {
        LOG.debug("testAssociationNotNull");
        CQLQuery query = loadQuery("associationNotNull.xml");
        CQLQueryResults results = loadQueryResults("goldAssociationNotNull.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testAssociationWithAttributeEqual() {
        LOG.debug("testAssociationWithAttributeEqual");
        CQLQuery query = loadQuery("associationWithAttributeEqual.xml");
        CQLQueryResults results = loadQueryResults("goldAssociationWithAttributeEqual.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testGroupOfAttributesUsingAnd() {
        LOG.debug("testGroupOfAttributesUsingAnd");
        CQLQuery query = loadQuery("groupOfAttributesUsingAnd.xml");
        CQLQueryResults results = loadQueryResults("goldGroupOfAttributesUsingAnd.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testGroupOfAttributesUsingOr() {
        LOG.debug("testGroupOfAttributesUsingOr");
        CQLQuery query = loadQuery("groupOfAttributesUsingOr.xml");
        CQLQueryResults results = loadQueryResults("goldGroupOfAttributesUsingOr.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testGroupOfAssociationsUsingAnd() {
        LOG.debug("testGroupOfAssociationsUsingAnd");
        CQLQuery query = loadQuery("groupOfAssociationsUsingAnd.xml");
        CQLQueryResults results = loadQueryResults("goldGroupOfAssociationsUsingAnd.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testGroupOfAssociationsUsingOr() {
        LOG.debug("testGroupOfAssociationsUsingOr");
        CQLQuery query = loadQuery("groupOfAssociationsUsingOr.xml");
        CQLQueryResults results = loadQueryResults("goldGroupOfAssociationsUsingOr.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testNestedAssociations() {
        LOG.debug("testNestedAssociations");
        CQLQuery query = loadQuery("nestedAssociations.xml");
        CQLQueryResults results = loadQueryResults("goldNestedAssociations.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testNestedAssociationsNoRoleNames() {
        LOG.debug("testNestedAssociationsNoRoleNames");
        CQLQuery query = loadQuery("nestedAssociationsNoRoleNames.xml");
        // should have same results as with role names
        CQLQueryResults results = loadQueryResults("goldNestedAssociations.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testAssociationWithGroup() {
        LOG.debug("testAssociationWithGroup");
        CQLQuery query = loadQuery("associationWithGroup.xml");
        CQLQueryResults results = loadQueryResults("goldAssociationWithGroup.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testCountAssociationNotNull() {
        LOG.debug("testCountAssociationNotNull");
        CQLQuery query = loadQuery("countAssociationNotNull.xml");
        CQLQueryResults results = loadQueryResults("goldCountAssociationNotNull.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testNestedGroups() {
        LOG.debug("testNestedGroups");
        CQLQuery query = loadQuery("nestedGroups.xml");
        CQLQueryResults results = loadQueryResults("goldNestedGroups.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testSingleAttributeFromCash() {
        LOG.debug("testSingleAttributeFromCash");
        CQLQuery query = loadQuery("singleAttributeFromCash.xml");
        CQLQueryResults results = loadQueryResults("goldSingleAttributeFromCash.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testAllSuperclass() {
        LOG.debug("testAllSuperclass");
        CQLQuery query = loadQuery("allSuperclass.xml");
        CQLQueryResults results = loadQueryResults("goldAllSuperclass.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    
    private void testNonExistantTarget() {
        LOG.debug("testNonExistantTarget");
        CQLQuery query = loadQuery("invalid_nonExistantTarget.xml");
        invokeInvalidQuery(query);
    }
    
    
    private void testNonExistantAssociation() {
        LOG.debug("testNonExistantAssociation");
        CQLQuery query = loadQuery("invalid_nonExistantAssociation.xml");
        invokeInvalidQuery(query);
    }
    
    
    private void testNonExistantAttribute() {
        LOG.debug("testNonExistantAttribute");
        CQLQuery query = loadQuery("invalid_nonExistantAttribute.xml");
        invokeInvalidQuery(query);
    }
    
    
    private void testAssociationWithWrongAttributeDatatype() {
        LOG.debug("testAssociationWithWrongAttributeDatatype");
        CQLQuery query = loadQuery("invalid_associationWithWrongAttributeDatatype.xml");
        invokeInvalidQuery(query);
    }
    
    
    private CQLQuery loadQuery(String filename) {
        String fullFilename = TEST_QUERIES_DIR + filename;
        CQLQuery query = null;
        try {
            InputStream queryInputStream = InvokeDataServiceStep.class.getResourceAsStream(fullFilename);
            InputStreamReader reader = new InputStreamReader(queryInputStream);
            query = Utils.deserializeObject(reader, CQLQuery.class);
            reader.close();
            queryInputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing query (" + fullFilename + "): " + ex.getMessage());
        }
        return query;
    }
    
    
    private CQLQueryResults loadQueryResults(String filename)  {
        String fullFilename = TEST_RESULTS_DIR + filename;
        CQLQueryResults results = null;
        try {
            InputStream resultInputStream = InvokeDataServiceStep.class.getResourceAsStream(fullFilename);
            InputStreamReader reader = new InputStreamReader(resultInputStream);
            results = Utils.deserializeObject(reader, CQLQueryResults.class);
            reader.close();
            resultInputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing query results (" + fullFilename + "): " + ex.getMessage());
        }
        return results;
    }
    
    
    /**
     * Executes a query, which is expected to be valid and compares the
     * results to gold, which is expected to be valid
     * @param query
     *      The query to execute
     * @param goldResults
     *      The gold results set
     */
    private void invokeValidQueryValidResults(CQLQuery query, CQLQueryResults goldResults) {
        CQLQueryResults queryResults = invokeValidQuery(query);
        if (queryResults == null && isJava6()) {
            LOG.info("No results returned from the query due to caCORE SDK + JDK6");
        } else {
            compareResults(goldResults, queryResults);
        }
    }
    
    
    protected CQLQueryResults invokeValidQuery(CQLQuery query) {
        DataServiceClient client = getServiceClient();
        CQLQueryResults queryResults = null;
        try {
            queryResults = client.query(query);
            // If this fails, we need to still be able to cleanly exit
        } catch (Exception ex) {
            if (isJava6() && isSpringJava6Error(ex)) {
                // some of the datatypes don't play nice with JDK 6
                LOG.info("Query failed due to caCORE SDK incompatibility with JDK 6", ex);
            } else {
                // that's a real failure
                ex.printStackTrace();
                fail("Query failed to execute: " + ex.getMessage());
            }
        }
        return queryResults;
    }
    
    
    /**
     * Executes a query, which is expected to be invalid and fail
     * @param query
     *      The expected invalid query
     */
    protected void invokeInvalidQuery(CQLQuery query) {
        DataServiceClient client = getServiceClient();
        try {
            client.query(query);
            fail("Query returned results, should have failed");
        } catch (Exception ex) {
            // expected
        }
    }
    
    
    protected DataServiceClient getServiceClient() {
        DataServiceClient client = null;
        try {
            if (container instanceof SecureContainer) {
            	configureCaDirectory();
                client = new DataServiceClient(getServiceUrl(), loadGlobusCredential());
                client.setAnonymousPrefered(false);
            } else {
                client = new DataServiceClient(getServiceUrl());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating data service client: " + ex.getMessage());
        }
        return client;
    }
    
    
    private String getServiceUrl() {
        String url = null;
        try {
            URI baseUri = container.getContainerBaseURI();
            url = baseUri.toString() + "cagrid/" + testInfo.getName();
        } catch (MalformedURIException ex) {
            ex.printStackTrace();
            fail("Error generating service url: " + ex.getMessage());
        }
        LOG.debug("Data service url: " + url);
        return url;
    }
    
    
    private void compareResults(CQLQueryResults gold, CQLQueryResults test) {
        List<Object> goldObjects = new ArrayList<Object>();
        List<Object> testObjects = new ArrayList<Object>();
        
        boolean goldIsAttributes = false;
        boolean goldIsCount = false;
        CQLQueryResultsIterator goldIter = new CQLQueryResultsIterator(gold, getClientConfigStream());
        while (goldIter.hasNext()) {
            Object o = goldIter.next();
            if (o instanceof TargetAttribute[]) {
                goldIsAttributes = true;
            } else if (o instanceof Long) {
                goldIsCount = true;
            }
            goldObjects.add(o);
        }
        
        boolean testIsAttributes = false;
        boolean testIsCount = false;
        CQLQueryResultsIterator testIter = new CQLQueryResultsIterator(test, getClientConfigStream());
        while (testIter.hasNext()) {
            Object o = testIter.next();
            if (o instanceof TargetAttribute[]) {
                testIsAttributes = true;
            } else if (o instanceof Long) {
                testIsCount = true;
            }
            testObjects.add(o);
        }
        
        assertEquals("Number of results differed from expected", goldObjects.size(), testObjects.size());
        assertEquals("Test results as attributes differed from expected", goldIsAttributes, testIsAttributes);
        assertEquals("Test results as count differed from expected", goldIsCount, testIsCount);
        
        if (goldIsAttributes) {
            List<TargetAttribute[]> goldAttributes = recastList(goldObjects);
            List<TargetAttribute[]> testAttributes = recastList(testObjects);
            compareTargetAttributes(goldAttributes, testAttributes);
        } else if (goldIsCount) {
            assertEquals("Count value differed from expected", goldObjects.get(0), testObjects.get(0));
        } else {
            compareObjects(goldObjects, testObjects);
        }
    }
    
    
    private void compareObjects(List<Object> gold, List<Object> test) {
        if (!gold.containsAll(test)) {
            // fail, but why?
            List<Object> tempGold = new ArrayList<Object>();
            tempGold.addAll(gold);
            tempGold.removeAll(test);
            StringBuffer errors = new StringBuffer();
            errors.append("The following objects were expected but not found\n");
            dumpGetters(tempGold, errors);
            List<Object> tempTest = new ArrayList<Object>();
            tempTest.addAll(test);
            tempTest.removeAll(gold);
            errors.append("\n\nThe following objects were found, but not expected\n");
            dumpGetters(tempTest, errors);
            fail(errors.toString());
        }
    }
    
    
    private void dumpGetters(Collection<Object> objs, StringBuffer buff) {
        for (Object o : objs) {
            buff.append(o.getClass().getName()).append("\n");
            Method[] methods = o.getClass().getMethods();
            for (Method m : methods) {
                if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
                    try {
                        Object value = m.invoke(o, new Object[0]);
                        buff.append(m.getName());
                        buff.append(" --> ");
                        buff.append(String.valueOf(value));
                        buff.append("\n");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    
    /**
     * assumes sizes of both lists are equal
     */
    private void compareTargetAttributes(List<TargetAttribute[]> gold, List<TargetAttribute[]> test) {
        // Must find each array of gold attributes (in any order) 
        // in the test attributes list
        
        // sorts attributes for consistency of comparison
        Comparator<TargetAttribute> attributeSorter = new Comparator<TargetAttribute>() {
            public int compare(TargetAttribute o1, TargetAttribute o2) {
                String att1String = o1.getName() + "=" + o1.getValue();
                String att2String = o2.getName() + "=" + o2.getValue();
                return att1String.compareTo(att2String);
            }
        };
        
        // walk the gold attribute arrays
        for (TargetAttribute[] goldAttributes : gold) {
            // sort that array
            Arrays.sort(goldAttributes, attributeSorter);
            
            // find a matching array of attributes in the test set
            Iterator<TargetAttribute[]> testIter = test.iterator();
            while (testIter.hasNext()) {
                TargetAttribute[] testAttributes = testIter.next();
                
                // sort them out
                Arrays.sort(testAttributes, attributeSorter);
                
                // veriy the same number of attributes.  This should be true for every array
                assertEquals("Number of attributes differed from expected", goldAttributes.length, testAttributes.length);
                
                // check that the current goldAttribute[] matches the test[]
                boolean matching = true;
                for (int i = 0; i < goldAttributes.length && matching; i++) {
                    assertEquals("Unexpected attribute name in test results", 
                        goldAttributes[i].getName(), testAttributes[i].getName());
                    String goldValue = goldAttributes[i].getValue();
                    String testValue = testAttributes[i].getValue();
                    matching = String.valueOf(goldValue).equals(String.valueOf(testValue));
                }
                if (matching) {
                    // found a matching TargetAttribute[] in test for one in gold.
                    // remove it from the test set so anything left in there is
                    // not a valid result when this process completes
                    testIter.remove();
                }
            }
        }
        
        if (test.size() != 0) {
            StringBuffer errors = new StringBuffer();
            errors.append("The following attribute arrays were not expected in the test results:");
            for (TargetAttribute[] atts : test) {
                errors.append("---------\n");
                for (TargetAttribute ta : atts) {
                    errors.append("Attribute: ").append(ta.getName()).append("\t\tValue: ")
                        .append(ta.getValue()).append("\n");
                }
            }
            fail(errors.toString());
        }
    }
    
    
    @SuppressWarnings("unchecked")
    private <T> List<T> recastList(List<?> set) {
        List<T> returnme = new ArrayList<T>();
        for (Object o : set) {
            returnme.add((T) o);
        }
        return returnme;
    }
    
    
    private InputStream getClientConfigStream() {
        InputStream is = null;
        String resourceName = TEST_RESOURCES_DIR + "wsdd/sdk-datatypes-client-config.wsdd";
        try {
            is = InvokeDataServiceStep.class.getResourceAsStream(resourceName);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining client config input stream: " + ex.getMessage());
        }
        return is;
    }
    
    
    private GlobusCredential loadGlobusCredential() {
        // Load the testing proxy cert
        GlobusCredential proxyCredential = null;
        try {
            File proxyFile = new File(((SecureContainer) container).getCertificatesDirectory(), PROXY_FILENAME);
            proxyCredential = new GlobusCredential(proxyFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining client proxy: " + ex.getMessage());
        }
        return proxyCredential;
    }
    
        
    protected boolean isJava6() {
        boolean is6 = false;
        String val = System.getProperty("java.version");
        if (val != null && val.startsWith("1.6")) {
            is6 = true;
        }
        return is6;
    }
    
    
    protected boolean isSpringJava6Error(Exception ex) {
        Throwable cause = ex;
        while (cause != null) {
            String message = cause.getMessage();
            if (cause instanceof BaseFaultType) {
                message = FaultHelper.getMessage(cause);
            }
            if (message != null) {
                for (String findme : JDK6_SPRING_ERROR_MESSAGES) {
                    if (message.contains(findme)) {
                        return true;
                    }
                }
            }
            cause = cause.getCause();
        }
        return false;
    }
    
    
    private void configureCaDirectory() throws RemoteException {
        File caCertsDir = null;
        try {
            File certsDir = ((SecureContainer) container).getCertificatesDirectory();
            caCertsDir = new File(certsDir, "ca");
        } catch (Exception ex) {
            throw new RemoteException("Error obtaining CA certificates directory from service container: " + ex.getMessage(), ex);
        }
        org.globus.common.CoGProperties properties = org.globus.common.CoGProperties.getDefault();
        System.out.println("CERTS DIRECTORY IS SET TO: " + properties.getCaCertLocations());
        properties.setCaCertLocations(caCertsDir.getAbsolutePath());
        org.globus.common.CoGProperties.setDefault(properties);
        System.out.println("SET CERTS DIRECTORY TO " + caCertsDir.getAbsolutePath());
    }

}
