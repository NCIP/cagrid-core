package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;
import java.io.FilenameFilter;


public class ValidateWSDLStep extends BaseStep {
    private TestCaseInfo tci;


    public ValidateWSDLStep(TestCaseInfo tci, boolean build) throws Exception {
        super(tci.getDir(), build);
        this.tci = tci;
    }


    public void runStep() throws Throwable {
        System.out.println("Validating WSDLs");

        File wsdlSchema = new File(Utils.decodeUrl(this.getClass().getResource("/schema/wsdl.xsd")));
        assertTrue("Unable to locate WSDL XSD", wsdlSchema.exists() && wsdlSchema.canRead());

        File schemaDir = new File(getBaseDir() + File.separator + tci.getDir() + File.separator + "schema"
            + File.separator + tci.getName());

        File[] wsdlFiles = schemaDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wsdl");
            }
        });
        // be sure we found at least one wsdl file
        assertTrue(wsdlFiles.length > 0);

        SchemaValidator validator = new SchemaValidator(wsdlSchema.toURI().toString());

        for (File wsdl : wsdlFiles) {
            try {
                System.out.println("Validating WSDL:"+wsdl);
                validator.validate(wsdl);
            } catch (SchemaValidationException e) {
                e.printStackTrace();
                fail("Schema validation for (" + wsdl + ") failed:" + e.getMessage());
            }
        }

    }
}
