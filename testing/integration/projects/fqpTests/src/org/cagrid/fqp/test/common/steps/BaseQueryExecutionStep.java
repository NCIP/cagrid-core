package org.cagrid.fqp.test.common.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.cagrid.fqp.test.common.FQPTestingConstants;

public abstract class BaseQueryExecutionStep extends Step {
    
    private String queryFilename;
    private String goldFilename;
    
    public BaseQueryExecutionStep(String queryFilename, String goldFilename) {
        this.queryFilename = queryFilename;
        this.goldFilename = goldFilename;
    }
    

    protected DCQLQuery deserializeQuery() {
        DCQLQuery query = null;
        FileReader reader = null;
        try {
            reader = new FileReader(queryFilename);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            fail("Unable to read query file " + queryFilename);
        }
        try {
            query = (DCQLQuery) Utils.deserializeObject(reader, DCQLQuery.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to deserialize query file " + queryFilename);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                // nothing else to do
            }
        }
        return query;
    }
    
    
    protected CQLQueryResults loadGoldCqlResults() {
        CQLQueryResults goldResults = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(goldFilename);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            fail("Unable to read gold results file " + goldFilename);
        }
        try {
            InputStream wsddStream = getClass().getResourceAsStream(FQPTestingConstants.CLIENT_WSDD);
            assertNotNull("Could not locate client-config.wsdd", wsddStream);
            goldResults = (CQLQueryResults) Utils.deserializeObject(
                new InputStreamReader(fis), CQLQueryResults.class, wsddStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to deserialize gold results " + goldFilename);
        } finally {
            try {
                fis.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                // we tried
            }
        }
        
        return goldResults;
    }
    
    
    protected DCQLQueryResultsCollection loadGoldDcqlResults() {
        DCQLQueryResultsCollection goldResults = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(goldFilename);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            fail("Unable to read gold results file " + goldFilename);
        }
        try {
            InputStream wsddStream = getClass().getResourceAsStream(FQPTestingConstants.CLIENT_WSDD);
            assertNotNull("Could not locate client-config.wsdd", wsddStream);
            goldResults = (DCQLQueryResultsCollection) Utils.deserializeObject(
                new InputStreamReader(fis), DCQLQueryResultsCollection.class, wsddStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to deserialize gold results " + goldFilename);
        } finally {
            try {
                fis.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                // we tried
            }
        }
        
        return goldResults;
    }
    
    
    protected String getQueryFilename() {
        return queryFilename;
    }
    
    
    protected String getGoldFilenname() {
        return goldFilename;
    }
}
