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

import java.util.EventObject;

/** 
 *  CheckTreeSelectionEvent
 *  Event object for changes to check box selection
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 21, 2006 
 * @version $Id: CheckTreeSelectionEvent.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class CheckTreeSelectionEvent extends EventObject {
	private CheckBoxTreeNode node;
	
	public CheckTreeSelectionEvent(CheckBoxTree tree, CheckBoxTreeNode node) {
		super(tree);
		this.node = node;
	}
	
	
	public CheckBoxTree getTree() {
		return (CheckBoxTree) super.getSource();
	}
	
	
	public CheckBoxTreeNode getNode() {
		return node;
	}
}
