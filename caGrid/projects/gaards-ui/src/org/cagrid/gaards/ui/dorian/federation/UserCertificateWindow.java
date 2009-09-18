package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.cert.X509Certificate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.UserCertificateRecord;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.ui.common.CertificateInformationComponent;
import org.cagrid.gaards.ui.common.GAARDSLookAndFeel;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.dorian.DorianSession;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

public class UserCertificateWindow extends ApplicationComponent implements
		DorianSessionProvider {

	private final static String DETAILS_PANEL = "Details"; // @jve:decl-index=0:

	private final static String AUDIT_PANEL = "Audit";

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private UserCertificateRecord record;

	private X509Certificate certificate;

	private JPanel informationPanel = null;

	private JLabel jLabel = null;

	private JLabel jLabel2 = null;

	private JLabel jLabel3 = null;

	private JLabel jLabel4 = null;

	private JLabel jLabel5 = null;

	private JLabel jLabel6 = null;

	private JTextField serialNumber = null;

	private JTextField subject = null;

	private JTextField issuer = null;

	private JTextField created = null;

	private JTextField expires = null;

	private UserCertificateStatusComboBox status = null;

	private JPanel notesPanel = null;

	private JScrollPane jScrollPane = null;

	private JTextArea notes = null;

	private JPanel titlePanel = null;

	private JLabel logo = null;

	private JLabel jLabel1 = null;

	private JLabel jLabel7 = null;

	private JPanel buttonPanel = null;

	private JButton update = null;

	private JButton viewCertificate = null;

	private DorianSession session;

	private ProgressPanel progressPanel = null;

	private JTabbedPane content = null;

	private JPanel details = null;

	private FederationAuditPanel auditPanel = null;

	/**
	 * This is the default constructor
	 */
	public UserCertificateWindow(DorianSession session,
			UserCertificateRecord record) throws Exception {
		super();
		this.record = record;
		this.session = session;
		this.certificate = CertUtil.loadCertificate(record.getCertificate()
				.getCertificateAsString());
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 350);
		this.setContentPane(getJContentPane());
		this.setTitle("User Certificate (" + record.getSerialNumber() + ")");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.weighty = 1.0;
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridy = 1;
			gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints21.weightx = 1.0;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.gridx = 0;
			gridBagConstraints20.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints20.weightx = 1.0D;
			gridBagConstraints20.gridy = 2;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.weightx = 1.0D;
			gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.anchor = GridBagConstraints.CENTER;
			gridBagConstraints15.gridy = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getTitlePanel(), gridBagConstraints15);
			jContentPane.add(getContent(), gridBagConstraints21);
			jContentPane.add(getProgressPanel(), gridBagConstraints20);

		}
		return jContentPane;
	}

	/**
	 * This method initializes informationPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getInformationPanel() {
		if (informationPanel == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridy = 5;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridy = 4;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 3;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridy = 5;
			jLabel6 = new JLabel();
			jLabel6.setText("Status");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 4;
			jLabel5 = new JLabel();
			jLabel5.setText("Expires");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridy = 3;
			jLabel4 = new JLabel();
			jLabel4.setText("Created");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.weighty = 30.0D;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			jLabel3 = new JLabel();
			jLabel3.setText("Issuer");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			jLabel2 = new JLabel();
			jLabel2.setText("Subject");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Serial Number");
			informationPanel = new JPanel();
			informationPanel.setLayout(new GridBagLayout());
			informationPanel.add(jLabel, gridBagConstraints);
			informationPanel.add(jLabel2, gridBagConstraints2);
			informationPanel.add(jLabel3, gridBagConstraints3);
			informationPanel.add(jLabel4, gridBagConstraints4);
			informationPanel.add(jLabel5, gridBagConstraints5);
			informationPanel.add(jLabel6, gridBagConstraints6);
			informationPanel.add(getSerialNumber(), gridBagConstraints7);
			informationPanel.add(getSubject(), gridBagConstraints10);
			informationPanel.add(getIssuer(), gridBagConstraints11);
			informationPanel.add(getCreated(), gridBagConstraints12);
			informationPanel.add(getExpires(), gridBagConstraints13);
			informationPanel.add(getStatus(), gridBagConstraints14);
		}
		return informationPanel;
	}

	/**
	 * This method initializes serialNumber
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSerialNumber() {
		if (serialNumber == null) {
			serialNumber = new JTextField();
			serialNumber.setEditable(false);
			serialNumber.setText(String.valueOf(this.record.getSerialNumber()));
		}
		return serialNumber;
	}

	/**
	 * This method initializes subject
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSubject() {
		if (subject == null) {
			subject = new JTextField();
			subject.setEditable(false);
			subject.setText(this.certificate.getSubjectDN().getName());
		}
		return subject;
	}

	/**
	 * This method initializes issuer
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getIssuer() {
		if (issuer == null) {
			issuer = new JTextField();
			issuer.setEditable(false);
			issuer.setText(this.certificate.getIssuerDN().getName());
		}
		return issuer;
	}

	/**
	 * This method initializes created
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCreated() {
		if (created == null) {
			created = new JTextField();
			created.setEditable(false);
			created.setText(this.certificate.getNotBefore().toString());
		}
		return created;
	}

	/**
	 * This method initializes expires
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getExpires() {
		if (expires == null) {
			expires = new JTextField();
			expires.setEditable(false);
			expires.setText(this.certificate.getNotAfter().toString());
		}
		return expires;
	}

	/**
	 * This method initializes status
	 * 
	 * @return javax.swing.JComboBox
	 */
	private UserCertificateStatusComboBox getStatus() {
		if (status == null) {
			status = new UserCertificateStatusComboBox();
			status.setSelectedItem(record.getStatus());
		}
		return status;
	}

	/**
	 * This method initializes notesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getNotesPanel() {
		if (notesPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 0;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.weighty = 1.0;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridx = 0;
			notesPanel = new JPanel();
			notesPanel.setLayout(new GridBagLayout());
			notesPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Notes", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			notesPanel.add(getJScrollPane(), gridBagConstraints9);
		}
		return notesPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getNotes());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes notes
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getNotes() {
		if (notes == null) {
			notes = new JTextArea();
			notes.setWrapStyleWord(true);
			notes.setLineWrap(true);
			notes.setText(record.getNotes());
		}
		return notes;
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 1;
			gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints18.weightx = 1.0D;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.gridy = 1;
			jLabel7 = new JLabel();
			jLabel7.setText(this.record.getGridIdentity());
			jLabel7.setFont(new Font("Arial", Font.ITALIC, 12));
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.gridy = 0;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.weightx = 1.0D;
			gridBagConstraints17.gridx = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("User Certificate");
			jLabel1.setFont(new Font("Arial", Font.BOLD, 14));
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints16.gridheight = 2;
			gridBagConstraints16.gridx = 0;
			logo = new JLabel(GAARDSLookAndFeel.getLogoNoText32x32());
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.add(logo, gridBagConstraints16);
			titlePanel.add(jLabel1, gridBagConstraints17);
			titlePanel.add(jLabel7, gridBagConstraints18);
		}
		return titlePanel;
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
			buttonPanel.add(getUpdate(), null);
			buttonPanel.add(getViewCertificate(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes update
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getUpdate() {
		if (update == null) {
			update = new JButton();
			update.setText("Update");
			update.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							updateUserCertificate();
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
		return update;
	}

	/**
	 * This method initializes viewCertificate
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getViewCertificate() {
		if (viewCertificate == null) {
			viewCertificate = new JButton();
			viewCertificate.setText("View Certificate");
			viewCertificate
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							GridApplication
									.getContext()
									.addApplicationComponent(
											new CertificateInformationComponent(
													certificate), 700, 550);
						}
					});
		}
		return viewCertificate;
	}

	private void updateUserCertificate() {
		try {
			getUpdate().setEnabled(false);
			getViewCertificate().setEnabled(false);
			getProgressPanel().showProgress("Updating...");
			GridAdministrationClient client = this.session.getAdminClient();
			client.updateUserCertificateRecord(record.getSerialNumber(),
					getStatus().getSelectedUserStatus(), Utils.clean(getNotes()
							.getText()));
			record.setStatus(getStatus().getSelectedUserStatus());
			record.setNotes(Utils.clean(getNotes().getText()));
			getProgressPanel()
					.stopProgress("Certificate successfully updated.");
		} catch (Exception e) {
			getProgressPanel().stopProgress("Error");
			FaultUtil.printFault(e);
			ErrorDialog.showError(e);
		} finally {
			getUpdate().setEnabled(true);
			getViewCertificate().setEnabled(true);
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
	 * This method initializes content
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getContent() {
		if (content == null) {
			content = new JTabbedPane();
			content.addTab(DETAILS_PANEL, null, getDetails(), null);
			content.addTab(AUDIT_PANEL, null, getAuditPanel(), null);
		}
		return content;
	}

	/**
	 * This method initializes details
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDetails() {
		if (details == null) {
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.gridy = 2;
			gridBagConstraints19.weightx = 1.0D;
			gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.gridwidth = 0;
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.ipady = 0;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.weighty = 1.0D;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			details = new JPanel();
			details.setLayout(new GridBagLayout());
			details.add(getInformationPanel(), gridBagConstraints1);
			details.add(getNotesPanel(), gridBagConstraints8);
			details.add(getButtonPanel(), gridBagConstraints19);
		}
		return details;
	}

	/**
	 * This method initializes auditPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private FederationAuditPanel getAuditPanel() {
		if (auditPanel == null) {
			auditPanel = new FederationAuditPanel(this,
					FederationAuditPanel.USER_CERTIFICATE_MODE, String
							.valueOf(record.getSerialNumber()));
			auditPanel.setProgess(getProgressPanel());
		}
		return auditPanel;
	}

	public DorianSession getSession() throws Exception {
		return this.session;
	}

}
