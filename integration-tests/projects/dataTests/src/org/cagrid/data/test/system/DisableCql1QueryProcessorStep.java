package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

public class DisableCql1QueryProcessorStep extends Step {
    
    private String serviceDir = null;

    public DisableCql1QueryProcessorStep(String serviceDir) {
        super();
        this.serviceDir = serviceDir;
    }


    public void runStep() throws Throwable {
        // get the service model
        String serviceModelFile = serviceDir + File.separator + IntroduceConstants.INTRODUCE_XML_FILE;
        ServiceDescription desc = null;
        try {
            desc = Utils.deserializeDocument(serviceModelFile, ServiceDescription.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading service description: " + ex.getMessage());
        }
        if (desc == null) {
            throw new NullPointerException("Service description is NULL!");
        }
        
        // blank the query processor service property
        CommonTools.setServiceProperty(desc, QueryProcessorConstants.QUERY_PROCESSOR_CLASS_PROPERTY, "", false);
        
        // serialize the service model back to disk
        Utils.serializeDocument(serviceModelFile, desc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
}
