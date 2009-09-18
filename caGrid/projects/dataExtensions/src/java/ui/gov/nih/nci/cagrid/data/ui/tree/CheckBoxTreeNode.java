package gov.nih.nci.cagrid.data.ui.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.tree.DefaultMutableTreeNode;

/** 
 *  CheckBoxTreeNode
 *  Tree node that has a check box
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 20, 2006 
 * @version $Id: CheckBoxTreeNode.java,v 1.3 2008-01-02 19:32:08 dervin Exp $ 
 */
public class CheckBoxTreeNode extends DefaultMutableTreeNode {

	private CheckBoxTree parentTree;
	private JCheckBox check;
	
	public CheckBoxTreeNode(CheckBoxTree parentTree, String nodeText) {
		super();
		this.parentTree = parentTree;
		setUserObject(nodeText);
	}
	
	
	public JCheckBox getCheckBox() {
		if (check == null) {
			check = new JCheckBox((String) getUserObject());
			// add listener to turn all children's check boxes on / off
			check.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int childCount = getChildCount();
					for (int i = 0; i < childCount; i++) {
						CheckBoxTreeNode node = (CheckBoxTreeNode) getChildAt(i);
						node.getCheckBox().setSelected(isChecked());
						parentTree.getTreeModel().nodeChanged(node);
					}
				}
			});
			check.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					// repaint the node when it changes
					parentTree.getTreeModel().nodeChanged(CheckBoxTreeNode.this);
					// inform everyone that a node has been checked / unchecked
					if (check.isSelected()) {
						parentTree.fireNodeChecked(CheckBoxTreeNode.this);
					} else {
						parentTree.fireNodeUnchecked(CheckBoxTreeNode.this);
					}
				}
			});
		}
		return check;
	}
	
	
	public boolean isChecked() {
		return getCheckBox().isSelected();
	}
	
	
	public CheckBoxTreeNode[] getCheckedChildren() {
		List<CheckBoxTreeNode> checked = new ArrayList<CheckBoxTreeNode>();
		Enumeration childEnum = children();
		while (childEnum.hasMoreElements()) {
			Object node = childEnum.nextElement();
			if (node instanceof CheckBoxTreeNode && ((CheckBoxTreeNode) node).isChecked()) {
				checked.add((CheckBoxTreeNode) node);
			}
		}
		CheckBoxTreeNode[] checkedNodes = new CheckBoxTreeNode[checked.size()];
		checked.toArray(checkedNodes);
		return checkedNodes;
	}
	
	
	public boolean allChildrenChecked() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			CheckBoxTreeNode node = (CheckBoxTreeNode) getChildAt(i);
			if (!node.isChecked()) {
				return false;
			}
		}
		return true;
	}
	
	
	public boolean noChildrenChecked() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			CheckBoxTreeNode node = (CheckBoxTreeNode) getChildAt(i);
			if (node.isChecked()) {
				return false;
			}
		}
		return true;
	}
}
