package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


public class AddAdminWindow extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel buttonPanel = null;

	private JButton addAdminButton = null;

	private DorianSessionProvider session;

	private JPanel userPanel = null;

	private JTextField gridIdentity = null;

	private JButton findUserButton = null;

	private JPanel titlePanel = null;


	/**
	 * This is the default constructor
	 */
	public AddAdminWindow(DorianSessionProvider session) {
		super(GridApplication.getContext().getApplication());
		this.session = session;
		initialize();
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(500, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("Add Administrator");
		// this.setIconImage(DorianLookAndFeel.getAdminIcon().getImage());
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.gridy = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.weighty = 1.0D;
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 2;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getButtonPanel(), gridBagConstraints1);
			jContentPane.add(getUserPanel(), gridBagConstraints11);
			jContentPane.add(getTitlePanel(), gridBagConstraints3);
		}
		return jContentPane;
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
			buttonPanel.add(getAddAdminButton(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes addAdmin
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddAdminButton() {
		if (addAdminButton == null) {
			addAdminButton = new JButton();
			addAdminButton.setText("Add");
			addAdminButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							addAdmin();
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
		return addAdminButton;
	}


	private void addAdmin() {
		try {
			addAdminButton.setEnabled(false);
			GridAdministrationClient client = session.getSession().getAdminClient();
			client.addAdmin(getGridIdentity().getText());
			dispose();
		} catch (Exception e) {
			ErrorDialog.showError(e);
			addAdminButton.setEnabled(true);
		}
	}


	/**
	 * This method initializes userPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getUserPanel() {
		if (userPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.weightx = 1.0;
			userPanel = new JPanel();
			userPanel.setLayout(new GridBagLayout());
			userPanel.add(getGridIdentity(), gridBagConstraints7);
			userPanel.add(getFindUserButton(), gridBagConstraints8);
		}
		return userPanel;
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
	 * This method initializes findUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFindUserButton() {
		if (findUserButton == null) {
			findUserButton = new JButton();
			findUserButton.setText("Find...");
			findUserButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					UserSearchDialog dialog = new UserSearchDialog(session);
					dialog.setModal(true);
					GridApplication.getContext().showDialog(dialog);
					if (dialog.getSelectedUser() != null) {
						gridIdentity.setText(dialog.getSelectedUser());
					}
				}
			});
		}
		return findUserButton;
	}


	/**
	 * This method initializes titlePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new TitlePanel("Add Administrator","Enter the Grid Identity of the user you wish to grant administrative rights to.");
		}
		return titlePanel;
	}

}
