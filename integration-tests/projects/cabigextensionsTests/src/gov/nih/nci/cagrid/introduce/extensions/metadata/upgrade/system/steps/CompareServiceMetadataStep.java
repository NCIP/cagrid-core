package gov.nih.nci.cagrid.introduce.extensions.metadata.upgrade.system.steps;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;

import java.io.File;
import java.io.FileReader;


/**
 * CompareServiceMetadataStep Compares every description tag is the same between
 * two metadata documents (this implicitly also verifies things such as the
 * number of services, operations, inputs, outputs, and faults are the same)
 * 
 * @author oster
 * @created Apr 11, 2007 3:38:47 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class CompareServiceMetadataStep extends XPathValidatingStep {

    // TODO: could use this if the UMLClass stuff still worked for caCORE
    // private static final String DESCRIPTION_XPATH = "//*/@description";
    private static final String DESCRIPTION_XPATH = "//*[local-name()='service' or local-name()='serviceContext'"
        + " or (local-name()='operation' and @name!='GetMultipleResourceProperties' and @name!='GetResourceProperty' and @name!='QueryResourceProperties') or local-name()='fault'  or local-name()='contextProperty']/@description";
    protected File origServiceMetadata;
    protected File newServiceMetadata;


    public CompareServiceMetadataStep(File origServiceMetadata, File newServiceMetadata) throws Exception {
        this.newServiceMetadata = newServiceMetadata;
        this.origServiceMetadata = origServiceMetadata;
    }


    @Override
    public void runStep() throws Throwable {
        assertTrue("Original file (" + this.origServiceMetadata + ") does not exist", this.origServiceMetadata.exists());
        assertTrue("New file (" + this.newServiceMetadata + ") does not exist", this.newServiceMetadata.exists());

        ServiceMetadata origMD = MetadataUtils.deserializeServiceMetadata(new FileReader(this.origServiceMetadata));
        ServiceMetadata newMD = MetadataUtils.deserializeServiceMetadata(new FileReader(this.newServiceMetadata));

        // make sure all the descriptions are the same

        System.out.println("Comparing all descriptions:");
        assertStringIteratorsEqual(createIterator(origMD, DESCRIPTION_XPATH), createIterator(newMD, DESCRIPTION_XPATH));

    }
}