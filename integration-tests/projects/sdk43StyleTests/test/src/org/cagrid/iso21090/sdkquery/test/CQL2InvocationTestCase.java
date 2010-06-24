package org.cagrid.iso21090.sdkquery.test;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql.utilities.CQL1toCQL2Converter;
import org.cagrid.cql.utilities.QueryConversionException;
import org.cagrid.iso21090.sdkquery.translator.ParameterizedHqlQuery;
import org.cagrid.iso21090.sdkquery.translator.cql2.CQL2ToParameterizedHQL;


public class CQL2InvocationTestCase extends TestCase {

    private ApplicationService service = null;
    private CQL1toCQL2Converter converter = null;
    private CQL2ToParameterizedHQL translator = null;


    public CQL2InvocationTestCase(String name) {
        super(name);
        // gets the local SDK service instance
        try {
            service = QueryTestsHelper.getSdkApplicationService();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining application service instance: " + ex.getMessage());
        }

        try {
            translator = QueryTestsHelper.getCql2Translator();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        try {
            FileReader reader = new FileReader("test/resources/isoExample_domainModel.xml");
            DomainModel model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
            converter = new CQL1toCQL2Converter(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating CQL converter: " + ex.getMessage());
        }
    }


    private File[] getCqlQueryFiles() {
        File queriesDir = new File("test/resources/testQueries");
        return queriesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml")
                    && !pathname.getName().startsWith("invalid_") 
                    && !pathname.getName().startsWith("dsetAd");
                    //&& pathname.getName().startsWith("nestedGroups");
            }
        });
    }


    private File getGoldResultsFile(String queryFilename) {
        File goldDir = new File("test/resources/testGoldResults");
        return new File(goldDir, "gold" + CommonTools.upperCaseFirstCharacter(queryFilename));
    }


    public void testLotsOfQueries() {
        File[] queryFiles = getCqlQueryFiles();
        List<Exception> failures = new ArrayList<Exception>();
        System.out.println("Found " + queryFiles.length + " query documents to run");
        for (File f : queryFiles) {
            System.out.println("Loading " + f.getName());
            CQLQuery query = null;
            try {
                query = Utils.deserializeDocument(f.getAbsolutePath(), CQLQuery.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error loading query: " + ex.getMessage());
            }
            System.out.println("Converting to CQL 2");
            org.cagrid.cql2.CQLQuery cql2 = null;
            try {
                cql2 = converter.convertToCql2Query(query);
            } catch (QueryConversionException e) {
                e.printStackTrace();
                fail("Error converting CQL 1 to 2: " + e.getMessage());
            }
            System.out.println("Translating");
            ParameterizedHqlQuery hql = null;
            try {
                hql = translator.convertToHql(cql2);
            } catch (Exception ex) {
                String message = "Error translating query " + f.getName() + ": " + ex.getMessage();
                System.err.println(message);
                Exception fail = new Exception(message, ex);
                failures.add(fail);
                continue;
            }
            System.out.println("Translated query:");
            System.out.println(hql);
            List<?> results = null;
            try {
                results = service.query(new HQLCriteria(hql.getHql(), hql.getParameters()));
            } catch (Exception ex) {
                String message = "Error executing query " + f.getName() + ": " + ex.getMessage();
                System.err.println(message);
                Exception fail = new Exception(message, ex);
                failures.add(fail);
                continue;
            }
            // TODO: load up gold results, validate
        }
        for (Exception ex : failures) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        assertEquals("Some queries failed", 0, failures.size());
    }


    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CQL2InvocationTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
