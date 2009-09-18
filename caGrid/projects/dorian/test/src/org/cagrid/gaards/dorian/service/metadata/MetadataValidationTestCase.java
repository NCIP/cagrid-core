package org.cagrid.gaards.dorian.service.metadata;

import gov.nih.nci.cagrid.core.SchemaValidationTestCase;

import java.io.File;


/**
 * @author oster
 */
public class MetadataValidationTestCase extends SchemaValidationTestCase {

    /*
     * (non-Javadoc)
     * @see
     * gov.nih.nci.cagrid.cadsr.metadata.SchemaValidationTestCase#getSchemaFilename
     * ()
     */
    public String getSchemaFilename() {
        return "schema" + File.separator + "Dorian" + File.separator + "xsd" + File.separator + "cagrid"
            + File.separator + "types" + File.separator + "caGridMetadata.xsd";
    }


    /*
     * (non-Javadoc)
     * @see
     * gov.nih.nci.cagrid.cadsr.metadata.SchemaValidationTestCase#getXMLFilename
     * ()
     */
    public String getXMLFilename() {
        return "etc" + File.separator + "serviceMetadata.xml";
    }

}
