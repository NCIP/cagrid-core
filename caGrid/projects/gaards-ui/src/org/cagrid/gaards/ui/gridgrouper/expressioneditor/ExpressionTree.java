
package org.cagrid.gaards.ui.gridgrouper.expressioneditor;

import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.cagrid.gaards.ui.gridgrouper.tree.TreeRenderer;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ExpressionTree extends JTree {
	
	private static final long serialVersionUID = 1L;

	private ExpressionNode rootNode;


	public ExpressionTree(GridGrouperExpressionEditor editor, MembershipExpression expression) {
		super();
		setLargeModel(true);
		this.rootNode = new ExpressionNode(editor, expression, true);
		setModel(new DefaultTreeModel(this.rootNode));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addMouseListener(new ExpressionTreeEventListener(this, editor));
		this.setCellRenderer(new TreeRenderer());
	}


	public ExpressionNode getRootNode() {
		return this.rootNode;
	}


	public BaseTreeNode getCurrentNode() {
		TreePath currentSelection = this.getSelectionPath();
		if (currentSelection != null) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentSelection.getLastPathComponent();
			return (BaseTreeNode) currentNode;
		}
		return null;
	}


	/**
	 * Get all the selected service nodes
	 * 
	 * @return A List of GridServiceTreeNodes
	 */
	public List getSelectedNodes() {
		List selected = new LinkedList();
		TreePath[] currentSelection = this.getSelectionPaths();
		if (currentSelection != null) {
			for (int i = 0; i < currentSelection.length; i++) {
				TreePath path = currentSelection[i];
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				if (currentNode != this.getRootNode()) {
					selected.add(currentNode);
				}
			}
		}
		return selected;
	}


	/**
	 * Reload a portion of the tree's view in a synchronized way
	 * 
	 * @param reloadPoint
	 *            The node from which to reload
	 */
	public synchronized void reload(TreeNode reloadPoint) {
		TreePath currentSelection = this.getSelectionPath();
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		model.reload(reloadPoint);
		this.setSelectionPath(currentSelection);
	}


	/**
	 * Reload from the root
	 */
	public synchronized void reload() {
		this.reload(getRootNode());
	}

}
