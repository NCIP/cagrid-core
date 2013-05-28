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
package org.cagrid.fqp.test.common.steps;

import gov.nih.nci.cagrid.common.PropertiesPreservingComments;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Edits the service.properties file of an Introduce
 * generated data service to clear out the CQL 2
 * query processor class name property, thereby disabling
 * the CQL 2 functionality of a data service
 * 
 * @author David W. Ervin
 */
public class DisableCql2QueryProcessorStep extends Step {
    
    private String baseDir = null;

    public DisableCql2QueryProcessorStep(String baseDir) {
        super();
        this.baseDir = baseDir;
    }


    public void runStep() throws Throwable {
        File servicePropsFile = new File(baseDir, "service.properties");
        assertTrue("Service properties file (" + servicePropsFile.getAbsolutePath() + ") not found", servicePropsFile.exists());
        PropertiesPreservingComments props = new PropertiesPreservingComments();
        props.load(servicePropsFile);
        props.setProperty(QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY, "");
        FileOutputStream out = new FileOutputStream(servicePropsFile);
        props.store(out);
        out.flush();
        out.close();
    }
}
