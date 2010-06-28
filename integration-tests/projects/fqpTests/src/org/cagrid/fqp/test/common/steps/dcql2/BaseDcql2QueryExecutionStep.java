package org.cagrid.fqp.test.common.steps.dcql2;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.cagrid.cql.utilities.CQL2SerializationUtil;
import org.cagrid.cql.utilities.DCQL2SerializationUtil;
import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.DistinctAttribute;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;

public abstract class BaseDcql2QueryExecutionStep extends Step {
    
    private String queryFilename;
    private String goldFilename;
    
    public BaseDcql2QueryExecutionStep(String queryFilename, String goldFilename) {
        this.queryFilename = queryFilename;
        this.goldFilename = goldFilename;
    }
    

    protected DCQLQuery deserializeQuery() {
        System.out.println("Deserializing " + queryFilename);
        DCQLQuery query = null;
        FileReader reader = null;
        try {
            reader = new FileReader(queryFilename);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            fail("Unable to read query file " + queryFilename);
        }
        try {
            query = DCQL2SerializationUtil.deserializeDcql2Query(reader);
            CQLQueryModifier mods = query.getQueryModifier();
            if (mods != null) {
                DistinctAttribute da = mods.getDistinctAttribute();
                if (da != null) {
                    Aggregation ag = da.getAggregation();
                    if (ag != null) {
                        System.out.println("Aggregation of " + ag.getValue());
                    } else {
                        System.out.println("No aggregation");
                    }
                } else {
                    System.out.println("No distinct attribute");
                }
            } else {
                System.out.println("No modifiers");
            }
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
        FileReader reader = null;
        try {
            reader = new FileReader(goldFilename);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            fail("Unable to read gold results file " + goldFilename);
        }
        try {
            goldResults = CQL2SerializationUtil.deserializeCql2QueryResults(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to deserialize gold results " + goldFilename);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                // we tried
            }
        }
        
        return goldResults;
    }
    
    
    protected DCQLQueryResultsCollection loadGoldDcqlResults() {
        DCQLQueryResultsCollection goldResults = null;
        FileReader reader = null;
        try {
            reader = new FileReader(goldFilename);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            fail("Unable to read gold results file " + goldFilename);
        }
        try {
            goldResults = DCQL2SerializationUtil.deserializeDcql2QueryResults(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to deserialize gold results " + goldFilename);
        } finally {
            try {
                reader.close();
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
