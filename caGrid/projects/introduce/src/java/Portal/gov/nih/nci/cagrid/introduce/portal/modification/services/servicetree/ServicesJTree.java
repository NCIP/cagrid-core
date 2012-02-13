package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.PopupTreeNode;
import gov.nih.nci.cagrid.introduce.portal.common.SortableJTreeModel;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


public class ServicesJTree extends JTree {
    private ServicesTypeTreeNode root;
    private ServiceInformation info;
    private JPanel optionsPanel;
    private DefaultMutableTreeNode currentNode = null;
    
    private static ServicesJTree tree = null;
    
    
    //will not create a new one
    public static ServicesJTree getInstance(){
        return tree;
    }


    public ServicesJTree(ServiceInformation info, JPanel optionsPanel) {
        super(new SortableJTreeModel(null, new ServiceJTreeComparator()));
        this.optionsPanel = optionsPanel;
        this.info = info;
        this.tree = this;

        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        setCellRenderer(new ServicesTreeRenderer());
        ToolTipManager.sharedInstance().registerComponent(
            this);
        setServices(info);

        this.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                currentNode = (DefaultMutableTreeNode) (e.getPath().getLastPathComponent());
                if (currentNode != null) {
                    if (currentNode instanceof MethodsTypeTreeNode) {
                        ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(
                            ServicesJTree.this.optionsPanel, "methods");
                    } else if (currentNode instanceof MethodTypeTreeNode) {
                        ((MethodButtonPanel) ServicesJTree.this.optionsPanel.getComponent(3)).setCanModify(true);
                        // show the correct card for editing a method
                        ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(
                            ServicesJTree.this.optionsPanel, "method");
                    } else if (currentNode instanceof ResourcePropertiesTypeTreeNode) {
                        ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(
                            ServicesJTree.this.optionsPanel, "resources");
                    } else if (currentNode instanceof ServicesTypeTreeNode) {
                        ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(
                            ServicesJTree.this.optionsPanel, "services");
                    } else if (currentNode instanceof ResourcePropertyTypeTreeNode) {
                        Component[] comps = ServicesJTree.this.optionsPanel.getComponents();
                        for(int i = 0; i < comps.length; i ++){
                            if(comps[i] instanceof ResourcePropertyButtonPanel){
                                ResourcePropertyButtonPanel panel = (ResourcePropertyButtonPanel)comps[i];
                                panel.updateView();
                                ServicesJTree.this.optionsPanel.repaint();
                            }
                        }
                        ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(
                            ServicesJTree.this.optionsPanel, "resourceProperty");
                    } else if (currentNode instanceof ServiceTypeTreeNode) {
                        ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(
                            ServicesJTree.this.optionsPanel, "service");
                    } else {
                        ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(
                            ServicesJTree.this.optionsPanel, "blank");
                    }
                } else {
                    ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(ServicesJTree.this.optionsPanel,
                        "blank");
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (currentNode instanceof PopupTreeNode) {
                        ((PopupTreeNode) currentNode).getPopUpMenu().show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        // expand anything in the tree if it changes
        this.getModel().addTreeModelListener(new TreeModelListener() {

            public void treeStructureChanged(TreeModelEvent e) {
                expandPath(e.getTreePath());
            }


            public void treeNodesRemoved(TreeModelEvent e) {
                expandPath(e.getTreePath());
            }


            public void treeNodesInserted(TreeModelEvent e) {
                expandPath(e.getTreePath());
            }


            public void treeNodesChanged(TreeModelEvent e) {
                expandPath(e.getTreePath());
            }
        });
    }


    public ServicesTypeTreeNode getRoot() {
        return root;
    }


    public void removeAllNodes(TreeNode node) {
        if (node != null) {
            // node is visited exactly once
            if (!node.equals(root)) {
                ((DefaultTreeModel) getModel()).removeNodeFromParent((MutableTreeNode) node);
            }

            if (node.getChildCount() >= 0) {
                for (Enumeration e = node.children(); e.hasMoreElements();) {
                    TreeNode n = (TreeNode) e.nextElement();
                    removeAllNodes(n);
                }
            }
        }
    }


    public void setServices(ServiceInformation info) {
        removeAllNodes(root);
        this.currentNode = null;
        ((DefaultTreeModel) this.getModel()).setRoot(null);
        root = new ServicesTypeTreeNode(info);
        ((DefaultTreeModel) this.getModel()).setRoot(root);
        root.setServices(info, (DefaultTreeModel) this.getModel());
        ((CardLayout) ServicesJTree.this.optionsPanel.getLayout()).show(ServicesJTree.this.optionsPanel,
        "blank");
        // expand the root
        this.expandAll(true);

    }


    public DefaultMutableTreeNode getCurrentNode() {
        return currentNode;
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


    @Override
    protected void setExpandedState(TreePath path, boolean state) {
        // pass along all expand events
        if (state) {
            super.setExpandedState(path, state);
        } else {
            // only pass along collapse events that aren't collapsing the root
            if (!path.getLastPathComponent().equals(getRoot())) {
                super.setExpandedState(path, state);
            }
        }
    }
}
