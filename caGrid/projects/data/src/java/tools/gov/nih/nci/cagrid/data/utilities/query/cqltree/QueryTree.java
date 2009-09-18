package gov.nih.nci.cagrid.data.utilities.query.cqltree;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

/** 
 *  QueryTree
 *  Tree for rendering a CQL Query
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public class QueryTree extends JTree {
	
	private DefaultMutableTreeNode rootNode;

	public QueryTree() {
		rootNode = new DefaultMutableTreeNode();
		// cause each row to be queried for its height
		setRowHeight(-1);
		setModel(new DefaultTreeModel(rootNode));		
		setRootVisible(false);
		setEditable(false);
		setSelectionModel(new DefaultTreeSelectionModel());
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new QueryTreeRenderer());
	}
	
	
	public void refreshTree() {
		((DefaultTreeModel) getModel()).reload(rootNode);
	}
	
	
	public void setQuery(CQLQuery query) {
		rootNode.removeAllChildren();
		QueryTreeNode queryNode = new QueryTreeNode(query);
		rootNode.add(queryNode);
		refreshTree();
	}
	
	
	public QueryTreeNode getQueryTreeNode() {
		if (rootNode.getChildCount() == 1) {
			QueryTreeNode node = (QueryTreeNode) rootNode.getChildAt(0);
			return node;
		}
		return null;
	}
	
	
	private static class QueryTreeRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
			if (value instanceof IconTreeNode) {
				Icon currentIcon = getIcon();
				Icon nodeIcon = ((IconTreeNode) value).getIcon();
				if (currentIcon != null && nodeIcon != null) {
					// combine the icons
					int iconWidth = currentIcon.getIconWidth() + nodeIcon.getIconWidth();
					int iconHeight = Math.max(currentIcon.getIconHeight(), nodeIcon.getIconHeight());
					BufferedImage image = new BufferedImage(iconWidth, iconHeight,
						BufferedImage.TYPE_INT_ARGB);
					Graphics gfx = image.getGraphics();
					currentIcon.paintIcon(this, gfx, 0, 0);
					nodeIcon.paintIcon(this, gfx, currentIcon.getIconWidth(), 0);
					ImageIcon newIcon = new ImageIcon(image);
					setIcon(newIcon);
					setSize(getWidth(), Math.max(getHeight(), iconHeight));
				}
			}
			return this;
		}
	}
}
