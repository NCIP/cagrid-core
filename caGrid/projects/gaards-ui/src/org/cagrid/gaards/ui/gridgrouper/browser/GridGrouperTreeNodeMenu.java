package org.cagrid.gaards.ui.gridgrouper.browser;

import gov.nih.nci.cagrid.common.Runner;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public abstract class GridGrouperTreeNodeMenu extends JPopupMenu {
	
	private static final long serialVersionUID = 1L;
	
	private GroupManagementBrowser browser;

	private GridGrouperTree tree;

	private JMenuItem view = null;

	private JMenuItem refresh = null;

	private JMenuItem remove = null;;


	public GridGrouperTreeNodeMenu(GroupManagementBrowser browser, GridGrouperTree tree) {
		super("");
		this.browser = browser;
		this.tree = tree;
		initialize();

	}


	/**
	 * This method initializes this
	 */
	private void initialize() {

		this.add(getView());
		this.add(getRefresh());
		this.add(getRemove());

	}


	/**
	 * This method initializes view
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getView() {
		if (view == null) {
			view = new JMenuItem();
			view.setText("View");
			view.setIcon(LookAndFeel.getQueryIcon());
			view.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							getBrowser().getContentManager().addNode(getGridGrouperTree().getCurrentNode());
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}

			});
		}
		return view;
	}


	public GroupManagementBrowser getBrowser() {
		return browser;
	}


	public GridGrouperTree getGridGrouperTree() {
		return tree;
	}


	/**
	 * This method initializes refresh
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getRefresh() {
		if (refresh == null) {
			refresh = new JMenuItem();
			refresh.setText("Refresh");
			refresh.setIcon(GridGrouperLookAndFeel.getLoadIcon());
			refresh.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							getGridGrouperTree().getCurrentNode().refresh();
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}

			});
		}
		return refresh;
	}


	protected void toggleRemove(boolean toggle) {
		this.getRemove().setEnabled(toggle);
	}


	/**
	 * This method initializes remove
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getRemove() {
		if (remove == null) {
			remove = new JMenuItem();
			remove.setText("Remove");
			remove.setIcon(LookAndFeel.getRemoveIcon());
			remove.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							removeNode();
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}

			});
		}
		return remove;
	}


	public abstract void removeNode();

}
