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

package org.cagrid.gaards.ui.gridgrouper.tree;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.client.Stem;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.ServiceMetadataServiceDescription;
import gov.nih.nci.cagrid.metadata.service.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.wsrf.utils.AddressingUtils;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 */
public class GridGroupersTreeNode extends GridGrouperBaseTreeNode {
	private static Log log = LogFactory.getLog(GridGroupersTreeNode.class);
	
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
				node.loadStem(0);
				
				try {
					EndpointReferenceType endPoint  = AddressingUtils.createEndpointReference(grouper.getName(), null);
					ServiceMetadata commonMetadata = MetadataUtils.getServiceMetadata(endPoint);
					ServiceMetadataServiceDescription desc = commonMetadata.getServiceDescription();
					Service service = desc.getService();
					node.setGridGrouperVersion(service.getVersion());
				} catch (Exception e) {
					e.getMessage();
				}

				getTree().stopEvent(id,
						"Grid Grouper Service Successfully Loaded!!!");
				this.groupers.put(grouper.getName(), node);
			} catch (Exception e) {
				ErrorDialog.showError(e);
				getTree()
						.stopEvent(id, "Error loading Grid Grouper Service!!!");
				FaultUtil.logFault(log, e);
			}

		}

	}
	
	public StemTreeNode getStemTreeNode(GridGrouper grouper) {
		return (StemTreeNode) groupers.get(grouper.getName());
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
