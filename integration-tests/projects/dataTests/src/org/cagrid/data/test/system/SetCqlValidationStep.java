package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.cagrid.data.test.creation.DataTestCaseInfo;

/**
 * SetCqlValidationStep
 * Disables CQL validation at query runtime
 * 
 * @author David
 */
public class SetCqlValidationStep extends Step {
    
    private DataTestCaseInfo serviceInfo = null;
    private boolean validateStructure;
    private boolean validateModel;
    
    public SetCqlValidationStep(DataTestCaseInfo serviceInfo, boolean validateStructure, boolean validateModel) {
        this.serviceInfo = serviceInfo;
        this.validateStructure = validateStructure;
        this.validateModel = validateModel;
    }
    

    public void runStep() throws Throwable {
        File propertiesFile = new File(serviceInfo.getDir(), "service.properties");
        assertTrue("Service properties file not found", propertiesFile.exists());
        Properties props = new Properties();
        FileInputStream propertiesInput = new FileInputStream(propertiesFile);
        props.load(propertiesInput);
        props.setProperty(DataServiceConstants.VALIDATE_CQL_FLAG, String.valueOf(validateStructure));
        props.setProperty(DataServiceConstants.VALIDATE_DOMAIN_MODEL_FLAG, String.valueOf(validateModel));
        propertiesInput.close();
        FileOutputStream propertiesOutput = new FileOutputStream(propertiesFile);
        props.store(propertiesOutput, "Properties edited by " + SetCqlValidationStep.class.getName());
        propertiesOutput.flush();
        propertiesOutput.close();
    }
}
