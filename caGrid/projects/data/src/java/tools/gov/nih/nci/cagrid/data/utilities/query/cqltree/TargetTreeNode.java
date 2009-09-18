package gov.nih.nci.cagrid.data.utilities.query.cqltree;

import gov.nih.nci.cagrid.cqlquery.Object;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/** 
 *  TargetTreeNode
 *  Tree node to represent the target level object in a CQL Query
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public class TargetTreeNode extends IconTreeNode {
	private static Icon icon = null;
	
	private Object targetObject;
	
	public TargetTreeNode(Object target) {
		this.targetObject = target;
		rebuild();
	}
	
	
	public Object getTarget() {
		return this.targetObject;
	}
	

	public Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(getClass().getResource("QueryTarget.png"));
		}
		return icon;
	}
	
	
	public void rebuild() throws IllegalStateException {
		setText();
		removeAllChildren();
		int childCount = countTargetChildren();
		if (childCount == 1) {
			// figure out which child and update
			IconTreeNode node = null;
			if (targetObject.getAssociation() != null) {
				node = new AssociationTreeNode(targetObject.getAssociation());
			}
			if (targetObject.getAttribute() != null) {
				node = new AttributeTreeNode(targetObject.getAttribute());
			}
			if (targetObject.getGroup() != null) {
				node = new GroupTreeNode(targetObject.getGroup());
			}
			add(node);
		} else if (childCount > 1) {
			throw new IllegalStateException("Target has more than one child!");
		}
	}
	
	
	private int countTargetChildren() {
		int childCount = 0;
		if (targetObject.getAssociation() != null) {
			childCount++;
		}
		if (targetObject.getAttribute() != null) {
			childCount++;
		}
		if (targetObject.getGroup() != null) {
			childCount++;
		}
		return childCount;
	}
	
	
	private void setText() {
		setUserObject("Target: " + targetObject.getName());
	}
}
