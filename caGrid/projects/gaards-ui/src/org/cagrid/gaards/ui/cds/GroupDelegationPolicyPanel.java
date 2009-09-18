package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.client.Group;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.GroupDelegationPolicy;
import org.cagrid.gaards.ui.gridgrouper.selector.GroupSelector;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;

public class GroupDelegationPolicyPanel extends DelegationPolicyPanel {

	private static final long serialVersionUID = 1L;

	private JLabel jLabel = null;

	private JTextField gridGrouperURL = null;

	private JLabel jLabel1 = null;

	private JTextField groupName = null;

	private JButton searchButton = null;

	/**
	 * This is the default constructor
	 */
	public GroupDelegationPolicyPanel(boolean editMode) {
		super(editMode);
		initialize();
	}

	public GroupDelegationPolicy getPolicy() {
		GroupDelegationPolicy policy = new GroupDelegationPolicy();
		policy.setGridGrouperServiceURL(Utils.clean(getGridGrouperURL()
				.getText()));
		policy.setGroupName(Utils.clean(getGroupName().getText()));
		return policy;
	}

	public void setPolicy(DelegationPolicy policy) {
		GroupDelegationPolicy p = (GroupDelegationPolicy) policy;
		getGridGrouperURL().setText(p.getGridGrouperServiceURL());
		getGroupName().setText(p.getGroupName());
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints11.gridwidth = 2;
		gridBagConstraints11.gridy = 2;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints3.gridx = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints2.gridy = 1;
		jLabel1 = new JLabel();
		jLabel1.setText("Group Name");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.gridy = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.gridx = 1;
		jLabel = new JLabel();
		jLabel.setText("Grid Grouper URL");
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				"Group Delegation Policy",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
				LookAndFeel.getPanelLabelColor()));
		this.add(jLabel, gridBagConstraints1);
		this.add(getGridGrouperURL(), gridBagConstraints);
		this.add(jLabel1, gridBagConstraints2);
		this.add(getGroupName(), gridBagConstraints3);
		this.add(getSearchButton(), gridBagConstraints11);
	}

	/**
	 * This method initializes gridGrouperURL
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridGrouperURL() {
		if (gridGrouperURL == null) {
			gridGrouperURL = new JTextField();
			if (!isEditMode()) {
				gridGrouperURL.setEditable(false);
			}
		}
		return gridGrouperURL;
	}

	/**
	 * This method initializes groupName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGroupName() {
		if (groupName == null) {
			groupName = new JTextField();
			if (!isEditMode()) {
				groupName.setEditable(false);
			}
		}
		return groupName;
	}

	/**
	 * This method initializes searchButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setText("Browse Groups....");
			if(!isEditMode()){
				searchButton.setEnabled(false);
				searchButton.setVisible(false);
			}
			searchButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					GroupSelector selector = new GroupSelector(GridApplication.getContext().getApplication());
					selector.setModal(true);
					GridApplication.getContext().showDialog(selector);
					Group group = selector.getSelectedGroup();
					gridGrouperURL.setText(group.getGridGrouper().getName());
					groupName.setText(group.getName());
				}
			});
		}
		return searchButton;
	}

}
