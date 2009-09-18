package org.cagrid.gaards.ui.common;

import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.gsi.GlobusCredential;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ProxyInformationComponent.java,v 1.3 2005/12/03 07:18:56
 *          langella Exp $
 */
public class CredentialManagerComponent extends ApplicationComponent {
	
	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel buttonPanel = null;

	private JPanel proxyPanel = null;

	private JComboBox proxyComboBox = null;

	private JButton viewCertificateButton = null;

	private JButton saveCredentialButton = null;

	private JButton setDefaultProxyButton = null;

	private static final String DEFAULT_PROXY = "Globus Default Proxy";

	private JButton deleteCredentialButton = null;

	private CredentialCaddy defaultProxy;

	private CredentialPanel proxyInfoPanel = null;

	private JButton importCredential = null;

	private JPanel titlePanel = null;

	/**
	 * This is the default constructor
	 */
	public CredentialManagerComponent() {
		super();
		initialize();
		List creds = CredentialManager.getInstance().getCredentials();
		defaultProxy = new CredentialCaddy(DEFAULT_PROXY, null);
		getProxyComboBox().addItem(defaultProxy);
		for (int i = 0; i < creds.size(); i++) {
			getProxyComboBox().addItem(
					new CredentialCaddy((GlobusCredential) creds.get(i)));
		}
	}

