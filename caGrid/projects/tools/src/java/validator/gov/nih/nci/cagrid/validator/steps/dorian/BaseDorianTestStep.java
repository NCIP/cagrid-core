package gov.nih.nci.cagrid.validator.steps.dorian;

import gov.nih.nci.cagrid.validator.steps.AbstractBaseServiceTestStep;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.dorian.client.DorianClient;


/**
 * BaseDorianTestStep 
 * Base step for testing dorian
 * 
 * @author David Ervin
 * 
 * @created Aug 27, 2007 4:04:29 PM
 * @version $Id: BaseDorianTestStep.java,v 1.2 2008-06-17 19:33:11 langella Exp $
 */
public abstract class BaseDorianTestStep extends AbstractBaseServiceTestStep {
    
    private DorianClient dorianClient = null;

    
    public BaseDorianTestStep(String serviceURL, File tempDir, Properties configuration) {
        super(serviceURL, tempDir, configuration);
    }


    public abstract void runStep() throws Throwable;
    
    
    protected DorianClient getDorianClient() {
        if (dorianClient == null) {
            try {
                dorianClient = new DorianClient(getServiceUrl());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                fail("Error creating Dorian Service Client: " + ex.getMessage());
            } catch (MalformedURIException ex) {
                ex.printStackTrace();
                fail("The service URL was not valid: " + ex.getMessage());
            }
        }
        return dorianClient;
    }
}