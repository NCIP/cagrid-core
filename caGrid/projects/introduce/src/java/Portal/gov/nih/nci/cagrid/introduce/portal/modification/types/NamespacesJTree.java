package gov.nih.nci.cagrid.introduce.portal.modification.types;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class NamespacesJTree extends JTree {
	NamespacesTypeTreeNode root;
	DefaultTreeModel model;
	NamespacesType namespaces;
	boolean showW3CSimpleTypes = true;


	public NamespacesJTree(NamespacesType namespaces, boolean showW3CSimpleTypes) {
		this.showW3CSimpleTypes = showW3CSimpleTypes;
		setRootVisible(false);
		setCellRenderer(new NamespacesTreeRenderer(model));
		setNamespaces(namespaces);
	}


	private void initialize() {
		if (namespaces.getNamespace() != null) {
			NamespaceTypeTreeNode w3cNode = null;
			for (int i = 0; i < namespaces.getNamespace().length; i++) {
				if (!checkTypeExists(namespaces.getNamespace(i))) {
					NamespaceTypeTreeNode newNode = new NamespaceTypeTreeNode(namespaces.getNamespace(i), model);
					if (namespaces.getNamespace(i).getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
						w3cNode = newNode;
					} else {
						model.insertNodeInto(newNode, root, root.getChildCount());
					}
				}
			}
			if (showW3CSimpleTypes) {
				model.insertNodeInto(w3cNode, root, root.getChildCount());
			}
		}
		expandAll(true);
	}


	public void setNamespaces(NamespacesType ns) {
		this.root = new NamespacesTypeTreeNode();
		this.model = new DefaultTreeModel(root, false);
		this.namespaces = ns;
		setModel(model);
		initialize();
	}


	public void addNode(NamespaceType type) {
		if (!checkTypeExists(type)) {
			NamespaceTypeTreeNode newNode = new NamespaceTypeTreeNode(type, model);
			model.insertNodeInto(newNode, root, 0);
			expandPath(new TreePath(model.getPathToRoot(newNode)));
			// keep namespacestype consistant
			int currentLength = 0;
			if (namespaces.getNamespace() != null) {
				currentLength = namespaces.getNamespace().length;
			}
			NamespaceType[] newNamespaceTypes = new NamespaceType[currentLength + 1];
			if (currentLength > 0) {
				System.arraycopy(namespaces.getNamespace(), 0, newNamespaceTypes, 1, currentLength);
			}
			newNamespaceTypes[0] = type;
			namespaces.setNamespace(newNamespaceTypes);
		}
	}


	public DefaultMutableTreeNode getCurrentNode() {
		TreePath currentSelection = this.getSelectionPath();
		if (currentSelection != null) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentSelection.getLastPathComponent();
			if (currentNode != this.root) {
				return currentNode;
			}
		}
		return null;
	}


	public List getSelectedNodes() {
		List selected = new LinkedList();
		TreePath[] currentSelection = this.getSelectionPaths();
		if (currentSelection != null) {
			for (int i = 0; i < currentSelection.length; i++) {
				TreePath path = currentSelection[i];
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				if (currentNode != this.root) {
					selected.add(currentNode);
				}
			}
		}
		return selected;
	}


	public void removeSelectedNode() {
		DefaultMutableTreeNode currentNode = getCurrentNode();

		// keep the namespaces object in sync
		if (currentNode instanceof NamespaceTypeTreeNode) {
			NamespaceType[] namespaceTypes = namespaces.getNamespace();
			if (namespaceTypes.length > 1) {
				NamespaceType[] newNamespaceTypes = new NamespaceType[namespaceTypes.length - 1];
				int kept = 0;
				for (int i = 0; i < namespaceTypes.length; i++) {
					NamespaceType type = namespaceTypes[i];
					if (!type.equals(currentNode.getUserObject())) {
						newNamespaceTypes[kept] = type;
						kept++;
					}
				}
				namespaces.setNamespace(newNamespaceTypes);
			} else {
				namespaces.setNamespace(null);
			}
		}

		if (currentNode != null) {
			model.removeNodeFromParent(currentNode);
		}
	}


	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree.
	public void expandAll(boolean expand) {
		JTree tree = this;
		TreeNode rootNode = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(new TreePath(rootNode), expand);
	}


	private void expandAll(TreePath parent, boolean expand) {
		JTree tree = this;
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (java.util.Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}


	protected void setExpandedState(TreePath path, boolean state) {
		// Ignore all collapse requests; collapse events will not be fired
		if (path.getLastPathComponent() != root) {
			super.setExpandedState(path, state);
		} else if (state && path.getLastPathComponent() == root) {
			super.setExpandedState(path, state);
		}
	}


	private boolean checkTypeExists(NamespaceType type) {
		TreeNode rootNode = (TreeNode) this.getModel().getRoot();
		// Traverse tree from root
		return checkTypeExists(type, new TreePath(rootNode));
	}


	private boolean checkTypeExists(NamespaceType type, TreePath parent) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (java.util.Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				NamespaceTypeTreeNode nsNode = (NamespaceTypeTreeNode) n;
				NamespaceType nsType = (NamespaceType) nsNode.getUserObject();
				if (nsType.getNamespace().equals(type.getNamespace())) {
					return true;
				}
			}
		}
		return false;
	}
}
