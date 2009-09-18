package gov.nih.nci.cagrid.data.utilities.query.cqltree;

import gov.nih.nci.cagrid.cqlquery.Association;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/** 
 *  AssociationTreeNode
 *  Tree node to represent an association in a CQL Query
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public class AssociationTreeNode extends IconTreeNode {
	private static Icon icon = null;
	
	private Association association;
	
	public AssociationTreeNode(Association assoc) {
		this.association = assoc;
		rebuild();
	}
	
	
	public Association getAssociation() {
		return this.association;
	}
	

	public Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(getClass().getResource("QueryAssociation.png")); 
		}
		return icon;
	}
	
	
	public void rebuild() throws IllegalStateException {
		setText();
		removeAllChildren();
		int childCount = countAssociationChildren();
		if (childCount == 1) {
			// figure out which child and update
			IconTreeNode node = null;
			if (association.getAssociation() != null) {
				node = new AssociationTreeNode(association.getAssociation());
			}
			if (association.getAttribute() != null) {
				node = new AttributeTreeNode(association.getAttribute());
			}
			if (association.getGroup() != null) {
				node = new GroupTreeNode(association.getGroup());
			}
			add(node);
		} else if (childCount > 1) {
			throw new IllegalStateException("Association has more than one child!");
		}
	}
	
	
	private int countAssociationChildren() {
		int childCount = 0;
		if (association.getAssociation() != null) {
			childCount++;
		}
		if (association.getAttribute() != null) {
			childCount++;
		}
		if (association.getGroup() != null) {
			childCount++;
		}
		return childCount;
	}
	
	
	private void setText() {
		setUserObject("Association: " + association.getRoleName() + " :: " + association.getName());
	}
}
