package gov.nih.nci.cagrid.introduce.portal.modification.security;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.security.X509Credential;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.utils.CompositeErrorDialog;
import org.globus.gsi.CertUtil;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */

public class LoadCredentialsFromFileSystemWindow extends ApplicationComponent {

	private JPanel jContentPane = null;
	private JPanel mainPanel = null;
	private JLabel jLabel = null;
	private JTextField certificate = null;
	private JButton browseCertButton = null;
	private JLabel jLabel1 = null;
	private JTextField privateKey = null;
	private JButton privateKeyButton = null;
	private JPanel buttonPanel = null;
	private JButton setCredentialsButton = null;
	private JButton cancelButton = null;
	private ServiceSecurityPanel serviceSecurity;


	/**
	 * This is the default constructor
	 */
	public LoadCredentialsFromFileSystemWindow(ServiceSecurityPanel ssp) {
		super();
		this.serviceSecurity = ssp;
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Load Credentials");
		this.setFrameIcon(IntroduceLookAndFeel.getLoadCredentialsIcon());
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new java.awt.Insets(5, 5, 5, 5);
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getMainPanel(), gridBagConstraints);
			jContentPane.add(getButtonPanel(), gridBagConstraints11);
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
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.gridy = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints3.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Private Key");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints1.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Certificate");
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(jLabel, gridBagConstraints1);
			mainPanel.add(getCertificate(), gridBagConstraints2);
			mainPanel.add(getBrowseCertButton(), new GridBagConstraints());
			mainPanel.add(jLabel1, gridBagConstraints3);
			mainPanel.add(getPrivateKey(), gridBagConstraints4);
			mainPanel.add(getPrivateKeyButton(), gridBagConstraints5);
		}
		return mainPanel;
	}


	/**
	 * This method initializes certificate
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getCertificate() {
		if (certificate == null) {
			certificate = new JTextField();
			certificate.setEditable(false);
		}
		return certificate;
	}


	/**
	 * This method initializes browseCertButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowseCertButton() {
		if (browseCertButton == null) {
			browseCertButton = new JButton();
			browseCertButton.setText("Browse");
			browseCertButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseCertificate();
				}
			});
		}
		return browseCertButton;
	}


	private void browseCertificate() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			this.certificate.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}


	private void browsePrivateKey() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			this.privateKey.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}


	/**
	 * This method initializes privateKey
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPrivateKey() {
		if (privateKey == null) {
			privateKey = new JTextField();
			privateKey.setEditable(false);
		}
		return privateKey;
	}


	/**
	 * This method initializes privateKeyButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getPrivateKeyButton() {
		if (privateKeyButton == null) {
			privateKeyButton = new JButton();
			privateKeyButton.setText("Browse");
			privateKeyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browsePrivateKey();
				}
			});
		}
		return privateKeyButton;
	}


	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getSetCredentialsButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes setCredentialsButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSetCredentialsButton() {
		if (setCredentialsButton == null) {
			setCredentialsButton = new JButton();
			setCredentialsButton.setText("Set Credentials");
			setCredentialsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setCredentials();
				}
			});
			setCredentialsButton.setIcon(IntroduceLookAndFeel.getLoadCredentialsIcon());
		}
		return setCredentialsButton;
	}


	private void setCredentials() {
		String certStr = this.certificate.getText().trim();
		if (certStr.length() == 0) {
			CompositeErrorDialog.showErrorDialog("You must specify a certificate!!!");
			return;
		}
		
		try {
			CertUtil.loadCertificate(certStr);
		} catch (Exception e) {
		    CompositeErrorDialog.showErrorDialog("Invalid certificate specified!!!");
			return;
		}

		String keyStr = this.privateKey.getText().trim();
		if (keyStr.length() == 0) {
		    CompositeErrorDialog.showErrorDialog("You must specify a private key!!!");
			return;
		}

		
		try {
			 KeyUtil.loadPrivateKey(new File(keyStr), null);
		} catch (Exception e) {
		    CompositeErrorDialog.showErrorDialog("Invalid private key specified: " + e.getMessage());
			return;
		}
		X509Credential cred = new X509Credential();
		cred.setCertificateLocation(certStr.replace('\\','/'));
		cred.setPrivateKeyLocation(keyStr.replace('\\','/'));
		try {
			this.serviceSecurity.setCredentials(cred);
		} catch (Exception e) {
			// PortalUtils.showErrorDialogDialog(e);
		    CompositeErrorDialog.showErrorDialog(e);
			return;
		}
		dispose();
	}


	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.setIcon(PortalLookAndFeel.getCloseIcon());
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

}
