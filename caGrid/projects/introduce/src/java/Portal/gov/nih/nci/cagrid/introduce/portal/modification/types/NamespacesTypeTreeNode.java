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

package gov.nih.nci.cagrid.introduce.portal.modification.types;

import javax.swing.tree.DefaultMutableTreeNode;

/** 
 *  Node for representing namepspace
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * 
 * @created Nov 22, 2004 
 * @version $Id: NamespacesTypeTreeNode.java,v 1.4 2006-04-05 18:17:49 hastings Exp $ 
 */
public class NamespacesTypeTreeNode extends DefaultMutableTreeNode {

	public NamespacesTypeTreeNode() {
		super();
		this.setUserObject("Data Types");
	}
	
	public String toString(){
		return this.getUserObject().toString();
	}
}
