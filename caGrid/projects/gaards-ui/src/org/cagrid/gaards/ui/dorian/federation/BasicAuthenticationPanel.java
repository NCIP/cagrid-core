package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.Credential;

public class BasicAuthenticationPanel extends CredentialPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private JTextField userId = null;
	private JLabel jLabel1 = null;
	private JPasswordField password = null;

	/**
	 * This is the default constructor
	 */
	public BasicAuthenticationPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		 GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		 gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		 gridBagConstraints3.gridy = 1;
		 gridBagConstraints3.weightx = 1.0;
		 gridBagConstraints3.anchor = GridBagConstraints.WEST;
		 gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
		 gridBagConstraints3.gridx = 1;
		 GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		 gridBagConstraints2.gridx = 0;
		 gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		 gridBagConstraints2.anchor = GridBagConstraints.WEST;
		 gridBagConstraints2.gridy = 1;
		 jLabel1 = new JLabel();
		 jLabel1.setText("Password");
		 GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		 gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		 gridBagConstraints1.gridx = 1;
		 gridBagConstraints1.gridy = 0;
		 gridBagConstraints1.anchor = GridBagConstraints.WEST;
		 gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		 gridBagConstraints1.weightx = 1.0;
		 GridBagConstraints gridBagConstraints = new GridBagConstraints();
		 gridBagConstraints.anchor = GridBagConstraints.WEST;
		 gridBagConstraints.gridx = 0;
		 gridBagConstraints.gridy = 0;
		 gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		 jLabel = new JLabel();
		 jLabel.setText("User Id");
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel, gridBagConstraints);
		this.add(getUserId(), gridBagConstraints1);
		this.add(jLabel1, gridBagConstraints2);
		this.add(getPassword(), gridBagConstraints3);
       
	}
	
	public Credential getCredential() throws Exception{
		BasicAuthentication credential =  new BasicAuthentication();
		credential.setUserId(Utils.clean(getUserId().getText()));
		credential.setPassword(Utils.clean(new String(getPassword().getPassword())));
		return credential;
	}

	/**
	 * This method initializes userId	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getUserId() {
		if (userId == null) {
			userId = new JTextField();
		}
		return userId;
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

}
