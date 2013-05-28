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
package gov.nih.nci.cagrid.testing.system.deployment.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class SetIndexRegistrationStep extends Step {
    
    private String serviceDir = null;
    private boolean enableRegistration;
    
    public SetIndexRegistrationStep(String serviceBaseDir, boolean enableRegistration) {
        this.serviceDir = serviceBaseDir;
        this.enableRegistration = enableRegistration;
    }
    

    public void runStep() throws Throwable {
        try {
            File propsFile = new File(serviceDir, "deploy.properties");
            FileInputStream deployPropertiesInput = new FileInputStream(propsFile);
            Properties props = new Properties();
            props.load(deployPropertiesInput);
            deployPropertiesInput.close();
            props.setProperty("perform.index.service.registration", String.valueOf(enableRegistration));
            FileOutputStream propsOutput = new FileOutputStream(propsFile);
            props.store(propsOutput, "Edited by " + getClass().getName());
            propsOutput.flush();
            propsOutput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error editing deploy.properties");
        }
    }
}
