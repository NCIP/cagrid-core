package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.cds.client.DelegationAdminClient;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: DelegatedCredentialManagerWindow.java,v 1.1 2007/11/19 17:05:26
 *          langella Exp $
 */
public class DelegatedCredentialManagerWindow extends ApplicationComponent {
	
	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel contentPanel = null;

	private JPanel buttonPanel = null;

	private DelegationRecordsTable delegatedCredentialsTable = null;

	private JScrollPane jScrollPane = null;

	private JButton manageDelegatedCredential = null;

	private SessionPanel session = null;

	private JPanel queryPanel = null;

	private JButton query = null;

	private ProgressPanel progressPanel = null;

	private JPanel filterPanel = null;

	private JLabel gridLabel = null;

	private JTextField gridIdentity = null;

	private boolean adminMode;

	private JLabel jLabel = null;

	private JTextField delegationId = null;

	private JLabel jLabel1 = null;

	private ExpirationStatusComboBox expirationStatus = null;

	private JLabel jLabel2 = null;

	private DelegationStatusComboBox delegationStatus = null;

	private JPanel gridIdentityPanel = null;

	private JButton findButton = null;

	private JButton deleteButton = null;

	private JButton deleteAll = null;

    private JPanel titlePanel = null;

	/**
	 * This is the default constructor
	 */
	public DelegatedCredentialManagerWindow(boolean adminMode) {
		super();
		this.adminMode = adminMode;
		initialize();
		this.setFrameIcon(CDSLookAndFeel.getDelegateCredentialsIcon());
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		if (adminMode) {
			this.setTitle("Delegated Credential Manager");
		} else {
			this.setTitle("My Delegated Credentials");
		}
		setSize(600, 600);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.weightx = 1.0D;
			gridBagConstraints15.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 2;
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 0;
			gridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints32.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints32.weightx = 1.0D;
			gridBagConstraints32.gridy = 7;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.gridy = 4;
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.gridx = 0;
			gridBagConstraints35.weightx = 1.0D;
			gridBagConstraints35.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints35.gridy = 1;

			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 5;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 6;
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			mainPanel.add(getButtonPanel(), gridBagConstraints2);
			mainPanel.add(getContentPanel(), gridBagConstraints1);
			mainPanel.add(getSession(), gridBagConstraints35);
			mainPanel.add(getQueryPanel(), gridBagConstraints33);
			mainPanel.add(getProgressPanel(), gridBagConstraints32);
			mainPanel.add(getFilterPanel(), gridBagConstraints);
			mainPanel.add(getTitlePanel(), gridBagConstraints15);
		}
		return mainPanel;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getContentPanel() {
		if (contentPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			contentPanel = new JPanel();
			contentPanel.setLayout(new GridBagLayout());
			contentPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Delegated Credential(s)",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
			contentPanel.add(getJScrollPane(), gridBagConstraints4);
		}
		return contentPanel;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getManageDelegatedCredential(), null);
			buttonPanel.add(getDeleteButton(), null);
			buttonPanel.add(getDeleteAll(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private DelegationRecordsTable getDelegatedCredentialsTable() {
		if (delegatedCredentialsTable == null) {
			delegatedCredentialsTable = new DelegationRecordsTable(getSession());
		}
		return delegatedCredentialsTable;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getDelegatedCredentialsTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes manageDelegatedCredential
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getManageDelegatedCredential() {
		if (manageDelegatedCredential == null) {
			manageDelegatedCredential = new JButton();
			manageDelegatedCredential.setText("View");
			manageDelegatedCredential
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							disableButtons();
							getDelegatedCredentialsTable().doubleClick();
							enableButtons();
						}

					});
		}

		return manageDelegatedCredential;
	}

	/**
	 * This method initializes session
	 * 
	 * @return javax.swing.JPanel
	 */
	private SessionPanel getSession() {
		if (session == null) {
			session = new SessionPanel();
		}
		return session;
	}

