package gov.nih.nci.cagrid.introduce.portal.updater.steps.updatetree;

import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.software.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.software.IntroduceType;
import gov.nih.nci.cagrid.introduce.beans.software.SoftwareType;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class UpdateTree extends JTree {

    DefaultTreeModel model = null;

    NamespacesType namespaces;

    private MainUpdateTreeNode root;


    public UpdateTree() {
        initialize();
    }


    public void update(SoftwareType software) {
        root = new MainUpdateTreeNode("Introduce Versions and Extensions", model, software);
        model = new DefaultTreeModel(root);
        this.setModel(model);
        // root.setModel(model);
        this.setCellRenderer(new UpdateTreeRenderer(model));
        this.setRootVisible(true);
        expandAll(true);
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) UpdateTree.this.getLastSelectedPathComponent();

                if (node == null) {
                    return;
                }
                Object obj = node.getUserObject();

                if ((obj != null) && (obj instanceof JCheckBox)) {
                    JCheckBox box = (JCheckBox) obj;
                    // return if this software is already installed
                    if (node instanceof IntroduceUpdateTreeNode) {

                        if (((IntroduceUpdateTreeNode) node).isInstalled()) {
                            return;
                        }
                    } else if (node instanceof ExtensionUpdateTreeNode) {
                        if (!box.isSelected() && !((IntroduceUpdateTreeNode) node.getParent()).isInstalled()) {
                            return;
                        }
                        if (((ExtensionUpdateTreeNode) node).isInstalled()) {
                            return;
                        }
                    } else if (node instanceof IntroduceRevUpdateTreeNode) {
                        if (!box.isSelected() && !((IntroduceUpdateTreeNode) node.getParent()).isInstalled()) {
                            return;
                        }
                        if (((IntroduceRevUpdateTreeNode) node).isInstalled()) {
                            return;
                        }
                    }
                    box.setSelected(!box.isSelected());
                    if ((node instanceof IntroduceUpdateTreeNode) && !box.isSelected()) {
                        // need to select or deselect all of it's children
                        int children = node.getChildCount();
                        for (int i = 0; i < children; i++) {
                            ((ExtensionUpdateTreeNode) node.getChildAt(i)).getCheckBox().setSelected(box.isSelected());
                        }
                    }
                    if ((node instanceof ExtensionUpdateTreeNode) && box.isSelected()) {
                        node = (DefaultMutableTreeNode) node.getParent();
                    } else if ((node instanceof IntroduceRevUpdateTreeNode) && box.isSelected()) {
                        node = (DefaultMutableTreeNode) node.getParent();
                    }
                    if ((node instanceof IntroduceUpdateTreeNode)
                        && (box.isSelected() || ((IntroduceUpdateTreeNode) node).isInstalled())) {
                        // need to unselect anything else not in this branch
                        int introduceNodeCount = root.getChildCount();
                        for (int i = 0; i < introduceNodeCount; i++) {
                            DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) root.getChildAt(i);
                            if (!treenode.equals(node)) {
                                IntroduceUpdateTreeNode introducenode = (IntroduceUpdateTreeNode) treenode;
                                introducenode.getCheckBox().setSelected(false);
                                int children = introducenode.getChildCount();
                                for (int j = 0; j < children; j++) {
                                    if (introducenode.getChildAt(j) instanceof ExtensionUpdateTreeNode) {
                                        ((ExtensionUpdateTreeNode) introducenode.getChildAt(j)).getCheckBox()
                                            .setSelected(false);
                                    } else if (introducenode.getChildAt(j) instanceof IntroduceRevUpdateTreeNode) {
                                        ((IntroduceRevUpdateTreeNode) introducenode.getChildAt(j)).getCheckBox()
                                            .setSelected(false);
                                    }
                                }
                            }
                        }
                    }
                    UpdateTree.this.paintAll(UpdateTree.this.getGraphics());
                } 
            }
        });
    }


    public SoftwareType getNonInstalledSelectedSoftware() {
        SoftwareType software = new SoftwareType();
        List introduceInstalls = new ArrayList();
        List extensionInstalls = new ArrayList();

        int introduceNodeCount = root.getChildCount();
        for (int i = 0; i < introduceNodeCount; i++) {
            DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) root.getChildAt(i);
            IntroduceUpdateTreeNode introducenode = (IntroduceUpdateTreeNode) treenode;

            if (!introducenode.isInstalled() && introducenode.isSelected()) {
                introduceInstalls.add(introducenode.getIntroduce());
            } else if (introducenode.isInstalled()) {
                if (introducenode.getChildCount() > 0
                    && introducenode.getChildAt(0) instanceof IntroduceRevUpdateTreeNode) {
                    DefaultMutableTreeNode treenodetemp = (DefaultMutableTreeNode) introducenode.getChildAt(0);
                    IntroduceRevUpdateTreeNode introducerevnode = (IntroduceRevUpdateTreeNode) treenodetemp;
                    if (introducerevnode.isSelected() && !introducerevnode.isInstalled()) {
                        introduceInstalls.add(introducenode.getIntroduce());
                    }

                }
            }
            introducenode.getCheckBox().setSelected(false);
            int children = introducenode.getChildCount();
            for (int j = 0; j < children; j++) {
                if (introducenode.getChildAt(j) instanceof ExtensionUpdateTreeNode) {
                    ExtensionUpdateTreeNode extensionnode = (ExtensionUpdateTreeNode) introducenode.getChildAt(j);

                    if (!extensionnode.isInstalled() && extensionnode.isSelected()) {
                        extensionInstalls.add(extensionnode.getExtension());
                    }
                }
            }

        }

        IntroduceType[] introduces = new IntroduceType[introduceInstalls.size()];
        introduceInstalls.toArray(introduces);
        ExtensionType[] extensions = new ExtensionType[extensionInstalls.size()];
        extensionInstalls.toArray(extensions);

        software.setIntroduce(introduces);
        software.setExtension(extensions);

        return software;
    }


    private void initialize() {
        root = new MainUpdateTreeNode("Introduce Versions and Extensions", model, null);
        model = new DefaultTreeModel(root);
        this.setModel(model);
        root.setModel(model);
        this.setCellRenderer(new UpdateTreeRenderer(model));
        this.setRootVisible(true);
        expandAll(true);
    }


    public DefaultMutableTreeNode getCurrentNode() {
        TreePath currentSelection = this.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentSelection.getLastPathComponent();
            if (currentNode != root) {
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
                selected.add(currentNode);
            }
        }
        return selected;
    }


    public void removeSelectedNode() {
        DefaultMutableTreeNode currentNode = getCurrentNode();
        if (currentNode != null) {
            model.removeNodeFromParent(currentNode);
        }
    }


    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public void expandAll(boolean expand) {
        JTree tree = this;
        TreeNode currRoot = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(new TreePath(currRoot), expand);
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
        } else if (state && (path.getLastPathComponent() == root)) {
            super.setExpandedState(path, state);
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
