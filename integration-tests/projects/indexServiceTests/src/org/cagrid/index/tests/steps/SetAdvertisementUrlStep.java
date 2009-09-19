package org.cagrid.index.tests.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.axis.message.addressing.EndpointReferenceType;

public class SetAdvertisementUrlStep extends Step {
    public static final String DEPLOY_PROPERTIES_FILENAME = "deploy.properties";
    public static final String INDEX_URL_PROPERTY = "index.service.url";
    
    private File serviceDir = null;
    private EndpointReferenceType indexServiceEPR = null;
    
    public SetAdvertisementUrlStep(File serviceDir, EndpointReferenceType indexServiceEPR) {
        this.serviceDir = serviceDir;
        this.indexServiceEPR = indexServiceEPR;
    }
    

    public void runStep() throws Throwable {
        File deployPropertiesFile = new File(serviceDir, DEPLOY_PROPERTIES_FILENAME);
        assertTrue("Deploy properties file not found", deployPropertiesFile.exists());
        Properties props = new Properties();
        FileInputStream propsInput = new FileInputStream(deployPropertiesFile);
        props.load(propsInput);
        propsInput.close();
        String indexUrl = indexServiceEPR.getAddress().toString();
        System.out.println("SET INDEX SERVICE URL FOR REGISTRATION:\n" + indexUrl);
        props.setProperty(INDEX_URL_PROPERTY, indexUrl);
        FileOutputStream propsOutput = new FileOutputStream(deployPropertiesFile);
        props.store(propsOutput, "Edited by " + SetAdvertisementUrlStep.class.getName());
        propsOutput.flush();
        propsOutput.close();
    }
}
