package org.cagrid.gaards.ui.gridgrouper.browser;

import edu.internet2.middleware.grouper.CompositeType;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.gaards.ui.gridgrouper.tree.GroupTreeNode;
import org.cagrid.grape.ApplicationComponent;
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
public class AddMemberWindow extends ApplicationComponent {

	private static final String USER = "User";

	private static final String GROUP = "Group";

	private static final String COMPOSITE = "Composite";

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel detailsPanel = null;

	private GroupTreeNode node;

	private JLabel jLabel = null;

	private JTextField gridGrouper = null;

	private JLabel jLabel1 = null;

	private JTextField group = null;

	private JLabel jLabel2 = null;

	private JTextField credentials = null;

	private JLabel jLabel3 = null;

	private JComboBox membershipType = null;

	private JPanel mainPanel = null;

	private JPanel memberPanel = null;

	private JPanel buttonPanel = null;

	private JButton addMember = null;

	private JPanel addUserPanel = null;

	private CardLayout memberTypeLayout = null;

	private JLabel jLabel4 = null;

	private JTextField userIdentity = null;

	private JPanel addGroupPanel = null;

	private JLabel jLabel5 = null;

	private JComboBox groupToAdd = null;

	private JPanel addCompositePanel = null;

	private JLabel jLabel6 = null;

	private JComboBox compositeType = null;

	private JLabel jLabel7 = null;

	private JComboBox leftGroup = null;

	private JLabel jLabel8 = null;

	private JComboBox rightGroup = null;

	private GroupBrowser browser;

	private JButton find = null;

    private JPanel titlePanel = null;


