package org.cagrid.gaards.ui.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: CertificatePanel.java,v 1.4 2008-11-20 15:29:42 langella Exp $
 */
public class CertificatePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JPanel certInfoPanel = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JLabel versionLabel = null;
	private JLabel jLabel7 = null;
	private JTextField certficateSignatureAlgorithm = null;
	private JTextField certificateType = null;
	private JTextField certificateVersion = null;
	private JTextField certificateSerialNumber = null;
	private JTextField certificateIssuer = null;
	private JTextField certificateExpires = null;
	private JTextField certificateCreated = null;
	private JTextField certificateSubject = null;
	private JPanel buttonPanel = null;
	private JButton loadButton = null;
	private JButton saveButton = null;
	private X509Certificate certificate;
	private JPanel certExtensionsPanel = null;
	private JScrollPane jScrollPane = null;
	private CertificateExtensionsTable certificateExtensionsTable = null;

	/**
	 * This is the default constructor
	 */
	public CertificatePanel() {
		super();
		initialize();
	}

	public CertificatePanel(X509Certificate cert) {
		super();
		initialize();
		this.certificate = cert;
		setCertificate(cert);
	}

	public void setAllowExport(boolean allow) {
		this.getSaveButton().setVisible(allow);
	}

	public void setAllowImport(boolean allow) {
		this.getLoadButton().setVisible(allow);
	}

	public void setCertificate(X509Certificate cert) {
		if (cert != null) {
			this.certificate = cert;
			this.getCertificateCreated()
					.setText(cert.getNotBefore().toString());
			this.getCertificateExpires().setText(cert.getNotAfter().toString());
			this.getCertificateIssuer().setText(cert.getIssuerDN().getName());
			this.getCertificateSerialNumber().setText(
					cert.getSerialNumber().toString());
			this.getCertificateSubject().setText(cert.getSubjectDN().getName());
			this.getCertificateSignatureAlgorithm().setText(
					cert.getSigAlgName());
			this.getCertificateType().setText(cert.getType());
			this.getCertificateVersion().setText(
					String.valueOf(cert.getVersion()));
			this.getCertificateExtensionsTable().clearTable();
			(this.getCertificateExtensionsTable()).addCertificate(cert);
		}
	}

	public void clearCertificate() {
		this.getCertificateCreated().setText("");
		this.getCertificateExpires().setText("");
		this.getCertificateIssuer().setText("");
		this.getCertificateSerialNumber().setText("");
		this.getCertificateSubject().setText("");
		this.getCertificateSignatureAlgorithm().setText("");
		this.getCertificateType().setText("");
		this.getCertificateVersion().setText("");
		this.getCertificateExtensionsTable().clearTable();
		this.certificate = null;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
		gridBagConstraints19.gridx = 0;
		gridBagConstraints19.weightx = 1.0D;
		gridBagConstraints19.weighty = 1.0D;
		gridBagConstraints19.insets = new java.awt.Insets(2, 2, 2, 2);
		gridBagConstraints19.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints19.gridy = 1;
		GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
		gridBagConstraints17.gridx = 0;
		gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
		gridBagConstraints17.gridy = 2;
		GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
		gridBagConstraints16.gridx = 0;
		gridBagConstraints16.anchor = java.awt.GridBagConstraints.NORTH;
		gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
		gridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints16.weightx = 1.0D;
		gridBagConstraints16.weighty = 1.0D;
		gridBagConstraints16.gridy = 0;
		this.setLayout(new GridBagLayout());
		this.add(getCertInfoPanel(), gridBagConstraints16);
		this.add(getButtonPanel(), gridBagConstraints17);
		this.add(getCertExtensionsPanel(), gridBagConstraints19);
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCertInfoPanel() {
		if (certInfoPanel == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints15.gridx = 1;
			gridBagConstraints15.gridy = 0;
			gridBagConstraints15.weightx = 1.0D;
			gridBagConstraints15.weighty = 0.0D;
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.gridx = 1;
			gridBagConstraints14.gridy = 4;
			gridBagConstraints14.weightx = 1.0D;
			gridBagConstraints14.weighty = 0.0D;
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.gridx = 1;
			gridBagConstraints13.gridy = 5;
			gridBagConstraints13.weightx = 1.0D;
			gridBagConstraints13.weighty = 0.0D;
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0D;
			gridBagConstraints12.weighty = 0.0D;
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.weighty = 0.0D;
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridy = 8;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 7;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 6;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 6;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			jLabel7 = new JLabel();
			jLabel7.setText("Signature Algorithm");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 8;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			versionLabel = new JLabel();
			versionLabel.setText("Version");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 7;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			jLabel5 = new JLabel();
			jLabel5.setText("Type");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 4;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			jLabel4 = new JLabel();
			jLabel4.setText("Created");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 5;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			jLabel3 = new JLabel();
			jLabel3.setText("Expires");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			jLabel2 = new JLabel();
			jLabel2.setText("Issuer");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 2;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			jLabel1 = new JLabel();
			jLabel1.setText("Serial Number");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 0.0D;
			gridBagConstraints.weighty = 0.0D;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			jLabel = new JLabel();
			jLabel.setText("Subject");
			certInfoPanel = new JPanel();
			certInfoPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Certificate Information",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			certInfoPanel.setLayout(new GridBagLayout());
			certInfoPanel.add(jLabel, gridBagConstraints);
			certInfoPanel.add(jLabel1, gridBagConstraints1);
			certInfoPanel.add(jLabel2, gridBagConstraints2);
			certInfoPanel.add(jLabel3, gridBagConstraints3);
			certInfoPanel.add(jLabel4, gridBagConstraints4);
			certInfoPanel.add(jLabel5, gridBagConstraints5);
			certInfoPanel.add(versionLabel, gridBagConstraints6);
			certInfoPanel.add(jLabel7, gridBagConstraints7);
			certInfoPanel.add(getCertificateSignatureAlgorithm(),
					gridBagConstraints8);
			certInfoPanel.add(getCertificateType(), gridBagConstraints9);
			certInfoPanel.add(getCertificateVersion(), gridBagConstraints10);
			certInfoPanel.add(getCertificateSerialNumber(),
					gridBagConstraints11);
			certInfoPanel.add(getCertificateIssuer(), gridBagConstraints12);
			certInfoPanel.add(getCertificateExpires(), gridBagConstraints13);
			certInfoPanel.add(getCertificateCreated(), gridBagConstraints14);
			certInfoPanel.add(getCertificateSubject(), gridBagConstraints15);
		}
		return certInfoPanel;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificateSignatureAlgorithm() {
		if (certficateSignatureAlgorithm == null) {
			certficateSignatureAlgorithm = new JTextField();
			certficateSignatureAlgorithm.setEditable(false);
		}
		return certficateSignatureAlgorithm;
	}

	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificateType() {
		if (certificateType == null) {
			certificateType = new JTextField();
			certificateType.setEditable(false);
		}
		return certificateType;
	}

	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificateVersion() {
		if (certificateVersion == null) {
			certificateVersion = new JTextField();
			certificateVersion.setEditable(false);
		}
		return certificateVersion;
	}

	/**
	 * This method initializes jTextField3
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificateSerialNumber() {
		if (certificateSerialNumber == null) {
			certificateSerialNumber = new JTextField();
			certificateSerialNumber.setEditable(false);
		}
		return certificateSerialNumber;
	}

	/**
	 * This method initializes jTextField4
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificateIssuer() {
		if (certificateIssuer == null) {
			certificateIssuer = new JTextField();
			certificateIssuer.setEditable(false);
		}
		return certificateIssuer;
	}

	/**
	 * This method initializes jTextField5
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificateExpires() {
		if (certificateExpires == null) {
			certificateExpires = new JTextField();
			certificateExpires.setEditable(false);
		}
		return certificateExpires;
	}

	/**
	 * This method initializes jTextField6
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificateCreated() {
		if (certificateCreated == null) {
			certificateCreated = new JTextField();
			certificateCreated.setEditable(false);
		}
		return certificateCreated;
	}

	/**
	 * This method initializes jTextField7
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificateSubject() {
		if (certificateSubject == null) {
			certificateSubject = new JTextField();
			certificateSubject.setEditable(false);
		}
		return certificateSubject;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getLoadButton(), null);
			buttonPanel.add(getSaveButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes loadButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getLoadButton() {
		if (loadButton == null) {
			loadButton = new JButton();
			loadButton.setText("Import Certificate");
			loadButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					importCertificate();

				}
			});
		}
		return loadButton;
	}

	private void importCertificate() {
		clearCertificate();
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				certificate = CertUtil.loadCertificate("BC", new File(fc
						.getSelectedFile().getAbsolutePath()));
				setCertificate(certificate);
			} catch (Exception ex) {
				ErrorDialog.showError(ex);
			}
		}

	}

	private void exportCertificate() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				CertUtil.writeCertificate(certificate, new File(fc
						.getSelectedFile().getAbsolutePath()));
			} catch (Exception ex) {
				ErrorDialog.showError(ex);
			}
		}

	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	/**
	 * This method initializes saveButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					exportCertificate();
				}
			});
			saveButton.setText("Export Certificate");
		}
		return saveButton;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCertExtensionsPanel() {
		if (certExtensionsPanel == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = GridBagConstraints.BOTH;
			gridBagConstraints18.weighty = 1.0;
			gridBagConstraints18.weightx = 1.0;
			certExtensionsPanel = new JPanel();

			certExtensionsPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Certificate Extensions",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			certExtensionsPanel.setLayout(new GridBagLayout());
			certExtensionsPanel.add(getJScrollPane(), gridBagConstraints18);
		}
		return certExtensionsPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getCertificateExtensionsTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes certificateExtensionsTable
	 * 
	 * @return gov.nih.nci.cagrid.gridca.portal.CertificateExtensionsTable
	 */
	private CertificateExtensionsTable getCertificateExtensionsTable() {
		if (certificateExtensionsTable == null) {
			certificateExtensionsTable = new CertificateExtensionsTable();
		}
		return certificateExtensionsTable;
	}

}
