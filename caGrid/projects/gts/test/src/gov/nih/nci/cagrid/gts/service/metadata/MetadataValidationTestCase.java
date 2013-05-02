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
package gov.nih.nci.cagrid.gts.service.metadata;

import gov.nih.nci.cagrid.core.SchemaValidationTestCase;

import java.io.File;


/**
 * @author oster
 */
public class MetadataValidationTestCase extends SchemaValidationTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nih.nci.cagrid.cadsr.metadata.SchemaValidationTestCase#getSchemaFilename()
	 */
	public String getSchemaFilename() {
		return "schema" + File.separator + "GTS" + File.separator + "xsd" + File.separator + "cagrid" + File.separator
			+ "types" + File.separator + "caGridMetadata.xsd";
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nih.nci.cagrid.cadsr.metadata.SchemaValidationTestCase#getXMLFilename()
	 */
	public String getXMLFilename() {
		return "etc" + File.separator + "serviceMetadata.xml";
	}

}
