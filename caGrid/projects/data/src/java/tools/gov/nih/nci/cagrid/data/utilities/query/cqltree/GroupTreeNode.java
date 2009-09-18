package gov.nih.nci.cagrid.data.utilities.query.cqltree;

import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.Group;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/** 
 *  GroupTreeNode
 *  Tree node to represent a Group in a CQL Query
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public class GroupTreeNode extends IconTreeNode {
	private static Icon icon = null;
	
	private Group group;
	
	public GroupTreeNode(Group group) {
		this.group = group;
		rebuild();
	}
	
	
	public Group getGroup() {
		return this.group;
	}
	

	public Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(getClass().getResource("QueryGroup.png"));
		}
		return icon;
	}
	
	
	public void rebuild() throws IllegalStateException {
		setText();
		removeAllChildren();
		if (group.getAssociation() != null) {
			for (int i = 0; i < group.getAssociation().length; i++) {
				Association assoc = group.getAssociation(i);
				AssociationTreeNode node = new AssociationTreeNode(assoc);
				add(node);
			}
		}
		if (group.getAttribute() != null) {
			for (int i = 0; i < group.getAttribute().length; i++) {
				Attribute attrib = group.getAttribute(i);
				AttributeTreeNode node = new AttributeTreeNode(attrib);
				add(node);
			}
		}
		if (group.getGroup() != null) {
			for (int i = 0; i < group.getGroup().length; i++) {
				Group g = group.getGroup(i);
				GroupTreeNode node = new GroupTreeNode(g);
				add(node);
			}
		}
	}
	
	
	private void setText() {
		setUserObject("Group: " + group.getLogicRelation().getValue());
	}
}
