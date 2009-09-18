package gov.nih.nci.cagrid.data.ui.tree;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/** 
 *  CheckBoxTree
 *  Tree to support check boxes
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 5, 2006 
 * @version $Id: CheckBoxTree.java,v 1.3 2008-01-02 19:32:08 dervin Exp $ 
 */
public class CheckBoxTree extends JTree {
	private DefaultTreeModel model;
	private DefaultMutableTreeNode rootNode;
	private List<CheckTreeSelectionListener> checkTreeSelectionListeners;
	
	public CheckBoxTree() {
		super();
		checkTreeSelectionListeners = new LinkedList<CheckTreeSelectionListener>();
		setCellRenderer(new CellRenderer());
		setCellEditor(new CellEditor());
		setEditable(true);
		setShowsRootHandles(true);
		this.rootNode = new DefaultMutableTreeNode();
		this.model = new DefaultTreeModel(rootNode);
		setModel(model);
		setRootVisible(false); // namespaces attach to invisible root node
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	
	protected DefaultTreeModel getTreeModel() {
		return model;
	}
	
	
	protected DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}
	

	public void clearTree() {
		rootNode.removeAllChildren();
	}
	
	
	public void reloadFromRoot() {
		this.model = new DefaultTreeModel(rootNode);
		this.setModel(this.model);
	}
	
	
	public void addCheckTreeSelectionListener(CheckTreeSelectionListener listener) {
		checkTreeSelectionListeners.add(listener);
	}
	
	
	public boolean removeCheckTreeSelectionListener(CheckTreeSelectionListener listener) {
		return checkTreeSelectionListeners.remove(listener);
	}
	
	
	protected void fireNodeChecked(CheckBoxTreeNode node) {
		Iterator listenerIter = checkTreeSelectionListeners.iterator();
		CheckTreeSelectionEvent event = null;
		while (listenerIter.hasNext()) {
			if (event == null) {
				event = new CheckTreeSelectionEvent(this, node);
			}
			((CheckTreeSelectionListener) listenerIter.next()).nodeChecked(event);
		}
	}
	
	
	protected void fireNodeUnchecked(CheckBoxTreeNode node) {
		Iterator listenerIter = checkTreeSelectionListeners.iterator();
		CheckTreeSelectionEvent event = null;
		while (listenerIter.hasNext()) {
			if (event == null) {
				event = new CheckTreeSelectionEvent(this, node);
			}
			((CheckTreeSelectionListener) listenerIter.next()).nodeUnchecked(event);
		}
	}
	
	
	public CheckBoxTreeNode[] getCheckedNodes() {
		List<CheckBoxTreeNode> selected = new ArrayList<CheckBoxTreeNode>();
		Enumeration nodes = getRootNode().depthFirstEnumeration();
		while (nodes.hasMoreElements()) {
			TreeNode treeNode = (TreeNode) nodes.nextElement();
			if (treeNode instanceof CheckBoxTreeNode) {
				CheckBoxTreeNode checkNode = (CheckBoxTreeNode) treeNode;
				if (checkNode.isChecked()) {
					selected.add(checkNode);
				}
			}
		}
		CheckBoxTreeNode[] types = new CheckBoxTreeNode[selected.size()];
		selected.toArray(types);
		return types;
	}
    
    
    public void repaint() {
        // check boxes changed in the repaint method instead of 'setEnabled()' because
        // nodes added after a call to setEnabled() will always appear 'enabled'
        if (getRootNode() != null) {
            boolean enable = isEnabled();
            Enumeration allNodes = getRootNode().breadthFirstEnumeration();
            while (allNodes.hasMoreElements()) {
                Object o = allNodes.nextElement();
                if (o instanceof CheckBoxTreeNode) {
                    CheckBoxTreeNode node = (CheckBoxTreeNode) o;
                    node.getCheckBox().setEnabled(enable);
                }
            }
        }
        super.repaint();
    }
    
	
	private static class CellRenderer extends DefaultTreeCellRenderer {
		
		public CellRenderer() {
			super();
		}
		
		
		public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean focused) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focused);
            if (value instanceof CheckBoxTreeNode) {
				CheckBoxTreeNode cbNode = (CheckBoxTreeNode) value;
				cbNode.getCheckBox().setBackground(getBackground());
				return cbNode.getCheckBox();
			}
            setEnabled(tree.isEnabled());
			return this;
		}
	}
	
	
	private static class CellEditor extends DefaultCellEditor {
		private JCheckBox check;
		
		public CellEditor() {
			super(new JCheckBox());
		}
		
		
		public Object getCellEditorValue() {
			return check;
		}
		
		
		public boolean isCellEditable(EventObject e) {
			return true;
		}
		
		
		public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
			if (value instanceof CheckBoxTreeNode) {
				CheckBoxTreeNode checkNode = (CheckBoxTreeNode) value;
				check = checkNode.getCheckBox();
				return check;
			} else {
				return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
			}
		}
	}
}
