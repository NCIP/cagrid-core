
package org.cagrid.gaards.ui.gridgrouper.tree;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.client.Stem;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GridGroupersTreeNode extends GridGrouperBaseTreeNode {
	
	private static final long serialVersionUID = 1L;

	private Map groupers;

	public GridGroupersTreeNode(GridGrouperTree tree) {
		super(tree);
		this.groupers = new HashMap();
	}

	public synchronized void addGridGrouper(GridGrouper grouper) {
		if (groupers.containsKey(grouper.getName())) {
			ErrorDialog.showError("The Grid Grouper Service "
					+ grouper.getName() + " has already been added!!!");
		} else {
			int id = getTree().startEvent("Loading Grid Grouper Service.... ");
			try {
				StemI root = grouper.getRootStem();
				StemTreeNode node = new StemTreeNode(getTree(), ((Stem) root),
						true);
				synchronized (getTree()) {
					this.add(node);
					getTree().reload(this);
				}
				node.loadStem();
				getTree().stopEvent(id,
						"Grid Grouper Service Successfully Loaded!!!");
				this.groupers.put(grouper.getName(), node);
			} catch (Exception e) {
				ErrorDialog.showError(e);
				getTree()
						.stopEvent(id, "Error loading Grid Grouper Service!!!");
			}

		}

	}

	public synchronized void removeAllGridGroupers() {
		this.groupers.clear();
		this.removeAllChildren();
	}

	public synchronized void refresh() {
		Map old = groupers;
		groupers = new HashMap();
		this.removeAllChildren();
		Iterator itr = old.values().iterator();
		while (itr.hasNext()) {
			final StemTreeNode node = (StemTreeNode) itr.next();
			Runner runner = new Runner() {
				public void execute() {
					addGridGrouper(node.getGridGrouper());
				}
			};
			try {
				GridApplication.getContext().executeInBackground(runner);
			} catch (Exception t) {
				t.getMessage();
			}

		}

	}

	public void removeSelectedGridGrouper() {
		GridGrouperBaseTreeNode node = this.getTree().getCurrentNode();
		if (node == null) {
			ErrorDialog.showError("No service selected, please select a Grid Grouper Service!!!");
		} else {
			if (node instanceof StemTreeNode) {
				StemTreeNode stn = (StemTreeNode) node;
				if (stn.isRootStem()) {
					synchronized (getTree()) {
						stn.removeFromParent();
						this.groupers.remove(stn.getGridGrouper().getName());
						getTree().reload(this);
					}
				} else {
					ErrorDialog.showError("No service selected, please select a Grid Grouper Service!!!");
				}
			} else {
				ErrorDialog.showError("No service selected, please select a Grid Grouper Service!!!");
			}
		}

	}

	public ImageIcon getIcon() {
		return GridGrouperLookAndFeel.getGridGrouperServicesIcon16x16();
	}

	public String toString() {

		return "Grid Grouper Service(s)";
	}
}
