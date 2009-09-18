package gov.nih.nci.cagrid.metadata.xmi;

import java.io.FileReader;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.xmi.DomainModelComparator.DomainModelComparisonResult;
import gov.nih.nci.cagrid.testing.system.haste.Step;

/** 
 *  GenericDomainModelComparisonStep
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Apr 4, 2008 12:45:18 PM
 * @version $Id: GenericDomainModelComparisonStep.java,v 1.1 2008-04-04 16:54:32 dervin Exp $ 
 */
public class GenericDomainModelComparisonStep extends Step {
    
    private String testModelFilename;
    private String goldModelFilename;
    
    public GenericDomainModelComparisonStep(String goldModelFilename, String testModelFilename) {
        this.goldModelFilename = goldModelFilename;
        this.testModelFilename = testModelFilename;
    }
    

    public void runStep() throws Throwable {
        DomainModel goldModel = null;
        try {
            FileReader reader = new FileReader(goldModelFilename);
            goldModel = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing gold domain model: " + ex.getMessage());    
        }
        
        DomainModel testModel = null;
        try {
            FileReader reader = new FileReader(testModelFilename);
            testModel = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing test domain model: " + ex.getMessage());    
        }
        
        DomainModelComparisonResult result = DomainModelComparator.testModelEquivalence(goldModel, testModel);
        assertTrue(result.getMessage(), result.modelsAreEqual());
    }
}
