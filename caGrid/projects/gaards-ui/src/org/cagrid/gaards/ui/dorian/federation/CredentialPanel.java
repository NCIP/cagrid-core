package org.cagrid.gaards.ui.dorian.federation;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.cagrid.gaards.authentication.Credential;

public abstract class CredentialPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	public CredentialPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
       
	}
	
	public abstract Credential getCredential() throws Exception;

}
