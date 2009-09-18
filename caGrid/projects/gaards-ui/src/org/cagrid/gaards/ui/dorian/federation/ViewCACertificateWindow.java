package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.gaards.ui.common.CertificateInformationComponent;
import org.cagrid.gaards.ui.dorian.DorianHandle;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianServiceListComboBox;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class ViewCACertificateWindow extends ApplicationComponent {
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JPanel mainPanel = null;
	private JPanel servicePanel = null;
	private JPanel buttonPanel = null;
	private JLabel ifsLabel = null;
	private DorianServiceListComboBox ifs = null;
	private JButton viewCAButton = null;
	private JButton close = null;


	/**
	 * This is the default constructor
	 */
	public ViewCACertificateWindow() {
		super();
		initialize();
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setFrameIcon(DorianLookAndFeel.getCertificateIcon());
		this.setTitle("View Dorian CA Certifcate");
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
			jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
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
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.SOUTH;
			gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getServicePanel(), gridBagConstraints);
			mainPanel.add(getButtonPanel(), gridBagConstraints3);
		}
		return mainPanel;
	}


	/**
	 * This method initializes idpPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getServicePanel() {
		if (servicePanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 0;
			ifsLabel = new JLabel();
			ifsLabel.setText("Dorian Service");
			servicePanel = new JPanel();
			servicePanel.setLayout(new GridBagLayout());
			servicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "View Dorian CA Certifcate",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
			servicePanel.add(ifsLabel, gridBagConstraints5);
			servicePanel.add(getIfs(), gridBagConstraints6);
		}
		return servicePanel;
	}


	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getViewCAButton(), null);
			buttonPanel.add(getClose(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes ifs
	 * 
	 * @return javax.swing.JComboBox
	 */
	private DorianServiceListComboBox getIfs() {
		if (ifs == null) {
			ifs = new DorianServiceListComboBox();
		}
		return ifs;
	}


	/**
	 * This method initializes authenticateButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getViewCAButton() {
		if (viewCAButton == null) {
			viewCAButton = new JButton();
			viewCAButton.setText("View CA Certificate");
			viewCAButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							getCACertificate();
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}
			});
			viewCAButton.setIcon(DorianLookAndFeel.getCertificateIcon());
		}
		return viewCAButton;
	}


	private void getCACertificate() {
		try {
			getViewCAButton().setEnabled(false);
			DorianHandle handle = ifs.getSelectedService();
			X509Certificate cert = handle.getUserClient().getCACertificate();
			dispose();
			CertificateInformationComponent cic = new CertificateInformationComponent(cert);
			GridApplication.getContext().addApplicationComponent(cic, 700, 500);
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}
		getViewCAButton().setEnabled(true);
	}


	private JButton getClose() {
		if (close == null) {
			close = new JButton();
			close.setText("Close");
			close.setIcon(LookAndFeel.getCloseIcon());
			close.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return close;
	}

}
