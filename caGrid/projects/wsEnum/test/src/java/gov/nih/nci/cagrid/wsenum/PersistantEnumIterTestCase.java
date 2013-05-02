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
package gov.nih.nci.cagrid.wsenum;

import gov.nih.nci.cagrid.wsenum.utils.PersistantObjectIterator;

/** 
 *  PersistantEnumIterTestCase
 *  Test case to test the persistent (complicated) enumeration iterator
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Nov 3, 2006 
 * @version $Id$ 
 */
public class PersistantEnumIterTestCase extends CompleteEnumIteratorBaseTest {
	
	public PersistantEnumIterTestCase() {
		super(PersistantObjectIterator.class.getName());
	}
}
