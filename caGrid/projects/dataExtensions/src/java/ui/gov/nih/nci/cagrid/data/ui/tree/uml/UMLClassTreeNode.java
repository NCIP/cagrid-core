package gov.nih.nci.cagrid.data.ui.tree.uml;

import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTree;
import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTreeNode;

/** 
 *  UMLCLassTreeNode
 *  Tree node to represent a UML Class from the caDSR
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 5, 2006 
 * @version $Id: UMLClassTreeNode.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class UMLClassTreeNode extends CheckBoxTreeNode {
	private String className;

	public UMLClassTreeNode(CheckBoxTree parentTree, String className) {
		super(parentTree, className);
		this.className = className;
		this.setAllowsChildren(false);
	}
	
	
	public String getClassName() {
		return className;
	}
}
