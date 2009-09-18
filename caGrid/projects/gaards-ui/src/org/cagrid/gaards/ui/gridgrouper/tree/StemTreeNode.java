
package org.cagrid.gaards.ui.gridgrouper.tree;

import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.client.Group;
import gov.nih.nci.cagrid.gridgrouper.client.Stem;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;

import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.tree.TreeNode;

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
public class StemTreeNode extends GridGrouperBaseTreeNode {
	
	private static final long serialVersionUID = 1L;

	private Stem stem;

	private boolean rootStem;

	public StemTreeNode(GridGrouperTree tree, Stem stem, boolean root) {
		super(tree);
		this.rootStem = root;
		this.stem = stem;
	}

	public void loadStem() throws Exception {
		this.removeAllChildren();
		Set set = stem.getChildStems();
		Iterator itr = set.iterator();
		while (itr.hasNext()) {
			StemI currentStem = (StemI) itr.next();
			StemTreeNode node = new StemTreeNode(getTree(),
					((Stem) currentStem), false);
			synchronized (getTree()) {
				this.add(node);
				TreeNode parentNode = this.getParent();
				if (parentNode != null) {
					getTree().reload(parentNode);
				} else {
					getTree().reload();
				}
			}
			node.loadStem();
		}
		Set grps = stem.getChildGroups();
		Iterator itr2 = grps.iterator();
		while (itr2.hasNext()) {
			GroupI group = (GroupI) itr2.next();
			GroupTreeNode node = new GroupTreeNode(getTree(), (Group) group);
			synchronized (getTree()) {
				this.add(node);
				TreeNode parentNode = this.getParent();
				if (parentNode != null) {
					getTree().reload(parentNode);
				} else {
					getTree().reload();
				}
			}
		}
	}

	public void refresh() {
		int id = getTree().startEvent("Refreshing " + toString() + ".... ");
		try {
			stem = (Stem) stem.getGridGrouper().findStem(stem.getName());
			if (parent != null) {
				getTree().reload(parent);
			} else {
				getTree().reload();
			}
			loadStem();
			getTree().stopEvent(id, "Refreshed " + toString() + "!!!");
		} catch (Exception e) {
			e.printStackTrace();
			getTree().stopEvent(id, "Error refreshing " + toString() + "!!!");
			ErrorDialog.showError(e);
		}
	}

	public ImageIcon getIcon() {
		if (this.rootStem) {
			return GridGrouperLookAndFeel.getGrouperIcon16x16();
		} else {
			return GridGrouperLookAndFeel.getStemIcon16x16();
		}
	}

	public String toString() {
		if (this.rootStem) {
			return stem.getGridGrouper().getName();
		} else {
			return stem.getDisplayExtension();
		}
	}

	public boolean isRootStem() {
		return rootStem;
	}

	public GridGrouper getGridGrouper() {
		return stem.getGridGrouper();
	}

	public Stem getStem() {
		return stem;
	}

}
