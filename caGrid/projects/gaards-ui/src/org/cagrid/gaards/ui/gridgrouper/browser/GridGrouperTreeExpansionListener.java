package org.cagrid.gaards.ui.gridgrouper.browser;

import java.util.Enumeration;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;
import org.cagrid.gaards.ui.gridgrouper.tree.StemTreeNode;
import org.cagrid.grape.utils.ErrorDialog;

public class GridGrouperTreeExpansionListener implements TreeExpansionListener {
	private GridGrouperTree tree = null;

	public GridGrouperTreeExpansionListener(GridGrouperTree tree) {
		this.tree = tree;
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		return;
	}

	public void treeExpanded(TreeExpansionEvent event) {
		String endMessage = null;
		StemTreeNode node = (StemTreeNode) event.getPath().getLastPathComponent();

		if (node.loadedChildStems()) {
			return;
		}
		
		int id = tree.startEvent("Loading Stems.... ");	

		Enumeration<?> children = node.children();
		try {
			while (children.hasMoreElements()) {
				Object child = children.nextElement();

				if (child instanceof StemTreeNode) {
					((StemTreeNode) child).loadStem(0);
				}
			}
			node.setLoadedChildStems(true);
			endMessage = node.toString() + " Stems Successfully Loaded!!!";
		} catch (Exception e) {
			ErrorDialog.showError(e);
			endMessage = "Error loading stem!!!";
		}
		tree.stopEvent(id, endMessage);
	}

}
