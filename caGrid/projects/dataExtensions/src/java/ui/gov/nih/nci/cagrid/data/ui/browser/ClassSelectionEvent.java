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
package gov.nih.nci.cagrid.data.ui.browser;

import java.util.EventObject;

/** 
 *  ClassSelectionEvent
 *  Event fired when a class is selected in the class browser panel
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 12, 2006 
 * @version $Id: ClassSelectionEvent.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class ClassSelectionEvent extends EventObject {

	public ClassSelectionEvent(ClassBrowserPanel source) {
		super(source);
	}
}
