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
package gov.nih.nci.cagrid.data.ui.tree.types;

import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTreeNode;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;


/** 
 *  TypeTreeNode
 *  Node in the TargetTypesTree to represent and allow selection of 
 *  schema element types
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 20, 2006 
 * @version $Id: TypeTreeNode.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class TypeTreeNode extends CheckBoxTreeNode {
	
	private SchemaElementType type;
	
	public TypeTreeNode(TargetTypesTree tree, SchemaElementType type) {
		super(tree, type.getType());
		setAllowsChildren(false);
		this.type = type;
	}
	
	
	public SchemaElementType getType() {
		return type;
	}
}
