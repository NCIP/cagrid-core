package gov.nih.nci.cagrid.introduce.portal.modification.security;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.introduce.beans.security.ProxyCredential;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.utils.CompositeErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */

public class LoadProxyFromFileSystemWindow extends ApplicationComponent {

	private JPanel jContentPane = null;
	private JPanel mainPanel = null;
	private JLabel jLabel = null;
	private JTextField proxy = null;
	private JButton browseCertButton = null;
	private JPanel buttonPanel = null;
	private JButton setCredentialsButton = null;
	private JButton cancelButton = null;
	private ServiceSecurityPanel serviceSecurity;


	/**
	 * This is the default constructor
	 */
	public LoadProxyFromFileSystemWindow(ServiceSecurityPanel ssp) {
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
		this.setTitle("Load Proxy");
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
			jLabel.setText("Proxy");
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(jLabel, gridBagConstraints1);
			mainPanel.add(getProxy(), gridBagConstraints2);
			mainPanel.add(getBrowseCertButton(), new GridBagConstraints());
		}
		return mainPanel;
	}


	/**
	 * This method initializes certificate
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getProxy() {
		if (proxy == null) {
			proxy = new JTextField();
			proxy.setEditable(false);
		}
		return proxy;
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
					browseProxy();
				}
			});
		}
		return browseCertButton;
	}


	private void browseProxy() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			this.proxy.setText(fc.getSelectedFile().getAbsolutePath());
		}
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
			setCredentialsButton.setText("Set Proxy");
			setCredentialsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setProxy();
				}
			});
			setCredentialsButton.setIcon(IntroduceLookAndFeel.getLoadCredentialsIcon());
		}
		return setCredentialsButton;
	}


	private void setProxy() {
		String proxyStr = this.proxy.getText().trim();
		if (proxyStr.length() == 0) {
		    CompositeErrorDialog.showErrorDialog("You must specify a proxy!!!");
			return;
		}

		try {
			ProxyUtil.loadProxy(proxyStr);
		} catch (Exception e) {
		    CompositeErrorDialog.showErrorDialog("Invalid proxy specified!!!");
			return;
		}

		try {
			proxyStr = proxyStr.replace('\\', '/');
			ProxyCredential cred = new ProxyCredential(proxyStr);
			this.serviceSecurity.setProxy(cred);
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
