package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** 
 *  Sdk40XmiToModelStep
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Apr 4, 2008 12:31:53 PM
 * @version $Id: GenericXmiToModelStep.java,v 1.1 2008-04-04 16:54:32 dervin Exp $ 
 */
public class GenericXmiToModelStep extends Step {
    
    private String xmiInputFile;
    private String domainModelOutputFile;
    private XmiFileType xmiType;
    private String projectName;
    private String projectVersion;
    
    public GenericXmiToModelStep(String xmiInputFile, String domainModelOutputFile, XmiFileType xmiType,
        String projectName, String projectVersion) {
        this.xmiInputFile = xmiInputFile;
        this.domainModelOutputFile = domainModelOutputFile;
        this.xmiType = xmiType;
        this.projectName = projectName;
        this.projectVersion = projectVersion;
    }
    

    public void runStep() throws Throwable {
        File xmiFile = new File(xmiInputFile);
        assertTrue("Source XMI file did not exist", xmiFile.exists());
        XMIParser parser = new XMIParser(projectName, projectVersion);
        DomainModel model = null;
        try {
            model = parser.parse(xmiFile, xmiType);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error parsing XMI to Domain Model: " + ex.getMessage());
        }
        try {
            FileWriter writer = new FileWriter(domainModelOutputFile);
            MetadataUtils.serializeDomainModel(model, writer);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error writing Domain Model to disk: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error serializing domain model: " + ex.getMessage());
        }
    }
}
