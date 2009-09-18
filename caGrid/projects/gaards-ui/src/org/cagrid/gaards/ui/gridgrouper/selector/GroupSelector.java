package org.cagrid.gaards.ui.gridgrouper.selector;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gridgrouper.client.Group;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperHandle;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperServiceList;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;
import org.cagrid.gaards.ui.gridgrouper.tree.GroupTreeNode;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.MultiEventProgressBar;

public class GroupSelector extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel grouperPanel = null;

	private JScrollPane jScrollPane = null;

	private GridGrouperTree groupTree = null;

	private JButton select = null;

	private GridGrouperServiceList gridGrouper = null;

	private JPanel groupPanel = null;

	private GridGrouperHandle currentGridGrouper;

	private Group selectedGroup;

	private MultiEventProgressBar progress = null;

    private JPanel titlePanel = null;

	/**
	 * @param owner
	 */
	public GroupSelector(Frame owner) {
		super(owner);
		initialize();
		loadSelectedGridGrouper();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Select Group");
		this.setSize(600, 400);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getMainPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes mainPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints22.weightx = 1.0D;
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new Insets(5, 25, 5, 25);
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints21.weightx = 1.0D;
			gridBagConstraints21.weighty = 1.0D;
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.gridy = 4;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.NORTH;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getGrouperPanel(), gridBagConstraints);
			mainPanel.add(getSelect(), gridBagConstraints2);
			mainPanel.add(getGroupPanel(), gridBagConstraints21);
			mainPanel.add(getProgress(), gridBagConstraints3);
			mainPanel.add(getTitlePanel(), gridBagConstraints22);
		}
		return mainPanel;
	}

	/**
	 * This method initializes grouperPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGrouperPanel() {
		if (grouperPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.weightx = 1.0;
			grouperPanel = new JPanel();
			grouperPanel.setLayout(new GridBagLayout());
			grouperPanel.add(getGridGrouper(), gridBagConstraints4);
			grouperPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Grid Grouper",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
		}
		return grouperPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getGroupTree());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes groupTree
	 * 
	 * @return javax.swing.JTree
	 */
	private GridGrouperTree getGroupTree() {
		if (groupTree == null) {
			groupTree = new GridGrouperTree();
			groupTree.setProgress(getProgress());
		}
		return groupTree;
	}

	/**
	 * This method initializes select
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSelect() {
		if (select == null) {
			select = new JButton();
			select.setText("Select Group");
			getRootPane().setDefaultButton(this.select);
			select.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					GroupTreeNode node = getGroupTree().getSelectedGroup();
					if (node == null) {
						GridApplication.getContext().showMessage(
								"Please select a group!!!");
					} else {
						selectedGroup = node.getGroup();
						dispose();
					}
				}
			});
		}
		return select;
	}

	public Group getSelectedGroup() {
		return selectedGroup;
	}

	/**
	 * This method initializes gridGrouper
	 * 
	 * @return javax.swing.JComboBox
	 */
	private GridGrouperServiceList getGridGrouper() {
		if (gridGrouper == null) {
			gridGrouper = new GridGrouperServiceList();
			gridGrouper.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					loadSelectedGridGrouper();
				}
			});

		}
		return gridGrouper;
	}

	private void loadSelectedGridGrouper() {
		final GridGrouperHandle selected = getGridGrouper().getSelectedService();
		if ((currentGridGrouper == null)
				|| (!currentGridGrouper.equals(selected))) {

			Runner runner = new Runner() {
				public void execute() {
					getGroupTree().getRootNode().removeAllGridGroupers();
					getGroupTree().getRootNode().addGridGrouper(
							selected.getClient());
				}
			};
			try {
				GridApplication.getContext().executeInBackground(runner);
			} catch (Exception t) {
				t.getMessage();
			}

		}
	}

	/**
	 * This method initializes groupPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGroupPanel() {
		if (groupPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridx = -1;
			gridBagConstraints1.gridy = -1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			groupPanel = new JPanel();
			groupPanel.setLayout(new GridBagLayout());
			groupPanel.add(getJScrollPane(), gridBagConstraints1);
			groupPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, "Select Group",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					LookAndFeel.getPanelLabelColor()));
		}
		return groupPanel;
	}

	/**
	 * This method initializes progress
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private MultiEventProgressBar getProgress() {
		if (progress == null) {
			progress = new MultiEventProgressBar(false);
		}
		return progress;
	}

    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Select Group","Discover and select a group managed by Grid Grouper.");
        }
        return titlePanel;
    }

}
