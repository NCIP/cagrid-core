
package org.cagrid.gaards.ui.gridgrouper.expressioneditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;
import org.cagrid.gaards.ui.gridgrouper.tree.GroupTreeNode;
import org.cagrid.gaards.ui.gridgrouper.tree.StemTreeNode;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GrouperTreeExpressionEventListener extends MouseAdapter {

	private GridGrouperTree tree;

	private GridGrouperExpressionEditor editor;

	private HashMap popupMappings;


	public GrouperTreeExpressionEventListener(GridGrouperTree owningTree, GridGrouperExpressionEditor editor) {
		this.tree = owningTree;
		this.popupMappings = new HashMap();
		this.editor = editor;
		this.associatePopup(StemTreeNode.class, new StemNodeMenu(editor, this.tree));
		this.associatePopup(GroupTreeNode.class, new GroupNodeMenu(editor, this.tree));
	}


	/**
	 * Associate a GridServiceTreeNode type with a popup menu
	 * 
	 * @param nodeType
	 * @param popup
	 */
	public void associatePopup(Class nodeType, JPopupMenu popup) {
		this.popupMappings.put(nodeType, popup);
	}


	public void mouseEntered(MouseEvent e) {
		maybeShowPopup(e);
	}


	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}


	private void maybeShowPopup(MouseEvent e) {
		if ((e.isPopupTrigger()) || (SwingUtilities.isRightMouseButton(e))) {
			DefaultMutableTreeNode currentNode = this.tree.getCurrentNode();
			GridGrouperTreeNodeMenu popup = null;
			if (currentNode != null) {
				popup = (GridGrouperTreeNodeMenu) popupMappings.get(currentNode.getClass());
			}
			if (popup != null) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}

		} else if (e.getClickCount() == 2) {
			DefaultMutableTreeNode currentNode = this.tree.getCurrentNode();
			if (currentNode instanceof GroupTreeNode) {
				GroupTreeNode grp = (GroupTreeNode) currentNode;
				this.editor.addGroupToCurrentExpression(grp.getGroup());
			}
		}
	}
}
