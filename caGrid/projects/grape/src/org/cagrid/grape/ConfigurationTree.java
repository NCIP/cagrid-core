package org.cagrid.grape;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ConfigurationTree extends JTree {

	private ConfigurationTreeNode rootNode;


	public ConfigurationTree(ConfigurationWindow window) throws Exception {
		super();
		this.rootNode = new ConfigurationTreeNode(window, this);
		setModel(new DefaultTreeModel(this.rootNode));
		this.rootNode.addToDisplay();
		this.addMouseListener(new ConfigurationTreeEventListener(this));
		this.setCellRenderer(new ConfigurationTreeRenderer());
	}


	public ConfigurationTreeNode getRootNode() {
		return this.rootNode;
	}


	public ConfigurationBaseTreeNode getCurrentNode() {
		TreePath currentSelection = this.getSelectionPath();
		if (currentSelection != null) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentSelection.getLastPathComponent();
			return (ConfigurationBaseTreeNode) currentNode;
		}
		return null;
	}


	/**
	 * Get all the selected service nodes
	 * 
	 * @return A List of GridServiceTreeNodes
	 */
	public List getSelectedNodes() {
		List<DefaultMutableTreeNode> selected = new LinkedList<DefaultMutableTreeNode>();
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
		((DefaultTreeModel) this.getModel()).reload(reloadPoint);
	}


	/**
	 * Reload from the root
	 */
	public synchronized void reload() {
		this.reload(getRootNode());
	}

}
