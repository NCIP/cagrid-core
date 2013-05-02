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
package gov.nih.nci.cagrid.core;

import java.io.File;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import junit.framework.TestCase;


/**
 * Validates a given XML file against a given XML Schema.
 * 
 * @author oster
 */
public abstract class SchemaValidationTestCase extends TestCase {

	protected SchemaValidator validator = null;


	protected void setUp() throws Exception {
		super.setUp();

		assertNotNull(getSchemaFilename());
		File schemaFile = new File(getSchemaFilename());
		assertTrue(schemaFile.exists());
		assertTrue(schemaFile.canRead());

		assertNotNull(getXMLFilename());
		File xmlFile = new File(getXMLFilename());
		assertTrue(xmlFile.exists());
		assertTrue(xmlFile.canRead());

		validator = new SchemaValidator(getSchemaFilename());
		assertNotNull(validator);
	}


	public void testValidation() {
		try {
			validator.validate(new File(getXMLFilename()));
		} catch (SchemaValidationException e) {
			fail(e.getMessage());
		}
	}


	/**
	 * The schema to validate with.
	 * 
	 * @return The schema to validate with.
	 */
	public abstract String getSchemaFilename();


	/**
	 * The XML file to validate.
	 * 
	 * @return The XML file to validate.
	 */
	public abstract String getXMLFilename();
}
