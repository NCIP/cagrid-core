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
package gov.nih.nci.cagrid.introduce.test.unit;

import gov.nih.nci.cagrid.introduce.codegen.services.methods.SyncHelper;
import junit.framework.TestCase;

public class SyncSourceTestCase extends TestCase {

	public void testRemoveMultiNewLines() {
		assertEquals("abc", SyncHelper.removeMultiNewLines("abc"));
		assertEquals("abc\n\n", SyncHelper.removeMultiNewLines("abc\n\n"));
		assertEquals("\n\na\nb\n\nc\n\n", 
            SyncHelper.removeMultiNewLines("\n\na\nb\n\nc\n\n"));
		assertEquals("\n\na\nb\n\nc\n\n", 
            SyncHelper.removeMultiNewLines("\n\na\nb\n\nc\n\n\n"));
		assertEquals("\n\na\nb\n\nc\n\n", 
            SyncHelper.removeMultiNewLines("\n\na\nb\n\nc\n\n\n\n"));
		assertEquals("\n\na\nb\n\nc\n\n", SyncHelper
				.removeMultiNewLines("\n\na\nb\n\nc\n\n\n\n\n"));
		assertEquals("\n\na\nb\n\nc\n\n", SyncHelper
				.removeMultiNewLines("\n\na\nb\n\nc\n\n\n\n\n\n"));
		assertEquals("\n\na\nb\n\nc\n\n", SyncHelper
				.removeMultiNewLines("\n\n\n\n\na\nb\n\nc\n\n\n\n\n\n"));
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SyncSourceTestCase.class);
	}
}
