package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.StringWriter;

import junit.framework.TestCase;

/** 
 *  BaseXmiToModelTest
 *  Base test for XMI to Domain Model capability
 * 
 * @author David Ervin
 * 
 * @created Oct 23, 2007 10:20:00 AM
 * @version $Id: BaseXmiToModelTest.java,v 1.1 2007-10-23 14:54:22 dervin Exp $ 
 */
public abstract class BaseXmiToModelTest extends TestCase {

    public BaseXmiToModelTest(String name) {
        super(name);
    }
    
    
    public abstract XmiToModelInfo getInfo();
    
    
    public void testXmiToDomainModel() {
        XmiToModelInfo info = getInfo();
        
        File xmiFile = new File(info.getXmiFilename());
        XMIParser parser = new XMIParser(info.getProjectShortName(), info.getProjectVersion());
        
        DomainModel model = null;
        try {
            model = parser.parse(xmiFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error parsing XMI file (" + xmiFile.getAbsolutePath() + "):" + ex.getMessage());
        }
        
        StringWriter modelWriter = new StringWriter();
        try {
            MetadataUtils.serializeDomainModel(model, modelWriter);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error serializing domain model: " + ex.getMessage());
        }
        
        File goldModelFile = new File(info.getGoldDomainModelFilename());
        
        String goldXml = null;
        try {
            goldXml = Utils.fileToStringBuffer(goldModelFile).toString().trim();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading gold XML: " + ex.getMessage());
        }
        String modelXml = modelWriter.getBuffer().toString().trim();
        
        assertEquals("Generted domain model does not match gold XML", goldXml, modelXml);
    }
}
