package org.cagrid.gaards.ui.common;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.globus.gsi.GlobusCredential;

public class ImportCredentialDialog extends JDialog {

	private static final String CERTIFICATE_PRIVATE_KEY_TYPE = "Certificate / Private Key"; // @jve:decl-index=0:

	private static final String PROXY_TYPE = "Grid Proxy";

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel selectorPanel = null;

	private JPanel inputPanel = null;

	private JPanel buttonPanel = null;

	private JButton importButton = null;

	private JButton cancel = null;

	private CardLayout inputLayout;

	private JComboBox credentialType = null;

	private JPanel certificateImportPanel = null;

	private JPanel proxyInputPanel = null;

	private GlobusCredential importedCredential = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JLabel jLabel2 = null;

	private JTextField certificate = null;

	private JTextField privateKey = null;

	private JButton browseCertificate = null;

	private JButton browsePrivateKey = null;

	private JPasswordField keyPassword = null;

	private JLabel proxy = null;

	private JTextField gridProxy = null;

	private JButton browseProxy = null;

	private File lastDir = null;

	/**
	 * @param owner
	 */
	public ImportCredentialDialog(Frame owner) {
		super(owner, "Import Credential");
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 250);
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
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getSelectorPanel(), gridBagConstraints);
			mainPanel.add(getInputPanel(), gridBagConstraints1);
			mainPanel.add(getButtonPanel(), gridBagConstraints2);
		}
		return mainPanel;
	}

	/**
	 * This method initializes selectorPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSelectorPanel() {
		if (selectorPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.weightx = 1.0;
			selectorPanel = new JPanel();
			selectorPanel.setLayout(new GridBagLayout());
			selectorPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Select Credential Type",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			selectorPanel.add(getCredentialType(), gridBagConstraints3);
		}
		return selectorPanel;
	}

	/**
	 * This method initializes inputPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getInputPanel() {
		if (inputPanel == null) {
			inputPanel = new JPanel();
			inputLayout = new CardLayout();
			inputPanel.setLayout(inputLayout);
			inputPanel.add(getCertificateImportPanel(),
					getCertificateImportPanel().getName());
			inputPanel
					.add(getProxyInputPanel(), getProxyInputPanel().getName());
		}
		return inputPanel;
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
			buttonPanel.add(getImportButton(), null);
			buttonPanel.add(getCancel(), null);
		}
		return buttonPanel;
	}

	private void importCredential() {
		String selected = (String) getCredentialType().getSelectedItem();
		if (selected.equals(CERTIFICATE_PRIVATE_KEY_TYPE)) {
			try {
				String certPath = Utils.clean(getCertificate().getText());
				String keyPath = Utils.clean(getPrivateKey().getText());
				String password = Utils.clean(new String(getKeyPassword()
						.getPassword()));
				if (certPath == null) {
					GridApplication.getContext().showMessage(
							"Please specify a certificate!!!");
					return;
				}

				if (keyPath == null) {
					GridApplication.getContext().showMessage(
							"Please specify a private key!!!");
					return;
				}

				File certFile = new File(certPath);
				if (!certFile.exists()) {
					GridApplication.getContext().showMessage(
							"The certificate you specified does not exist!!!");
					return;
				}

				File keyFile = new File(keyPath);
				if (!keyFile.exists()) {
					GridApplication.getContext().showMessage(
							"The private key you specified does not exist!!!");
					return;
				}

				X509Certificate cert = CertUtil.loadCertificate(certFile);
				PrivateKey key = KeyUtil.loadPrivateKey(keyFile, password);
				importedCredential = new GlobusCredential(key,
						new X509Certificate[] { cert });
				dispose();
				return;
			} catch (Exception ex) {
				ex.printStackTrace();
				GridApplication.getContext().showMessage(
						Utils.getExceptionMessage(ex));
				return;
			}
		} else if (selected.equals(PROXY_TYPE)) {
			try {
				String proxyPath = Utils.clean(getGridProxy().getText());
				if (proxyPath == null) {
					GridApplication.getContext().showMessage(
							"Please specify a proxy!!!");
					return;
				}

				File proxyFile = new File(proxyPath);
				if (!proxyFile.exists()) {
					GridApplication.getContext().showMessage(
							"The proxy you specified does not exist!!!");
					return;
				}
				importedCredential = new GlobusCredential(new FileInputStream(
						proxyFile));
				dispose();
				return;
			} catch (Exception ex) {
				ex.printStackTrace();
				GridApplication.getContext().showMessage(
						Utils.getExceptionMessage(ex));
				return;
			}

		}

	}

	/**
	 * This method initializes importButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getImportButton() {
		if (importButton == null) {
			importButton = new JButton();
			importButton.setText("Import Credential");
			importButton.setIcon(LookAndFeel.getImportIcon());
			importButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					importCredential();
				}
			});
		}
		return importButton;
	}

	/**
	 * This method initializes cancel
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancel() {
		if (cancel == null) {
			cancel = new JButton();
			cancel.setText("Cancel");
			cancel.setIcon(PortalLookAndFeel.getCloseIcon());
			cancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancel;
	}

	/**
	 * This method initializes credentialType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCredentialType() {
		if (credentialType == null) {
			getInputPanel();
			credentialType = new JComboBox();
			credentialType
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							inputLayout.show(getInputPanel(),
									(String) getCredentialType()
											.getSelectedItem());
						}
					});
			credentialType.addItem(CERTIFICATE_PRIVATE_KEY_TYPE);
			credentialType.addItem(PROXY_TYPE);
		}
		return credentialType;
	}

	/**
	 * This method initializes certificateImportPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCertificateImportPanel() {
		if (certificateImportPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridwidth = 1;
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.weightx = 1.0;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridx = 2;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridy = 0;
			gridBagConstraints9.gridx = 2;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.weightx = 1.0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridy = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Key Password");
			jLabel1 = new JLabel();
			jLabel1.setText("Private Key");
			jLabel = new JLabel();
			jLabel.setText("Certificate");
			certificateImportPanel = new JPanel();
			certificateImportPanel.setLayout(new GridBagLayout());
			certificateImportPanel.setName(CERTIFICATE_PRIVATE_KEY_TYPE);
			certificateImportPanel.setBorder(BorderFactory.createTitledBorder(
					null, "Import Certifcate/Private Key",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			certificateImportPanel.add(jLabel, gridBagConstraints4);
			certificateImportPanel.add(jLabel1, gridBagConstraints5);
			certificateImportPanel.add(jLabel2, gridBagConstraints6);
			certificateImportPanel.add(getCertificate(), gridBagConstraints7);
			certificateImportPanel.add(getPrivateKey(), gridBagConstraints8);
			certificateImportPanel.add(getBrowseCertificate(),
					gridBagConstraints9);
			certificateImportPanel.add(getBrowsePrivateKey(),
					gridBagConstraints10);
			certificateImportPanel.add(getKeyPassword(), gridBagConstraints11);
		}
		return certificateImportPanel;
	}

	/**
	 * This method initializes proxyInputPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getProxyInputPanel() {
		if (proxyInputPanel == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 2;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridy = 0;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.gridx = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.weightx = 1.0;
			proxy = new JLabel();
			proxy.setText("Proxy");
			proxyInputPanel = new JPanel();
			proxyInputPanel.setLayout(new GridBagLayout());
			proxyInputPanel.setName(PROXY_TYPE);
			proxyInputPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Import Grid Proxy", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			proxyInputPanel.add(proxy, gridBagConstraints13);
			proxyInputPanel.add(getGridProxy(), gridBagConstraints12);
			proxyInputPanel.add(getBrowseProxy(), gridBagConstraints14);
		}
		return proxyInputPanel;
	}

	public GlobusCredential getImportedCredential() {
		return importedCredential;
	}

	/**
	 * This method initializes certificate
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificate() {
		if (certificate == null) {
			certificate = new JTextField();
		}
		return certificate;
	}

	/**
	 * This method initializes privateKey
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPrivateKey() {
		if (privateKey == null) {
			privateKey = new JTextField();
		}
		return privateKey;
	}

	/**
	 * This method initializes browseCertificate
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowseCertificate() {
		if (browseCertificate == null) {
			browseCertificate = new JButton();
			browseCertificate.setText("Browse....");
			browseCertificate
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							String path = getPath();
							if (path != null) {
								getCertificate().setText(path);
							}
						}
					});
		}
		return browseCertificate;
	}

	private String getPath() {
		JFileChooser fc = new JFileChooser(this.lastDir);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			lastDir = fc.getSelectedFile().getParentFile();
			return fc.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}

	/**
	 * This method initializes browsePrivateKey
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowsePrivateKey() {
		if (browsePrivateKey == null) {
			browsePrivateKey = new JButton();
			browsePrivateKey.setName("browsePrivateKey");
			browsePrivateKey.setText("Browse....");
			browsePrivateKey
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							String path = getPath();
							if (path != null) {
								getPrivateKey().setText(path);
							}
						}
					});
		}
		return browsePrivateKey;
	}

	/**
	 * This method initializes keyPassword
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getKeyPassword() {
		if (keyPassword == null) {
			keyPassword = new JPasswordField();
		}
		return keyPassword;
	}

	/**
	 * This method initializes gridProxy
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridProxy() {
		if (gridProxy == null) {
			gridProxy = new JTextField();
		}
		return gridProxy;
	}

	/**
	 * This method initializes browseProxy
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowseProxy() {
		if (browseProxy == null) {
			browseProxy = new JButton();
			browseProxy.setText("Browse...");
			browseProxy.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String path = getPath();
					if (path != null) {
						getGridProxy().setText(path);
					}
				}
			});
		}
		return browseProxy;
	}
}
