package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;

public class IdentityDelegationPolicyPanel extends DelegationPolicyPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane jScrollPane = null;

	private GridIdentityTable gridIdentityTable = null;

	private JPanel addIdentityPanel = null;

	private JLabel jLabel = null;

	private JTextField gridIdentity = null;

	private JButton findButton = null;

	private JPanel actionPanel = null;

	private JButton addButton = null;

	private JButton removeButton = null;

	/**
	 * This is the default constructor
	 */
	public IdentityDelegationPolicyPanel(boolean editMode) {
		super(editMode);
		initialize();
	}

	public IdentityDelegationPolicy getPolicy() {
		IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
		AllowedParties ap = new AllowedParties();
		int count = getGridIdentityTable().getRowCount();
		String[] ids = new String[count];
		for (int i = 0; i < count; i++) {
			ids[i] = (String) getGridIdentityTable().getValueAt(i, 0);
		}
		ap.setGridIdentity(ids);
		policy.setAllowedParties(ap);
		return policy;
	}

	public void setPolicy(DelegationPolicy policy) {
		IdentityDelegationPolicy p = (IdentityDelegationPolicy) policy;
		AllowedParties ap = p.getAllowedParties();
		if (ap != null) {
			String[] ids = ap.getGridIdentity();
			if (ids != null) {
				for (int i = 0; i < ids.length; i++) {
					getGridIdentityTable().addIdentity(ids[i]);
				}
			}
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints21.weightx = 1.0D;
		gridBagConstraints21.gridy = 2;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.weightx = 1.0D;
		gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints11.gridy = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.weightx = 1.0;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				"Identity Delegation Policy",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
				LookAndFeel.getPanelLabelColor()));
		this.add(getJScrollPane(), gridBagConstraints);
		this.add(getAddIdentityPanel(), gridBagConstraints11);
		this.add(getActionPanel(), gridBagConstraints21);
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getGridIdentityTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes gridIdentityTable
	 * 
	 * @return javax.swing.JTable
	 */
	private GridIdentityTable getGridIdentityTable() {
		if (gridIdentityTable == null) {
			gridIdentityTable = new GridIdentityTable();
		}
		return gridIdentityTable;
	}

	/**
	 * This method initializes addIdentityPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAddIdentityPanel() {
		if (addIdentityPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.weightx = 1.0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Grid Identity");
			addIdentityPanel = new JPanel();
			addIdentityPanel.setLayout(new GridBagLayout());
			addIdentityPanel.add(jLabel, gridBagConstraints1);
			addIdentityPanel.add(getGridIdentity(), gridBagConstraints2);
			addIdentityPanel.add(getFindButton(), gridBagConstraints3);
			if (!isEditMode()) {
				addIdentityPanel.setVisible(false);
			}
		}
		return addIdentityPanel;
	}

	/**
	 * This method initializes gridIdentity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridIdentity() {
		if (gridIdentity == null) {
			gridIdentity = new JTextField();
		}
		return gridIdentity;
	}

	/**
	 * This method initializes findButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFindButton() {
		if (findButton == null) {
			findButton = new JButton();
			findButton.setText("Find...");
			findButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					UserSearchDialog dialog = new UserSearchDialog();
					dialog.setModal(true);
					GridApplication.getContext().showDialog(dialog);
					if (dialog.getSelectedUser() != null) {
						getGridIdentity().setText(dialog.getSelectedUser());
					}
				}
			});
		}
		return findButton;
	}

	/**
	 * This method initializes actionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.gridy = 0;
			actionPanel = new JPanel();
			actionPanel.setLayout(new GridBagLayout());
			actionPanel.add(getAddButton(), gridBagConstraints4);
			actionPanel.add(getRemoveButton(), gridBagConstraints5);
			if (!isEditMode()) {
				actionPanel.setVisible(false);
			}
		}
		return actionPanel;
	}

	/**
	 * This method initializes addButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add");
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String gridId = Utils.clean(getGridIdentity().getText());
					if (gridId == null) {
						GridApplication.getContext().showMessage(
								"Please specify a Grid Identity.");
					}
					getGridIdentityTable().addIdentity(gridId);
					getGridIdentity().setText("");
				}
			});
		}
		return addButton;
	}

	/**
	 * This method initializes removeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove");
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getGridIdentityTable().removeSelectedIdentity();
					} catch (Exception ex) {
						GridApplication.getContext().showMessage(
								ex.getMessage());
					}
				}
			});
		}
		return removeButton;
	}

}
