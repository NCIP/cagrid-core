package gov.nih.nci.cagrid.data.ui.tree.uml;

import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTree;
import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTreeNode;

/** 
 *  UMLPackageTreeNode
 *  Node to show and manage UML package metadata from the caDSR
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 5, 2006 
 * @version $Id: UMLPackageTreeNode.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class UMLPackageTreeNode extends CheckBoxTreeNode {
	private String packageName;

	public UMLPackageTreeNode(CheckBoxTree parentTree, String packageName) {
		super(parentTree, packageName);
		this.packageName = packageName;
	}
	
	
	public String getPackageName() {
		return packageName;
	}
	
	
	public String[] getSelectedClasses() {
		CheckBoxTreeNode[] checked = getCheckedChildren();
		String[] classes = new String[checked.length];
		for (int i = 0; i < checked.length; i++) {
			classes[i] = ((UMLClassTreeNode) checked[i]).getClassName();
		}
		return classes;
	}
}