	/**
	 * This is the default constructor
	 */
	public AddMemberWindow(GroupBrowser browser, GroupTreeNode node) {
		super();
		this.node = node;
		this.browser = browser;
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(500, 300);
		this.setContentPane(getJContentPane());
		this.setTitle("Add Member");
		this.setFrameIcon(GridGrouperLookAndFeel.getMemberIcon22x22());
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
	 * This method initializes detailsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDetailsPanel() {
		if (detailsPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.gridx = 0;
			jLabel3 = new JLabel();
			jLabel3.setText("Member Type");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			jLabel2 = new JLabel();
			jLabel2.setText("Credentials");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Group");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.gridx = 0;
			jLabel = new JLabel();
			jLabel.setText("Grid Grouper");
			detailsPanel = new JPanel();
			detailsPanel.setLayout(new GridBagLayout());
			detailsPanel.add(jLabel, gridBagConstraints);
			detailsPanel.add(getGridGrouper(), gridBagConstraints1);
			detailsPanel.add(jLabel1, gridBagConstraints11);
			detailsPanel.add(getGroup(), gridBagConstraints2);
			detailsPanel.add(jLabel2, gridBagConstraints3);
			detailsPanel.add(getCredentials(), gridBagConstraints5);
			detailsPanel.add(jLabel3, gridBagConstraints6);
			detailsPanel.add(getMembershipType(), gridBagConstraints7);
		}
		return detailsPanel;
	}


	/**
	 * This method initializes gridGrouper
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridGrouper() {
		if (gridGrouper == null) {
			gridGrouper = new JTextField();
			gridGrouper.setEditable(false);
			gridGrouper.setText(node.getGridGrouper().getName());
		}
		return gridGrouper;
	}


	/**
	 * This method initializes group
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGroup() {
		if (group == null) {
			group = new JTextField();
			group.setEditable(false);
			group.setText(node.getGroup().getDisplayName());
		}
		return group;
	}


	/**
	 * This method initializes credentials
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCredentials() {
		if (credentials == null) {
			credentials = new JTextField();
			credentials.setEditable(false);
			credentials.setText(node.getGridGrouper().getProxyIdentity());
		}
		return credentials;
	}


	/**
	 * This method initializes membershipType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getMembershipType() {
		if (membershipType == null) {
			membershipType = new JComboBox();
			membershipType.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					swapPanels();
				}
			});
			membershipType.addItem(USER);
			membershipType.addItem(GROUP);
			membershipType.addItem(COMPOSITE);
		}
		return membershipType;
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
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.weightx = 1.0D;
			gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints22.gridy = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 3;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridx = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.weighty = 2.0D;
			gridBagConstraints8.insets = new Insets(2, 10, 2, 10);
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.weightx = 1.0D;
			gridBagConstraints4.weighty = 1.0D;
			gridBagConstraints4.gridy = 1;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getMemberPanel(), gridBagConstraints8);
			mainPanel.add(getDetailsPanel(), gridBagConstraints4);
			mainPanel.add(getButtonPanel(), gridBagConstraints9);
			mainPanel.add(getTitlePanel(), gridBagConstraints22);
		}
		return mainPanel;
	}


	/**
	 * This method initializes memberPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMemberPanel() {
		if (memberPanel == null) {
			memberPanel = new JPanel();
			memberTypeLayout = new CardLayout();
			memberPanel.setLayout(memberTypeLayout);
			memberPanel.add(getAddCompositePanel(), COMPOSITE);
			memberPanel.add(getAddGroupPanel(), GROUP);
			memberPanel.add(getAddUserPanel(), USER);
		}
		return memberPanel;
	}


	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getAddMember(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes addMember
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddMember() {
		if (addMember == null) {
			addMember = new JButton();
			addMember.setText("Add");
			getRootPane().setDefaultButton(addMember);
			addMember.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							addMember();
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
		return addMember;
	}


	private void addMember() {
		String type = (String) this.getMembershipType().getSelectedItem();
		if (type.equals(USER)) {
			String user = Utils.clean(this.getUserIdentity().getText());
			if (user == null) {
				ErrorDialog.showError("Please enter a member identity!!!");
				return;
			} else {
				try {
					node.getGroup().addMember(SubjectUtils.getSubject(user));
					if (browser.getHasListedMembers()) {
						browser.listMembers();
					}
					dispose();

					GridApplication.getContext().showMessage("The member was added successfully!!!");
				} catch (Exception e) {
					e.printStackTrace();
					ErrorDialog.showError(e);
					return;
				}
			}
		} else if (type.equals(GROUP)) {
			GroupNodeCaddy caddy = (GroupNodeCaddy) getGroupToAdd().getSelectedItem();
			try {
				node.getGroup().addMember(caddy.getGroupNode().getGroup().toSubject());
				if (browser.getHasListedMembers()) {
					browser.listMembers();
				}
				dispose();
				GridApplication.getContext().showMessage("The group member was added successfully!!!");
			} catch (Exception e) {
				ErrorDialog.showError(e);
				return;
			}

		} else if (type.equals(COMPOSITE)) {
			CompositeType ct = (CompositeType) this.getCompositeType().getSelectedItem();
			GroupNodeCaddy left = (GroupNodeCaddy) getLeftGroup().getSelectedItem();
			GroupNodeCaddy right = (GroupNodeCaddy) getRightGroup().getSelectedItem();
			try {
				node.getGroup().addCompositeMember(ct, left.getGroupNode().getGroup(), right.getGroupNode().getGroup());
				this.node.refresh();
				this.browser.setGroup();
				if (browser.getHasListedMembers()) {
					browser.listMembers();
				}
				dispose();
				GridApplication.getContext().showMessage("The composite member was added successfully!!!");
			} catch (Exception e) {
				ErrorDialog.showError(e);
				return;
			}

		}

	}


	/**
	 * This method initializes addUserPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAddUserPanel() {
		if (addUserPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.gridy = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.weightx = 1.0;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridy = 0;
			jLabel4 = new JLabel();
			jLabel4.setText("Member Identity");
			addUserPanel = new JPanel();
			addUserPanel.setLayout(new GridBagLayout());
			addUserPanel.setName("addUserPanel");
			addUserPanel.setBorder(BorderFactory.createTitledBorder(null, "Add User",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
				new Color(62, 109, 181)));
			addUserPanel.add(jLabel4, gridBagConstraints10);
			addUserPanel.add(getUserIdentity(), gridBagConstraints12);
			addUserPanel.add(getFind(), gridBagConstraints21);
		}
		return addUserPanel;
	}


	/**
	 * This method initializes userIdentity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getUserIdentity() {
		if (userIdentity == null) {
			userIdentity = new JTextField();
		}
		return userIdentity;
	}


	/**
	 * This method initializes addGroupPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAddGroupPanel() {
		if (addGroupPanel == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.gridx = 1;
			gridBagConstraints14.gridy = 0;
			gridBagConstraints14.weightx = 1.0;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			jLabel5 = new JLabel();
			jLabel5.setText("Group");
			addGroupPanel = new JPanel();
			addGroupPanel.setLayout(new GridBagLayout());
			addGroupPanel.setBorder(BorderFactory.createTitledBorder(null, "Add Group",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
				new Color(62, 109, 181)));
			addGroupPanel.add(jLabel5, gridBagConstraints13);
			addGroupPanel.add(getGroupToAdd(), gridBagConstraints14);
		}
		return addGroupPanel;
	}


	/**
	 * This method initializes groupToAdd
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getGroupToAdd() {
		if (groupToAdd == null) {
			groupToAdd = new JComboBox();
			List nodes = node.getTree().getGroupNodes();
			for (int i = 0; i < nodes.size(); i++) {
				this.groupToAdd.addItem(new GroupNodeCaddy((GroupTreeNode) nodes.get(i)));
			}
		}
		return groupToAdd;
	}


	private void swapPanels() {
		String type = (String) this.getMembershipType().getSelectedItem();
		this.memberTypeLayout.show(getMemberPanel(), type);
	}


	private class GroupNodeCaddy {
		private GroupTreeNode groupNode;


		public GroupNodeCaddy(GroupTreeNode node) {
			this.groupNode = node;
		}


		public GroupTreeNode getGroupNode() {
			return groupNode;
		}


		public String toString() {
			return groupNode.getGroup().getDisplayName();
		}
	}


	/**
	 * This method initializes addCompositePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAddCompositePanel() {
		if (addCompositePanel == null) {
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints20.gridy = 2;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.anchor = GridBagConstraints.WEST;
			gridBagConstraints20.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints20.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.anchor = GridBagConstraints.WEST;
			gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints19.gridy = 2;
			jLabel8 = new JLabel();
			jLabel8.setText("Right Group");
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints18.gridy = 1;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints18.gridx = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.gridy = 1;
			jLabel7 = new JLabel();
			jLabel7.setText("Left Group");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.gridy = 0;
			gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints15.gridx = 0;
			jLabel6 = new JLabel();
			jLabel6.setText("Composite Type");
			addCompositePanel = new JPanel();
			addCompositePanel.setLayout(new GridBagLayout());
			addCompositePanel.setName(COMPOSITE);
			addCompositePanel.setBorder(BorderFactory.createTitledBorder(null, "Add Composite Member",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
				new Color(62, 109, 181)));
			addCompositePanel.add(jLabel6, gridBagConstraints15);
			addCompositePanel.add(getCompositeType(), gridBagConstraints16);
			addCompositePanel.add(jLabel7, gridBagConstraints17);
			addCompositePanel.add(getLeftGroup(), gridBagConstraints18);
			addCompositePanel.add(jLabel8, gridBagConstraints19);
			addCompositePanel.add(getRightGroup(), gridBagConstraints20);
		}
		return addCompositePanel;
	}


	/**
	 * This method initializes compositeType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCompositeType() {
		if (compositeType == null) {
			compositeType = new JComboBox();
			compositeType.addItem(CompositeType.UNION);
			compositeType.addItem(CompositeType.INTERSECTION);
			compositeType.addItem(CompositeType.COMPLEMENT);
		}
		return compositeType;
	}


	/**
	 * This method initializes leftGroup
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getLeftGroup() {
		if (leftGroup == null) {
			leftGroup = new JComboBox();
			List nodes = node.getTree().getGroupNodes();
			for (int i = 0; i < nodes.size(); i++) {
				this.leftGroup.addItem(new GroupNodeCaddy((GroupTreeNode) nodes.get(i)));
			}
		}
		return leftGroup;
	}


	/**
	 * This method initializes rightGroup
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getRightGroup() {
		if (rightGroup == null) {
			rightGroup = new JComboBox();
			List nodes = node.getTree().getGroupNodes();
			for (int i = 0; i < nodes.size(); i++) {
				this.rightGroup.addItem(new GroupNodeCaddy((GroupTreeNode) nodes.get(i)));
			}
		}
		return rightGroup;
	}


	/**
	 * This method initializes find	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getFind() {
		if (find == null) {
			find = new JButton();
			find.setText("Find...");
			find.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					UserSearchDialog dialog = new UserSearchDialog();
					dialog.setModal(true);
					GridApplication.getContext().showDialog(dialog);
					if (dialog.getSelectedUser() != null) {
						getUserIdentity().setText(dialog.getSelectedUser());
					}
				}
			});
		}
		return find;
	}


    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Add Member","Add a member to the group "+node.getGroup().getDisplayExtension()+".");
        }
        return titlePanel;
    }

}
