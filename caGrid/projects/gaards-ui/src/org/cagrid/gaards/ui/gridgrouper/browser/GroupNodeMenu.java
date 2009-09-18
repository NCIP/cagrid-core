package org.cagrid.gaards.ui.gridgrouper.browser;

import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperBaseTreeNode;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;
import org.cagrid.gaards.ui.gridgrouper.tree.GroupTreeNode;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GroupNodeMenu extends GridGrouperTreeNodeMenu {
	
	private static final long serialVersionUID = 1L;

	public GroupNodeMenu(GroupManagementBrowser browser, GridGrouperTree tree) {
		super(browser, tree);
	}


	private GroupTreeNode getGroupNode() {
		return (GroupTreeNode) getGridGrouperTree().getCurrentNode();
	}


	public void removeNode() {
		GroupTreeNode node = getGroupNode();

		int id = getBrowser().getProgress().startEvent("Removing the group.... ");
		try {
			node.getGroup().delete();
			getBrowser().getContentManager().removeGroup(node);
			((GridGrouperBaseTreeNode) node.getParent()).refresh();
			getBrowser().getProgress().stopEvent(id, "Successfully removed the group !!!");
		} catch (Exception e) {
			getBrowser().getProgress().stopEvent(id, "Error removing the group !!!");
			ErrorDialog.showError(e);
		}
	}

}
