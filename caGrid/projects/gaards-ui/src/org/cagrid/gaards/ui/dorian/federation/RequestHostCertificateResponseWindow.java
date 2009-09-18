package org.cagrid.gaards.ui.dorian.federation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.dorian.federation.HostCertificateStatus;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.ui.common.CertificateInformationComponent;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

public class RequestHostCertificateResponseWindow extends ApplicationComponent {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel buttonPanel = null;

	private JPanel responsePanel = null;

	private JButton close = null;

	private HostCertificateRecord record;

	private File directory;

	private PrivateKey privateKey;

	private JScrollPane jScrollPane = null;

	private JTextArea response = null;

	private JButton viewCertificate = null;

	private JPanel titlePanel = null;

	/**
	 * This is the default constructor
	 */
	public RequestHostCertificateResponseWindow(HostCertificateRecord record,
			PrivateKey privateKey, File directory) {
		super();
		this.directory = directory;
		this.record = record;
		this.privateKey = privateKey;
		initialize();
		reportOut();
	}

	private void reportOut() {
		try {
			directory.mkdirs();
			File keyPath = new File(directory.getAbsolutePath()
					+ File.separator + record.getHost() + "-key.pem");
			KeyUtil.writePrivateKey(this.privateKey, keyPath);
			String str = null;
			if (record.getStatus().equals(HostCertificateStatus.Active)) {
				File certPath = new File(directory.getAbsolutePath()
						+ File.separator + record.getHost() + "-cert.pem");
				X509Certificate cert = CertUtil.loadCertificate(record
						.getCertificate().getCertificateAsString());
				CertUtil.writeCertificate(cert, certPath);
				str = "The certificate request for the host "
						+ record.getHost()
						+ " has been approved.  The host's certificate has been written to "
						+ certPath.getAbsolutePath()
						+ ".  The host's private key has been written to "
						+ keyPath.getAbsolutePath()
						+ ".   Together the host certificate and private key "
						+ "make up host credentials which can be used to host a secure "
						+ "container in which you may run secure services.   "
						+ "Please make sure you secure access to the host private key, "
						+ "if the private key is compromised please notify an administrator immediately.";
			} else {
				str = "The certificate request for the host "
						+ record.getHost()
						+ " has been submitted for review. You can check the status of this request and view "
						+ "other host certificates you own through the \"My Host Certificates\" window.  "
						+ "  The host's private key has been written to "
						+ keyPath.getAbsolutePath()
						+ ".  Once the request has been approved you will be able to download the host ceritficate from Dorian.  "
						+ "Together the host certificate and private key "
						+ "make up host credentials which can be used to host a secure "
						+ "container in which you may run secure services.   "
						+ "Please make sure you secure access to the host private key, "
						+ "if the private key is compromised please notify an administrator immediately.";
			}
			this.getResponse().setText(str);
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(400, 250);
		this.setContentPane(getJContentPane());
		this.setTitle("Host Certificate Request Report");
		this.setFrameIcon(DorianLookAndFeel.getHostIcon());
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
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridy = 1;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 0;
			gridBagConstraints51.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints51.gridy = 2;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getButtonPanel(), gridBagConstraints51);
			mainPanel.add(getResponsePanel(), gridBagConstraints);
			mainPanel.add(getTitlePanel(), gridBagConstraints11);
		}
		return mainPanel;
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
			buttonPanel.add(getViewCertificate(), null);
			buttonPanel.add(getClose(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes responsePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getResponsePanel() {
		if (responsePanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0;
			responsePanel = new JPanel();
			responsePanel.setLayout(new GridBagLayout());
			responsePanel.add(getJScrollPane(), gridBagConstraints1);
			responsePanel.setBorder(BorderFactory.createTitledBorder(null,
					"Report", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
		}
		return responsePanel;
	}

	/**
	 * This method initializes close
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getClose() {
		if (close == null) {
			close = new JButton();
			close.setText("Close");
			getRootPane().setDefaultButton(close);
			close.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return close;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getResponse());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes response
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getResponse() {
		if (response == null) {
			response = new JTextArea();
			response.setLineWrap(true);
			response.setEditable(false);
			response.setWrapStyleWord(true);
		}
		return response;
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
			if (record.getStatus().equals(HostCertificateStatus.Active)) {
				viewCertificate.setVisible(true);
				viewCertificate
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(
									java.awt.event.ActionEvent e) {
								try {
									X509Certificate cert = CertUtil
											.loadCertificate(record
													.getCertificate()
													.getCertificateAsString());
									GridApplication
											.getContext()
											.addApplicationComponent(
													new CertificateInformationComponent(
															cert), 600, 425);
								} catch (Exception ex) {
									ErrorDialog.showError(ex);
								}

							}
						});
			} else {
				viewCertificate.setVisible(false);
			}
		}
		return viewCertificate;
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			String message = "";
			String sub = "";

			if (this.record.getStatus().equals(HostCertificateStatus.Active)) {
				message = "Host Certificate Issued";
				sub = "The host certificate requested was successfully issued.";
			} else {
				message = "Host Certificate Being Processed";
				sub = "The host certificate requested is being processed.";
			}
			titlePanel = new TitlePanel(message, sub);
		}
		return titlePanel;
	}
}
