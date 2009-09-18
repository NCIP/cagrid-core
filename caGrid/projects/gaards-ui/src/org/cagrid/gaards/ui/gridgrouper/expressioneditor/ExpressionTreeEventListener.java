
package org.cagrid.gaards.ui.gridgrouper.expressioneditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ExpressionTreeEventListener extends MouseAdapter {
	
	private static final long serialVersionUID = 1L;

	private ExpressionTree tree;

	private GridGrouperExpressionEditor editor;

	private HashMap popupMappings;


	public ExpressionTreeEventListener(ExpressionTree owningTree, GridGrouperExpressionEditor editor) {
		this.tree = owningTree;
		this.popupMappings = new HashMap();
		this.editor = editor;
	}


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
		if ((SwingUtilities.isLeftMouseButton(e))) {
			DefaultMutableTreeNode currentNode = this.tree.getCurrentNode();
			if (currentNode instanceof ExpressionNode) {
				ExpressionNode exp = (ExpressionNode) currentNode;
				editor.setExpressionEditor(exp.getExpression());
			} else if (currentNode instanceof QueryNode) {
				QueryNode query = (QueryNode) currentNode;
				editor.setExpressionQuery(query.getQuery());
			}
		}
	}
}
