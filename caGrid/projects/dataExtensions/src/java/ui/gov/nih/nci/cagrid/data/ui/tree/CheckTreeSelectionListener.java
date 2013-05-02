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
package gov.nih.nci.cagrid.data.ui.tree;

/** 
 *  CheckTreeSelectionListener
 *  Listener for node check / uncheck events
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 21, 2006 
 * @version $Id: CheckTreeSelectionListener.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public interface CheckTreeSelectionListener {

	public void nodeChecked(CheckTreeSelectionEvent e);
	
	
	public void nodeUnchecked(CheckTreeSelectionEvent e);
}
