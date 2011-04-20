package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.TargetAttribute;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Method;
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
import org.cagrid.cql.utilities.CQL1toCQL2Converter;
import org.cagrid.cql.utilities.CQL2SerializationUtil;
import org.cagrid.cql.utilities.iterator.CQL2QueryResultsIterator;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.iso21090.tests.integration.SDK43ServiceStyleSystemTestConstants;
import org.oasis.wsrf.faults.BaseFaultType;

public class InvokeCql2DataServiceStep extends Step {
    
    public static final String TEST_RESOURCES_DIR = "test/resources/";
    public static final String TEST_QUERIES_DIR = TEST_RESOURCES_DIR + "testQueries/";
    public static final String TEST_RESULTS_DIR = TEST_RESOURCES_DIR + "testGoldResults/cql2/";
    
    private static Log LOG = LogFactory.getLog(InvokeCql2DataServiceStep.class);
    
    public static final String[] JDK6_SPRING_ERROR_MESSAGES = {
        "nested exception is java.lang.ClassNotFoundException: [Ljava.lang.Object;",
        "cannot assign instance of org.hibernate.proxy.pojo.cglib.SerializableProxy to field "
    };
    
    private DataTestCaseInfo testInfo = null;
    private ServiceContainer container = null;

    public InvokeCql2DataServiceStep(DataTestCaseInfo testInfo, ServiceContainer container) {
        super();
        this.testInfo = testInfo;
        this.container = container;
    }
    
    
    public void runStep() throws Throwable {
        String baseDirName = System.getProperty(SDK43ServiceStyleSystemTestConstants.TESTS_BASEDIR_PROPERTY);
        assertNotNull("Property " + SDK43ServiceStyleSystemTestConstants.TESTS_BASEDIR_PROPERTY + " must be set!");
        File basedir = new File(baseDirName);
        assertTrue("Base dir did not exist or wasn't a directory", basedir.exists() && basedir.isDirectory());
        File[] queryFiles = new File(basedir, TEST_QUERIES_DIR).listFiles(new FileFilter() {
            
            public boolean accept(File pathname) {
                // exclude the [distinct] attribute queries, since those
                // turned into association to ISO types
                String name = pathname.getName();
                return name.endsWith(".xml") &&
                    !name.startsWith("singleAttributeFromCash") &&
                    !name.startsWith("distinctAttributeFromCash");
            }
        });
        assertTrue("Didn't find any query files to test", queryFiles.length > 0);
        
        // sort for sanity
        Arrays.sort(queryFiles, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        
        // set up the query translator
        DomainModel model = MetadataUtils.getDomainModel(getServiceClient().getEndpointReference());
        CQL1toCQL2Converter converter = new CQL1toCQL2Converter(model);
        
        File goldDir = new File(basedir, TEST_RESULTS_DIR);
        for (File f : queryFiles) {
            System.out.println("Testing query file " + f.getName());
            File goldFile = new File(goldDir, "gold_" + f.getName());
            System.out.println("\tGold results file is " + goldFile.getName());
            
            CQLQuery query = loadQuery(f.getAbsolutePath());
            org.cagrid.cql2.CQLQuery convertedQuery = converter.convertToCql2Query(query);
            CQLQueryResults goldResults = loadQueryResults(goldFile.getAbsolutePath());
            invokeValidQueryValidResults(convertedQuery, goldResults);
        }
    }
    
    
    private CQLQuery loadQuery(String filename) {
        CQLQuery query = null;
        try {
            FileReader reader = new FileReader(filename);
            query = Utils.deserializeObject(reader, CQLQuery.class);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing query (" + filename + "): " + ex.getMessage());
        }
        return query;
    }
    
    
    private CQLQueryResults loadQueryResults(String filename)  {
        CQLQueryResults results = null;
        try {
            FileReader reader = new FileReader(filename);
            results = CQL2SerializationUtil.deserializeCql2QueryResults(reader);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing query results (" + filename + "): " + ex.getMessage());
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
    private void invokeValidQueryValidResults(org.cagrid.cql2.CQLQuery query, CQLQueryResults goldResults) {
        DataServiceClient client = getServiceClient();
        CQLQueryResults queryResults = null;
        boolean isSdkJdk6Error = false;
        try {
            queryResults = client.executeQuery(query);
            // If this fails, we need to still be able to exit the jvm
        } catch (Exception ex) {
            if (isJava6() && isSpringJava6Error(ex)) {
                // some of the datatypes don't play nice with JDK 6
                isSdkJdk6Error = true;
                LOG.debug("Query failed due to caCORE SDK incompatibility with JDK 6", ex);
            } else {
                // that's a real failure
                ex.printStackTrace();
                fail("Query failed to execute: " + ex.getMessage());
            }
        }
        if (!isSdkJdk6Error) {
            compareResults(goldResults, queryResults);
        }
    }
    
    
    private DataServiceClient getServiceClient() {
        DataServiceClient client = null;
        try {
            client = new DataServiceClient(getServiceUrl()); 
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
        CQL2QueryResultsIterator goldIter = new CQL2QueryResultsIterator(gold, getClientConfigStream());
        while (goldIter.hasNext()) {
            Object o = goldIter.next();
            if (o instanceof TargetAttribute[]) {
                goldIsAttributes = true;
            }
            goldObjects.add(o);
        }
        
        boolean testIsAttributes = false;
        CQL2QueryResultsIterator testIter = new CQL2QueryResultsIterator(test, getClientConfigStream());
        while (testIter.hasNext()) {
            Object o = testIter.next();
            if (o instanceof TargetAttribute[]) {
                testIsAttributes = true;
            }
            testObjects.add(o);
        }
        
        assertEquals("Number of results differed from expected", goldObjects.size(), testObjects.size());
        assertEquals("Test results as attributes differed from expected", goldIsAttributes, testIsAttributes);
        
        if (goldIsAttributes) {
            List<TargetAttribute[]> goldAttributes = recastList(goldObjects);
            List<TargetAttribute[]> testAttributes = recastList(testObjects);
            compareTargetAttributes(goldAttributes, testAttributes);
        } else {
            // assertTrue("Gold and Test contained different objects", goldObjects.containsAll(testObjects));
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
        String base = System.getProperty(SDK43ServiceStyleSystemTestConstants.TESTS_BASEDIR_PROPERTY);
        String resourceName = TEST_RESOURCES_DIR + "wsdd/client-config.wsdd";
        try {
            is = new FileInputStream(new File(base, resourceName));
            assertNotNull("Could not locate client config wsdd resource", is);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining client config input stream: " + ex.getMessage());
        }
        return is;
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
}