	/**
	 * This method initializes queryPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getQueryPanel() {
		if (queryPanel == null) {
			queryPanel = new JPanel();
			queryPanel.add(getQuery(), null);
		}
		return queryPanel;
	}

	/**
	 * This method initializes query
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getQuery() {
		if (query == null) {
			query = new JButton();
			query.setText("Search");
			getRootPane().setDefaultButton(query);
			query.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							findDelegatedCredentials();
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}

				}
			});
		}
		return query;
	}

	private void findDelegatedCredentials() {

		disableButtons();

		this.getDelegatedCredentialsTable().clearTable();
		getProgressPanel().showProgress("Searching...");

		try {
			DelegationRecordFilter f = new DelegationRecordFilter();

			String idStr = Utils.clean(this.getDelegationId().getText());
			if (idStr != null) {
				try {
					DelegationIdentifier id = new DelegationIdentifier();
					id.setDelegationId(Integer.valueOf(idStr).intValue());
					f.setDelegationIdentifier(id);
				} catch (Exception e) {
					getProgressPanel().stopProgress("Error");
					ErrorDialog
							.showError("A Delegation Identifier must be an integer.");
					return;
				}
			}

			if (adminMode) {
				f.setGridIdentity(Utils.clean(getGridIdentity().getText()));

			} else {
				f.setGridIdentity(getSession().getCredential().getIdentity());
			}

			f.setDelegationStatus(getDelegationStatus().getDelegationStatus());
			f.setExpirationStatus(getExpirationStatus().getExpirationStatus());

			List<DelegationRecord> records;

			if (adminMode) {
				DelegationAdminClient client = new DelegationAdminClient(
						getSession().getServiceURI(), getSession()
								.getCredential());

				records = client.findDelegatedCredentials(f);
			} else {
				DelegationUserClient client = new DelegationUserClient(
						getSession().getServiceURI(), getSession()
								.getCredential());
				records = client.findMyDelegatedCredentials(f);
			}

			for (int i = 0; i < records.size(); i++) {
				this.getDelegatedCredentialsTable().addRecord(records.get(i));
			}

			getProgressPanel().stopProgress(records.size()+" credential(s) found,");

		} catch (PermissionDeniedFault pdf) {
			ErrorDialog.showError(pdf);
			getProgressPanel().stopProgress("Error");
		} catch (Exception e) {
			ErrorDialog.showError(e);
			getProgressPanel().stopProgress("Error");
		} finally {
			enableButtons();
		}

	}

	/**
	 * This method initializes progressPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private ProgressPanel getProgressPanel() {
		if (progressPanel == null) {
			progressPanel = new ProgressPanel();
		}
		return progressPanel;
	}



	/**
	 * This method initializes filterPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridx = 1;
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.weightx = 1.0D;
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 3;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridy = 3;
			jLabel2 = new JLabel();
			jLabel2.setText("Delegation Status");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 2;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = 2;
			jLabel1 = new JLabel();
			jLabel1.setText("Expiration Status");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.weightx = 1.0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.gridy = 1;
			jLabel = new JLabel();
			jLabel.setText("Delegation Identifier");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 7;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints6.gridy = 0;
			gridLabel = new JLabel();
			gridLabel.setText("Grid Identity");
			if (adminMode) {
				gridLabel.setVisible(true);
				gridLabel.setEnabled(true);
			} else {
				gridLabel.setVisible(false);
				gridLabel.setEnabled(false);
			}
			filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			filterPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Search Criteria", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			filterPanel.add(gridLabel, gridBagConstraints6);
			filterPanel.add(jLabel, gridBagConstraints3);
			filterPanel.add(getDelegationId(), gridBagConstraints5);
			filterPanel.add(jLabel1, gridBagConstraints8);
			filterPanel.add(getExpirationStatus(), gridBagConstraints9);
			filterPanel.add(jLabel2, gridBagConstraints10);
			filterPanel.add(getDelegationStatus(), gridBagConstraints12);
			filterPanel.add(getGridIdentityPanel(), gridBagConstraints13);
		}
		return filterPanel;
	}

	/**
	 * This method initializes gridIdentity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridIdentity() {
		if (gridIdentity == null) {
			gridIdentity = new JTextField();
			if (adminMode) {
				gridIdentity.setVisible(true);
				gridIdentity.setEnabled(true);
			} else {
				gridIdentity.setVisible(false);
				gridIdentity.setEnabled(false);
			}
		}
		return gridIdentity;
	}

	

	/**
	 * This method initializes delegationId
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getDelegationId() {
		if (delegationId == null) {
			delegationId = new JTextField();
		}
		return delegationId;
	}

	/**
	 * This method initializes expirationStatus
	 * 
	 * @return javax.swing.JComboBox
	 */
	private ExpirationStatusComboBox getExpirationStatus() {
		if (expirationStatus == null) {
			expirationStatus = new ExpirationStatusComboBox(true);
		}
		return expirationStatus;
	}

