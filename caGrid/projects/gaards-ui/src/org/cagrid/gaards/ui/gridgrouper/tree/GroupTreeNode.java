
package org.cagrid.gaards.ui.gridgrouper.tree;

import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.client.Group;

import javax.swing.ImageIcon;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GroupTreeNode extends GridGrouperBaseTreeNode {
	
	private static final long serialVersionUID = 1L;

	private Group group;


	public GroupTreeNode(GridGrouperTree tree, Group group) {
		super(tree);
		this.group = group;
	}


	public void refresh() {
		int id = getTree().startEvent("Refreshing " + toString() + ".... ");
		try {
			if (parent != null) {
				getTree().reload(parent);
			} else {
				getTree().reload();
			}
			getTree().stopEvent(id, "Refreshed " + toString() + "!!!");
		} catch (Exception e) {
			getTree().stopEvent(id, "Error refreshing " + toString() + "!!!");
			ErrorDialog.showError(e);
		}
	}


	public ImageIcon getIcon() {
		return GridGrouperLookAndFeel.getGroupIcon16x16();
	}


	public String toString() {
		return group.getDisplayExtension();
	}


	public GridGrouper getGridGrouper() {
		return group.getGridGrouper();
	}


	public Group getGroup() {
		return group;
	}

}
