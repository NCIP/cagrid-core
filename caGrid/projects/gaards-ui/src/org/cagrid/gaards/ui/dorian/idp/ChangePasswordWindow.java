package org.cagrid.gaards.ui.dorian.idp;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.dorian.client.LocalUserClient;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianHandle;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianServiceListComboBox;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 */
public class ChangePasswordWindow extends ApplicationComponent {
	private static Log log = LogFactory.getLog(ChangePasswordWindow.class);
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel authPanel = null;

	private JPanel buttonPanel = null;

	private JLabel serviceLabel = null;

	private JComboBox service = null;

	private JLabel usernameLabel = null;

	private JTextField username = null;

	private JLabel passwordLabel = null;

	private JPasswordField password = null;

	private JLabel verifyLabel = null;

	private JPasswordField verify = null;

	private JButton resetPassword = null;

	private JLabel jLabel = null;

	private JPasswordField newPassword = null;

    private JPanel titlePanel = null;

    private ProgressPanel progressPanel = null;


	/**
	 * This is the default constructor
	 */
	public ChangePasswordWindow() {
		super();
		initialize();
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Change Password");
		this.setFrameIcon(DorianLookAndFeel.getPasswordIcon());
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
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.weightx = 1.0D;
			gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints41.gridy = 3;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints31.weightx = 1.0D;
			gridBagConstraints31.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridx = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getAuthPanel(), gridBagConstraints);
			mainPanel.add(getButtonPanel(), gridBagConstraints2);
			mainPanel.add(getTitlePanel(), gridBagConstraints31);
			mainPanel.add(getProgressPanel(), gridBagConstraints41);
		}
		return mainPanel;
	}


	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAuthPanel() {
		if (authPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridy = 3;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.gridy = 3;
			jLabel = new JLabel();
			jLabel.setText("New Password");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 4;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints9.gridy = 4;
			verifyLabel = new JLabel();
			verifyLabel.setText("Verify Password");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints7.gridx = 0;
			passwordLabel = new JLabel();
			passwordLabel.setText("Current Password");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 1;
			usernameLabel = new JLabel();
			usernameLabel.setText("Username");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.weightx = 1.0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints3.gridy = 0;
			serviceLabel = new JLabel();
			serviceLabel.setText("Service");
			authPanel = new JPanel();
			authPanel.setLayout(new GridBagLayout());
			authPanel.add(serviceLabel, gridBagConstraints3);
			authPanel.add(getService(), gridBagConstraints4);
			authPanel.add(usernameLabel, gridBagConstraints5);
			authPanel.add(getUsername(), gridBagConstraints6);
			authPanel.add(passwordLabel, gridBagConstraints7);
			authPanel.add(getPassword(), gridBagConstraints8);
			authPanel.add(verifyLabel, gridBagConstraints9);
			authPanel.add(getVerify(), gridBagConstraints10);
			authPanel.add(jLabel, gridBagConstraints1);
			authPanel.add(getNewPassword(), gridBagConstraints11);
		}
		return authPanel;
	}


	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getResetPassword(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes service
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getService() {
		if (service == null) {
			service = new DorianServiceListComboBox();
		}
		return service;
	}


	/**
	 * This method initializes username
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getUsername() {
		if (username == null) {
			username = new JTextField();
		}
		return username;
	}


	/**
	 * This method initializes password
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getPassword() {
		if (password == null) {
			password = new JPasswordField();
		}
		return password;
	}


	/**
	 * This method initializes verify
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getVerify() {
		if (verify == null) {
			verify = new JPasswordField();
		}
		return verify;
	}


	/**
	 * This method initializes resetPassword
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getResetPassword() {
		if (resetPassword == null) {
			resetPassword = new JButton();
			resetPassword.setText("Change Password");
			resetPassword.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					resetPassword();

				}
			});
		}
		return resetPassword;
	}


	public void resetPassword() {
		getResetPassword().setEnabled(false);
		final String pass = new String(this.getNewPassword().getPassword());
		String vpass = new String(this.getVerify().getPassword());
		if (!pass.equals(vpass)) {
			ErrorDialog.showError("Password don't match!!!");
			getResetPassword().setEnabled(true);
			return;
		}

		final DorianHandle handle = ((DorianServiceListComboBox) this.getService()).getSelectedService();

		Runner runner = new Runner() {
			public void execute() {
				try {
					LocalUserClient client = handle.getLocalUserClient();
					BasicAuthentication cred = new BasicAuthentication();
					cred.setUserId(getUsername().getText());
					cred.setPassword(new String(getPassword().getPassword()));
					client.changePassword(cred, pass);
					GridApplication.getContext().showMessage("Password successfully changed!!!");
					dispose();
				} catch (Exception e) {
					FaultUtil.logFault(log, e);
					ErrorDialog.showError(e);
					getResetPassword().setEnabled(true);
				}
			}
		};
		try {
			GridApplication.getContext().executeInBackground(runner);
		} catch (Exception t) {
			t.getMessage();
		}
	}


	/**
	 * This method initializes newPassword
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getNewPassword() {
		if (newPassword == null) {
			newPassword = new JPasswordField();
		}
		return newPassword;
	}


    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Change Password","Change your account password with the Dorian identity provider");
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
}
