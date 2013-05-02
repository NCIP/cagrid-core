/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.ui.gridgrouper.browser;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;

import java.util.Enumeration;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;
import org.cagrid.gaards.ui.gridgrouper.tree.StemTreeNode;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;

public class GridGrouperTreeExpansionListener implements TreeExpansionListener {
	private static Log log = LogFactory.getLog(GridGrouperTreeExpansionListener.class);
	
	private GridGrouperTree tree = null;
	
	private StemTreeNode node = null;

	public GridGrouperTreeExpansionListener(GridGrouperTree tree) {
		this.tree = tree;
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		return;
	}

	public void treeExpanded(TreeExpansionEvent event) {
        Runner runner = new Runner() {
            public void execute() {
                loadNodes();
            }         
        };
        try {
        	node = (StemTreeNode) event.getPath().getLastPathComponent();
            GridApplication.getContext().executeInBackground(runner);
        } catch (Exception t) {
            t.getMessage();
        }

	}
	
	private void loadNodes() {
		if (node ==  null) {
			return;
		}
		
		String endMessage = null;

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
			FaultUtil.logFault(log, e);
		}
		tree.stopEvent(id, endMessage);
		
	}

}