	public CredentialManagerComponent(GlobusCredential cred) {
		this();
		getProxyComboBox().setSelectedItem(new CredentialCaddy(cred));
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setFrameIcon(GAARDSLookAndFeel.getCertificateIcon());
		this.setTitle("Credential Manager");
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
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridy = 2;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.weightx = 1.0D;
			gridBagConstraints14.gridy = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.SOUTH;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			mainPanel.add(getButtonPanel(), gridBagConstraints4);
			mainPanel.add(getProxyPanel(), gridBagConstraints14);
			mainPanel.add(getProxyInfoPanel(), gridBagConstraints);
			mainPanel.add(getTitlePanel(), gridBagConstraints1);
		}
		return mainPanel;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getViewCertificateButton(), null);
			buttonPanel.add(getImportCredential(), null);
			buttonPanel.add(getSaveCredentialButton(), null);
			buttonPanel.add(getDeleteCredentialButton(), null);
			buttonPanel.add(getSetDefaultProxyButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes proxyPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getProxyPanel() {
		if (proxyPanel == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 0;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
			proxyPanel = new JPanel();
			proxyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, "Select Credential",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					org.cagrid.grape.LookAndFeel.getPanelLabelColor()));
			proxyPanel.setLayout(new GridBagLayout());
			proxyPanel.add(getProxyComboBox(), gridBagConstraints15);
		}
		return proxyPanel;
	}

	/**
	 * This method initializes proxy
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getProxyComboBox() {
		if (proxyComboBox == null) {
			proxyComboBox = new JComboBox();
			/*
			 * proxy.addItemListener(new java.awt.event.ItemListener() { public
			 * void itemStateChanged(java.awt.event.ItemEvent e) { } });
			 */
			proxyComboBox
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							CredentialCaddy caddy = (CredentialCaddy) getProxyComboBox()
									.getSelectedItem();
							if (caddy != null) {
								if (caddy.getIdentity() == DEFAULT_PROXY) {
									try {
										proxyInfoPanel.clearProxy();
										caddy.setProxy(ProxyUtil
												.getDefaultProxy());
									} catch (Exception ex) {
										return;
									}
								}
								proxyInfoPanel.showProxy(caddy.getProxy());
							}
						}
					});
		}
		return proxyComboBox;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getViewCertificateButton() {
		if (viewCertificateButton == null) {
			viewCertificateButton = new JButton();
			viewCertificateButton.setText("View Certificate");
			viewCertificateButton
					.addActionListener(new java.awt.event.ActionListener() {

						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								proxyInfoPanel.getCertificates().doubleClick();
							} catch (Exception ex) {
								ErrorDialog
										.showError(
												"An unexpected error in loading the requested certificate.",
												ex);
							}
						}
					});
		}
		return viewCertificateButton;
	}

	/**
	 * This method initializes saveProxy
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSaveCredentialButton() {
		if (saveCredentialButton == null) {
			saveCredentialButton = new JButton();
			saveCredentialButton.setText("Save Credential");
			saveCredentialButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							saveCredentialNow();

						}
					});
		}
		return saveCredentialButton;
	}

	private void saveCredentialNow() {
		try {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				CredentialCaddy caddy = (CredentialCaddy) getProxyComboBox()
						.getSelectedItem();
				ProxyUtil.saveProxy(caddy.getProxy(), fc.getSelectedFile()
						.getAbsolutePath());
			}
		} catch (Exception e) {
			ErrorDialog
					.showError(
							"An unexpected error occurred in saving the currently selected proxy!!!",
							e);
		}
	}

	/**
	 * This method initializes setDefaultProxy
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSetDefaultProxyButton() {
		if (setDefaultProxyButton == null) {
			setDefaultProxyButton = new JButton();
			setDefaultProxyButton.setText("Set Default");
			setDefaultProxyButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								CredentialCaddy caddy = (CredentialCaddy) getProxyComboBox()
										.getSelectedItem();
								ProxyUtil.saveProxyAsDefault(caddy.getProxy());
								GridApplication.getContext().showMessage(
										"Selected proxy saved as the default");
							} catch (Exception ex) {
								ErrorDialog
										.showError(
												"An unexpected error occurred in saving the currently selected proxy!!!",
												ex);
							}
						}
					});
		}
		return setDefaultProxyButton;
	}

	/**
	 * This method initializes deleteProxy
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteCredentialButton() {
		if (deleteCredentialButton == null) {
			deleteCredentialButton = new JButton();
			deleteCredentialButton.setText("Delete Credential");
			deleteCredentialButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							CredentialCaddy caddy = (CredentialCaddy) getProxyComboBox()
									.getSelectedItem();
							if (caddy != null) {
								proxyInfoPanel.clearProxy();
								getProxyComboBox().removeItemAt(
										getProxyComboBox().getSelectedIndex());
								if (caddy.getIdentity() == DEFAULT_PROXY) {
									ProxyUtil.destroyDefaultProxy();
								} else {
									CredentialManager.getInstance()
											.deleteCredential(caddy.getProxy());

								}
							}
						}
					});
		}
		return deleteCredentialButton;
	}

	/**
	 * This method initializes proxyInformation
	 * 
	 * @return javax.swing.JPanel
	 */
	private CredentialPanel getProxyInfoPanel() {
		if (proxyInfoPanel == null) {
			proxyInfoPanel = new CredentialPanel();
		}
		return proxyInfoPanel;
	}

	/**
	 * This method initializes importCredential
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getImportCredential() {
		if (importCredential == null) {
			importCredential = new JButton();
			importCredential.setText("Import Credential");
			importCredential
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							ImportCredentialDialog dialog = new ImportCredentialDialog(
									GridApplication.getContext()
											.getApplication());
							dialog.setModal(true);
							GridApplication.getContext().showDialog(dialog);
							GlobusCredential cred = dialog
									.getImportedCredential();
							if (cred != null) {
								try {
									CredentialManager.getInstance()
											.addCredential(cred);
									getProxyComboBox().addItem(
											new CredentialCaddy(
													(GlobusCredential) cred));
									getProxyComboBox().setSelectedItem(
											new CredentialCaddy(cred));
								} catch (Exception ex) {
									ex.printStackTrace();
									ErrorDialog.showError(ex);
								}
							}
						}
					});
		}
		return importCredential;
	}

	/**
	 * This method initializes titlePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new TitlePanel("Credential Manager","View, import, and export grid credentials.");
		}
		return titlePanel;
	}
}
