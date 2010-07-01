package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import org.cagrid.iso21090.tests.integration.ExampleProjectInfo;

public abstract class AbstractLocalCqlInvocationStep extends Step {

    public static final String TESTS_BASEDIR_PROPERTY = "sdk43.tests.base.dir";
    public static final String TESTS_EXT_LIB_DIR = 
        "ext" + File.separator + "dependencies" + File.separator + "jars";
    public static final String SDK_LOCAL_CLIENT_DIR = 
        "sdk" + File.separator + "checkout" + File.separator + "sdk-toolkit" + 
        File.separator + "iso-example-project" + File.separator + "target" + 
        File.separator + "dist" + File.separator + "exploded" + File.separator +
        "output" + File.separator + ExampleProjectInfo.EXAMPLE_PROJECT_NAME + File.separator + "package" + 
        File.separator + "local-client";
    
    private ApplicationService service = null;
    
    public AbstractLocalCqlInvocationStep() {
        super();
    }
    
    
    public void runStep() throws Throwable {
        testLotsOfQueries();
    }
    
    
    private File[] getCqlQueryFiles() {
        File basedir = new File(System.getProperty(TESTS_BASEDIR_PROPERTY));
        File queriesDir = new File(basedir, 
            "test" + File.separator + "resources" + File.separator + "testQueries");
        return queriesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml")
                && !pathname.getName().startsWith("invalid_");
            }
        });
    }
    
    
    private void testLotsOfQueries() {
        File[] queryFiles = getCqlQueryFiles();
        System.out.println("Found " + queryFiles.length + " query documents to run");
        for (File f : queryFiles) {
            System.out.println("Loading " + f.getName());
            CQLQuery query = null;
            try {
                query = (CQLQuery) Utils.deserializeDocument(f.getAbsolutePath(), CQLQuery.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error loading query: " + ex.getMessage());
            }
            System.out.println("Executing query " + f.getName());
            List<?> results = null;
            try {
                results = executeQuery(query);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error executing query " + f.getName() + ": " + ex.getMessage());
            }
            // TODO: load up gold results, validate
        }
    }
    
    
    protected abstract List<?> executeQuery(CQLQuery query) throws Exception;
    
    
    protected ApplicationService getService() {
        if (service == null) {
            try {
                service = ApplicationServiceProvider.getApplicationService();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Error initializing application service: " + e.getMessage());
            }
        }
        return service;
    }
}
