package org.cagrid.data.sdkquery42.test;

import java.util.Properties;

import org.cagrid.data.sdkquery42.processor.SDK42QueryProcessor;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class QueryProcessorInitializationTestCase extends TestCase {
    
    private SDK42QueryProcessor queryProcessor = null;
    
    public QueryProcessorInitializationTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        this.queryProcessor = new SDK42QueryProcessor();
    }
    
    
    public void testInitializeLocalApi() {
        Properties props = queryProcessor.getRequiredParameters();
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API, "true");
        try {
            queryProcessor.initialize(props, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error initializing query processor: " + ex.getMessage());
        }
    }
    
    
    public void testInitializeLocalApiWithGridIdent() {
        Properties props = queryProcessor.getRequiredParameters();
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API, "true");
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_GRID_IDENTITY_LOGIN, "true");
        try {
            queryProcessor.initialize(props, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error initializing query processor: " + ex.getMessage());
        }
    }
    
    
    public void testInitializeLocalApiWithStaticLogin() {
        Properties props = queryProcessor.getRequiredParameters();
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API, "true");
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_STATIC_LOGIN, "true");
        props.setProperty(SDK42QueryProcessor.PROPERTY_STATIC_LOGIN_USER, "fakeuser");
        props.setProperty(SDK42QueryProcessor.PROPERTY_STATIC_LOGIN_PASS, "fakepass");
        try {
            queryProcessor.initialize(props, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error initializing query processor: " + ex.getMessage());
        }
    }
    
    
    public void testInitializeRemoteApi() {
        Properties props = queryProcessor.getRequiredParameters();
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API, "false");
        try {
            queryProcessor.initialize(props, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error initializing query processor: " + ex.getMessage());
        }
    }
    
    
    public void testInitializeRemoteApiWithStaticLogin() {
        Properties props = queryProcessor.getRequiredParameters();
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API, "false");
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_STATIC_LOGIN, "true");
        props.setProperty(SDK42QueryProcessor.PROPERTY_STATIC_LOGIN_USER, "fakeuser");
        props.setProperty(SDK42QueryProcessor.PROPERTY_STATIC_LOGIN_PASS, "fakepass");
        try {
            queryProcessor.initialize(props, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error initializing query processor: " + ex.getMessage());
        }
    }
    
    
    public void testInitializeRemoteApiWithGridIdent() {
        Properties props = queryProcessor.getRequiredParameters();
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API, "false");
        props.setProperty(SDK42QueryProcessor.PROPERTY_USE_GRID_IDENTITY_LOGIN, "true");
        try {
            queryProcessor.initialize(props, null);
            fail("Should have errored initializing remote API with grid ident login");
        } catch (Exception ex) {
            // expected
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(QueryProcessorInitializationTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
