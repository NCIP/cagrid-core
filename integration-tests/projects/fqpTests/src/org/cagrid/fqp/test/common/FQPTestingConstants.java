/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.fqp.test.common;

import java.io.File;

public interface FQPTestingConstants {

    /**
     * A wsdd file with the types used in the test data services configured
     */
    public static final String CLIENT_WSDD = "/resources/wsdd/client-config.wsdd";
    
    /**
     * The location of testing DCQL 1 queries
     */
    public static final String QUERIES_LOCATION = 
        "resources" + File.separator + "queries" + File.separator + "dcql1" + File.separator;
    
    /**
     * The location of expected query results for the DCQL 1 queries
     */
    public static final String GOLD_LOCATION = 
        "resources" + File.separator + "gold" + File.separator + "dcql1" + File.separator;
    
    /**
     * The location of testing DCQL 2 queries
     */
    public static final String DCQL2_QUERIES_LOCATION =
        "resources" + File.separator + "queries" + File.separator + "dcql2" + File.separator;
    
    
    /**
     * The location of expected query results for the DCQL 2 queries
     */
    public static final String DCQL2_GOLD_LOCATION = 
        "resources" + File.separator + "gold" + File.separator + "dcql2" + File.separator;
    
    /**
     * Controlled by a property in jndi-config.xml
     */
    public static final long RESOURCE_SWEEPER_DELAY = 2000;
    
    /**
     * Number of times to try the isProcessingComplete() method
     */
    public static final int PROCESSING_WAIT_RETRIES = 20;
    
    /**
     * ms delay between successive calls to isProcessingComplete()
     */
    public static final long PROCESSING_RETRY_DELAY = 500;
    
    /**
     * Default directory (relative to this one's base directory)
     * where the caGrid projects reside
     */
    public static final String DEFAULT_CAGRID_PROJECTS_DIR = "/../../../caGrid/projects";
    
    /**
     * System property which points to the FQP service directory
     */
    public static final String FQP_DIR_PROPERTY = "fqp.service.dir";
    
    /**
     * The default project directory for FQP
     */
    public static final String DEFAULT_FQP_DIR = DEFAULT_CAGRID_PROJECTS_DIR + File.separator + "fqp";
    
    /**
     * System property which points to the Transfer service directory
     */
    public static final String TRANSFER_SERVICE_DIR_PROPERTY = "transfer.service.dir";
    
    /**
     * The default project directory for Transfer
     */
    public static final String DEFAULT_TRANSFER_DIR = DEFAULT_CAGRID_PROJECTS_DIR + File.separator + "transfer";
    
    /**
     * System property which points to the CDS service directory
     */
    public static final String CDS_SERVICE_DIR_PROPERTY = "cds.service.dir";
    
    /**
     * The default project directory for CDS
     */
    public static final String DEFAULT_CDS_DIR = DEFAULT_CAGRID_PROJECTS_DIR + File.separator + "cds";
}
