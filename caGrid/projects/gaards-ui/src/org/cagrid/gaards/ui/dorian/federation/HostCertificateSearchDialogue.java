package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigInteger;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.HostCertificateFilter;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: HostCertificatesWindow.java,v 1.1 2007/06/06 19:27:54 langella
 *          Exp $
 */
public class HostCertificateSearchDialogue extends JDialog implements
		HostCertificateLauncher {

	private static final long serialVersionUID = 1L;
	
	private javax.swing.JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel contentPanel = null;

	private JPanel buttonPanel = null;

	private HostCertificatesTable hostCertificatesTable = null;

	private JScrollPane jScrollPane = null;

	private JButton select = null;

	private JPanel queryPanel = null;

	private JButton query = null;

	private JPanel criteriaPanel = null;

	private JLabel jLabel = null;

	private JTextField recordId = null;

	private JLabel jLabel1 = null;

	private JTextField host = null;

	private JLabel jLabel2 = null;

	private JTextField serialNumber = null;

	private JLabel jLabel3 = null;

	private HostCertificateStatusComboBox status = null;

	private JLabel jLabel4 = null;

	private JTextField subject = null;

	private JLabel jLabel5 = null;

	private JPanel ownerPanel = null;

	private JTextField ownerId = null;

	private JButton findUser = null;

	private JLabel jLabel6 = null;

	private JComboBox expiration = null;

	private JPanel titlePanel = null;

	private ProgressPanel progressPanel = null;

	private DorianSessionProvider session;

	private HostCertificateRecord selectedHostCertificate;

	/**
	 * This is the default constructor
	 */
	public HostCertificateSearchDialogue(DorianSessionProvider session) {
		super(GridApplication.getContext().getApplication());
		this.session = session;
		initialize();
		setSize(500, 550);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Host Certificate Management");
	}

	public void selectHostCertificate(HostCertificateRecord record) {
		try {

			selectedHostCertificate = getHostCertificatesTable()
					.getSelectedHostCertificate();
			dispose();
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}

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
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridy = 5;
			gridBagConstraints21.weightx = 1.0D;
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
			gridBagConstraints110.gridx = 0;
			gridBagConstraints110.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints110.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints110.weightx = 1.0D;
			gridBagConstraints110.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 1;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 3;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 4;
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			mainPanel.add(getButtonPanel(), gridBagConstraints2);
			mainPanel.add(getContentPanel(), gridBagConstraints1);
			mainPanel.add(getQueryPanel(), gridBagConstraints33);
			mainPanel.add(getCriteriaPanel(), gridBagConstraints);
			mainPanel.add(getTitlePanel(), gridBagConstraints110);
			mainPanel.add(getProgressPanel(), gridBagConstraints21);
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
									"Host Certificates",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.gridx = 0;
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
			buttonPanel.add(getSelect(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private HostCertificatesTable getHostCertificatesTable() {
		if (hostCertificatesTable == null) {
			hostCertificatesTable = new HostCertificatesTable(this);
		}
		return hostCertificatesTable;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getHostCertificatesTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes manageUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSelect() {
		if (select == null) {
			select = new JButton();
			select.setText("Select");
			select.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							try {
								selectHostCertificate(getHostCertificatesTable()
										.getSelectedHostCertificate());
							} catch (Exception ex) {
								ErrorDialog.showError(ex);
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

		return select;
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
					getQuery().setEnabled(false);
					getSelect().setEnabled(false);
					Runner runner = new Runner() {
						public void execute() {
							findHostCertificates();
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

	private void findHostCertificates() {
		getProgressPanel().showProgress("Searching...");
		this.getHostCertificatesTable().clearTable();
		try {
			HostCertificateFilter filter = new HostCertificateFilter();

			if (Utils.clean(getRecordId().getText()) != null) {
				try {
					filter.setId(new BigInteger(getRecordId().getText()));
				} catch (NumberFormatException nfe) {
					throw new Exception(
							"The record id must be an integer value.");
				}
			}

			if (Utils.clean(getHost().getText()) != null) {
				filter.setHost(getHost().getText());
			}

			if (Utils.clean(getSerialNumber().getText()) != null) {
				try {
					filter.setSerialNumber(new BigInteger(getSerialNumber()
							.getText()));
				} catch (NumberFormatException nfe) {
					throw new Exception(
							"The serial number must be an integer value.");
				}
			}

			if (Utils.clean(getSubject().getText()) != null) {
				filter.setSubject(getSubject().getText());
			}

			if (getStatus().getStatus() != null) {
				filter.setStatus(getStatus().getStatus());
			}

			if (Utils.clean(getOwnerId().getText()) != null) {
				filter.setOwner(getOwnerId().getText());
			}

			if (getExpiration().getSelectedItem() instanceof Boolean) {
				filter
						.setIsExpired((Boolean) getExpiration()
								.getSelectedItem());
			}

			GridAdministrationClient client = this.session.getSession()
					.getAdminClient();
			List<HostCertificateRecord> certs = client
					.findHostCertificates(filter);
			for (int i = 0; i < certs.size(); i++) {
				this.getHostCertificatesTable()
						.addHostCertificate(certs.get(i));
			}

			getProgressPanel().stopProgress(
					certs.size() + " host certificate(s) found.");

		} catch (Exception e) {
			getProgressPanel().stopProgress("Error");
			ErrorDialog.showError(Utils.getExceptionMessage(e),e);
		} finally {
			this.getQuery().setEnabled(true);
			getSelect().setEnabled(true);
		}
	}

	/**
	 * This method initializes criteriaPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCriteriaPanel() {
		if (criteriaPanel == null) {
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridy = 5;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.anchor = GridBagConstraints.WEST;
			gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints19.gridx = 1;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints18.gridy = 5;
			jLabel6 = new JLabel();
			jLabel6.setText("Expiration");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 1;
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridy = 6;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.gridy = 6;
			jLabel5 = new JLabel();
			jLabel5.setText("Owner");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridy = 3;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 4;
			jLabel4 = new JLabel();
			jLabel4.setText("Status");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridy = 4;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridy = 3;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridx = 0;
			jLabel3 = new JLabel();
			jLabel3.setText("Subject");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 2;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridx = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Serial Number");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Host");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.weightx = 1.0;
			jLabel = new JLabel();
			jLabel.setText("Record Id");
			criteriaPanel = new JPanel();
			criteriaPanel.setLayout(new GridBagLayout());
			criteriaPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Search Criteria",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			criteriaPanel.add(jLabel, gridBagConstraints5);
			criteriaPanel.add(getRecordId(), gridBagConstraints3);
			criteriaPanel.add(jLabel1, gridBagConstraints6);
			criteriaPanel.add(getHost(), gridBagConstraints7);
			criteriaPanel.add(jLabel2, gridBagConstraints8);
			criteriaPanel.add(getSerialNumber(), gridBagConstraints9);
			criteriaPanel.add(jLabel3, gridBagConstraints10);
			criteriaPanel.add(getStatus(), gridBagConstraints11);
			criteriaPanel.add(jLabel4, gridBagConstraints12);
			criteriaPanel.add(getSubject(), gridBagConstraints13);
			criteriaPanel.add(jLabel5, gridBagConstraints14);
			criteriaPanel.add(getOwnerPanel(), gridBagConstraints15);
			criteriaPanel.add(jLabel6, gridBagConstraints18);
			criteriaPanel.add(getExpiration(), gridBagConstraints19);
		}
		return criteriaPanel;
	}

	/**
	 * This method initializes recordId
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getRecordId() {
		if (recordId == null) {
			recordId = new JTextField();
		}
		return recordId;
	}

	/**
	 * This method initializes host
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getHost() {
		if (host == null) {
			host = new JTextField();
		}
		return host;
	}

	/**
	 * This method initializes serialNumber
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSerialNumber() {
		if (serialNumber == null) {
			serialNumber = new JTextField();
		}
		return serialNumber;
	}

	/**
	 * This method initializes status
	 * 
	 * @return gov.nih.nci.cagrid.dorian.ui.ifs.HostCertificateStatusComboBox
	 */
	private HostCertificateStatusComboBox getStatus() {
		if (status == null) {
			status = new HostCertificateStatusComboBox(true);
		}
		return status;
	}

	/**
	 * This method initializes subject
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSubject() {
		if (subject == null) {
			subject = new JTextField();
		}
		return subject;
	}

	/**
	 * This method initializes ownerPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getOwnerPanel() {
		if (ownerPanel == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.gridy = 0;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints16.weightx = 1.0;
			ownerPanel = new JPanel();
			ownerPanel.setLayout(new GridBagLayout());
			ownerPanel.add(getOwnerId(), gridBagConstraints16);
			ownerPanel.add(getFindUser(), gridBagConstraints17);
		}
		return ownerPanel;
	}

	/**
	 * This method initializes ownerId
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getOwnerId() {
		if (ownerId == null) {
			ownerId = new JTextField();
		}
		return ownerId;
	}

	/**
	 * This method initializes findUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFindUser() {
		if (findUser == null) {
			findUser = new JButton();
			findUser.setText("Find...");
			findUser.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					UserSearchDialog dialog = new UserSearchDialog();
					dialog.setModal(true);
					GridApplication.getContext().showDialog(dialog);
					if (dialog.getSelectedUser() != null) {
						ownerId.setText(dialog.getSelectedUser());
					}
				}
			});
		}
		return findUser;
	}

	/**
	 * This method initializes expiration
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getExpiration() {
		if (expiration == null) {
			expiration = new JComboBox();
			expiration.addItem("");
			expiration.addItem(Boolean.TRUE);
			expiration.addItem(Boolean.FALSE);
		}
		return expiration;
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new TitlePanel("Host Certificates",
					"Search for requested and issued host certificates.");
		}
		return titlePanel;
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

	public HostCertificateRecord getSelectedHostCertificate() {
		return selectedHostCertificate;
	}

}
