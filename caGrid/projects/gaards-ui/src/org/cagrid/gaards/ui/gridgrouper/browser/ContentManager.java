package org.cagrid.gaards.ui.gridgrouper.browser;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.cagrid.gaards.ui.gridgrouper.CombinedIcon;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperBaseTreeNode;
import org.cagrid.gaards.ui.gridgrouper.tree.GroupTreeNode;
import org.cagrid.gaards.ui.gridgrouper.tree.StemTreeNode;
import org.cagrid.grape.utils.ErrorDialog;
import org.cagrid.grape.utils.MultiEventProgressBar;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ContentManager extends JTabbedPane implements StemActionListener,
		GroupActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel welcomePanel = null;

	private static final String WELCOME = "Grid Grouper";

	private JLabel gridGrouperImage = null;

	private Map stems = new HashMap(); // @jve:decl-index=0:

	private Map groups = new HashMap(); // @jve:decl-index=0:

	private MultiEventProgressBar progress;

	/**
	 * This is the default constructor
	 */
	public ContentManager(MultiEventProgressBar progress) {
		super();
		this.progress = progress;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.addTab(WELCOME, GridGrouperLookAndFeel.getGrouperIcon22x22(),
				getWelcomePanel(), null);
	}

	public void addNode(GridGrouperBaseTreeNode node) {
		if (node instanceof StemTreeNode) {
			this.addStem((StemTreeNode) node);
		} else if (node instanceof GroupTreeNode) {
			this.addGroup((GroupTreeNode) node);
		} else {
			ErrorDialog.showError("Please select a stem or group to view!!!");
		}
	}

	public void viewStem(StemTreeNode node) {
		addNode(node);
	}

	public void removeNode(GridGrouperBaseTreeNode node) {
		if (node instanceof StemTreeNode) {
			this.removeStem((StemTreeNode) node);
		} else if (node instanceof GroupTreeNode) {
			this.removeGroup((GroupTreeNode) node);
		} else {
			ErrorDialog.showError("Please select a stem or group to remove!!!");
		}
	}

	public void refreshStem(StemTreeNode node) {
		String stemId = node.getStem().getUuid();
		if (stems.containsKey(stemId)) {
			StemBrowser browse = (StemBrowser) stems.get(stemId);
			for (int i = 0; i < getTabCount(); i++) {
				if (getComponentAt(i) == browse) {
					this.setTitleAt(i, node.getStem().getDisplayExtension());
				}
			}
			browse.setStem();
		}
	}

	public void refreshGroup(GroupTreeNode node) {
		String groupId = node.getGroup().getUuid();
		if (groups.containsKey(groupId)) {
			GroupBrowser browse = (GroupBrowser) groups.get(groupId);
			for (int i = 0; i < getTabCount(); i++) {
				if (getComponentAt(i) == browse) {
					this.setTitleAt(i, node.getGroup().getDisplayExtension());
				}
			}
			browse.setGroup();
		}
	}

	public void addStem(StemTreeNode node) {
		String stemId = node.getStem().getUuid();
		this.removeStem(node, true);
		StemBrowser browser = new StemBrowser(this, this, node);
		browser.setProgress(progress);
		stems.put(stemId, browser);
		this.addTab(node.getStem().getDisplayExtension(), new CombinedIcon(
				new ContentManagerTabCloseIcon(), GridGrouperLookAndFeel
						.getStemIcon16x16()), browser, null);
		this.remove(getWelcomePanel());
		this.setSelectedComponent(browser);

	}

	public void addGroup(GroupTreeNode node) {
		String groupId = node.getGroup().getUuid();
		this.removeGroup(node, true);
		GroupBrowser browser = new GroupBrowser(node);
		browser.setProgress(progress);
		groups.put(groupId, browser);
		this.addTab(node.getGroup().getDisplayExtension(), new CombinedIcon(
				new ContentManagerTabCloseIcon(), GridGrouperLookAndFeel
						.getGroupIcon16x16()), browser, null);
		this.remove(getWelcomePanel());
		this.setSelectedComponent(browser);
	}

	public void removeSelectedNode() {
		Component c = this.getSelectedComponent();
		if (c instanceof StemBrowser) {
			StemBrowser sb = (StemBrowser) c;
			removeStem(sb.getStemNode());
		}

		if (c instanceof GroupBrowser) {
			GroupBrowser sb = (GroupBrowser) c;
			removeGroup(sb.getGroupNode());
		}
	}

	public void viewGroup(GroupTreeNode node) {
		addNode(node);
	}

	public void removeGroup(GroupTreeNode node) {
		this.removeGroup(node, false);
	}

	private void removeGroup(GroupTreeNode node, boolean internal) {
		String groupId = node.getGroup().getUuid();
		if (groups.containsKey(groupId)) {
			GroupBrowser sb = (GroupBrowser) groups.remove(groupId);
			this.remove(sb);
		}
		if (!internal) {
			if ((stems.size() == 0) && (groups.size() == 0)) {
				this.addTab(WELCOME, GridGrouperLookAndFeel
						.getGrouperIcon22x22(), getWelcomePanel(), null);
				this.setSelectedComponent(getWelcomePanel());
			}
		}
	}

	public void removeStem(StemTreeNode node) {
		this.removeStem(node, false);
	}

	private void removeStem(StemTreeNode node, boolean internal) {
		String stemId = node.getStem().getUuid();
		if (stems.containsKey(stemId)) {
			StemBrowser sb = (StemBrowser) stems.remove(stemId);
			this.remove(sb);
		}
		if (!internal) {
			if ((stems.size() == 0) && (groups.size() == 0)) {
				this.addTab(WELCOME, GridGrouperLookAndFeel
						.getGrouperIcon22x22(), getWelcomePanel(), null);
				this.setSelectedComponent(getWelcomePanel());
			}
		}
	}

	public void removeAllNodes(String gridGrouper) {
		Iterator stemItr = stems.values().iterator();
		List nodesToRemove = new ArrayList();
		while (stemItr.hasNext()) {
			StemBrowser sb = (StemBrowser) stemItr.next();
			if (gridGrouper.equals(sb.getStemNode().getStem().getGridGrouper()
					.getName())) {
				nodesToRemove.add(sb.getStemNode());
			}
		}

		Iterator groupItr = groups.values().iterator();
		while (groupItr.hasNext()) {
			GroupBrowser sb = (GroupBrowser) groupItr.next();
			if (gridGrouper.equals(sb.getGroupNode().getGroup()
					.getGridGrouper().getName())) {
				nodesToRemove.add(sb.getGroupNode());
			}
		}

		for (int i = 0; i < nodesToRemove.size(); i++) {
			GridGrouperBaseTreeNode node = (GridGrouperBaseTreeNode) nodesToRemove
					.get(i);
			this.removeNode(node);
		}

	}

	/**
	 * This method initializes welcomePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getWelcomePanel() {
		if (welcomePanel == null) {
			gridGrouperImage = new JLabel(GridGrouperLookAndFeel
					.getGrouperIconNoBackground());
			welcomePanel = new JPanel();
			welcomePanel.setLayout(new GridBagLayout());
			welcomePanel.add(gridGrouperImage, new GridBagConstraints());
		}
		return welcomePanel;
	}
}