	/**
	 * This method initializes delegationStatus
	 * 
	 * @return javax.swing.JComboBox
	 */
	private DelegationStatusComboBox getDelegationStatus() {
		if (delegationStatus == null) {
			delegationStatus = new DelegationStatusComboBox(true);
		}
		return delegationStatus;
	}

	/**
	 * This method initializes gridIdentityPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGridIdentityPanel() {
		if (gridIdentityPanel == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 1;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridy = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridIdentityPanel = new JPanel();
			gridIdentityPanel.setLayout(new GridBagLayout());
			gridIdentityPanel.add(getGridIdentity(), gridBagConstraints7);
			if (adminMode) {
				gridIdentityPanel.setVisible(true);
				gridIdentityPanel.setEnabled(true);
				gridIdentityPanel.add(getFindButton(), gridBagConstraints14);
			} else {
				gridIdentityPanel.setVisible(false);
				gridIdentityPanel.setEnabled(false);
			}
		}
		return gridIdentityPanel;
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
			if (adminMode) {
				findButton.setVisible(true);
				findButton.setEnabled(true);
			} else {
				findButton.setVisible(false);
				findButton.setEnabled(false);
			}
		}
		return findButton;
	}

	/**
	 * This method initializes deleteButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteButton() {
		if (deleteButton == null) {
			deleteButton = new JButton();
			deleteButton.setText("Delete");

			if (!adminMode) {
				deleteButton.setVisible(false);
				deleteButton.setEnabled(false);
			}

			deleteButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							try {
								disableButtons();
								DelegationAdminClient client = new DelegationAdminClient(
										getSession().getServiceURI(),
										getSession().getCredential());
								DelegationRecord r = getDelegatedCredentialsTable()
										.getSelectedRecord();
								client.deleteDelegatedCredential(r
										.getDelegationIdentifier());
								getDelegatedCredentialsTable().removeRecord(
										r.getDelegationIdentifier());
								GridApplication.getContext().showMessage(
										"Succesfully removed the delegation record with the id "
												+ r.getDelegationIdentifier()
														.getDelegationId()
												+ ".");
							} catch (Exception ex) {
								ErrorDialog.showError(Utils
										.getExceptionMessage(ex), ex);
							} finally {
								enableButtons();
							}
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}

				}
			});
		}
		return deleteButton;
	}

	private void disableButtons() {
		getManageDelegatedCredential().setEnabled(false);
		getFindButton().setEnabled(false);
		getDeleteButton().setEnabled(false);
		getDeleteAll().setEnabled(false);
	}

	private void enableButtons() {
		getManageDelegatedCredential().setEnabled(true);
		getFindButton().setEnabled(true);
		if (adminMode) {
			getDeleteButton().setEnabled(true);
			getDeleteAll().setEnabled(true);
		}
	}

	/**
	 * This method initializes deleteAll
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteAll() {
		if (deleteAll == null) {
			deleteAll = new JButton();
			deleteAll.setText("Delete All");
			if (!adminMode) {
				deleteAll.setVisible(false);
				deleteAll.setEnabled(false);
			}
			deleteAll.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							List<DelegationRecord> removed = new ArrayList<DelegationRecord>();
							try {
								disableButtons();
								int value = JOptionPane
										.showConfirmDialog(
												GridApplication.getContext()
														.getApplication(),
												"Are you sure you want to delete the "
														+ getDelegatedCredentialsTable()
																.getRowCount()
														+ " delegated credential(s) listed.");
								if (value == JOptionPane.OK_OPTION) {
									DelegationAdminClient client = new DelegationAdminClient(
											getSession().getServiceURI(),
											getSession().getCredential());

									for (int i = 0; i < getDelegatedCredentialsTable()
											.getRowCount(); i++) {
										DelegationRecord r = getDelegatedCredentialsTable()
												.getRecord(i);
										client.deleteDelegatedCredential(r
												.getDelegationIdentifier());
										removed.add(r);
									}

									GridApplication.getContext().showMessage(
											"Succesfully removed  "
													+ removed.size()
													+ " delegation record(s).");
								} else {
									return;
								}

							} catch (Exception ex) {
								ErrorDialog.showError(Utils
										.getExceptionMessage(ex), ex);
							} finally {
								for (int i = 0; i < removed.size(); i++) {
									getDelegatedCredentialsTable()
											.removeRecord(
													removed
															.get(i)
															.getDelegationIdentifier());
								}
								enableButtons();
							}
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}

				}
			});
		}
		return deleteAll;
	}

    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Delegated Credential Search","Search for and manage delegated credentials.");
        }
        return titlePanel;
    }

}
