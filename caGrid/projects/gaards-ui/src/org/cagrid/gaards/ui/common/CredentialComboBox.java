package org.cagrid.gaards.ui.common;

import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.util.List;

import javax.swing.JComboBox;

import org.globus.gsi.GlobusCredential;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CredentialComboBox extends JComboBox {
	
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_PROXY = "Globus Default Proxy";

	private static final String NO_PROXY = "None";

	public CredentialComboBox() {
		this(false);
	}

	public CredentialComboBox(boolean none) {
		List creds = CredentialManager.getInstance().getCredentials();
		if (none) {
			addItem(new CredentialCaddy(NO_PROXY, null));
		}
		addItem(new CredentialCaddy(DEFAULT_PROXY, null));
		for (int i = 0; i < creds.size(); i++) {
			addItem(new CredentialCaddy((GlobusCredential) creds.get(i)));
		}
	}

	public CredentialComboBox(GlobusCredential cred) {
		this(false);
		this.setSelectedItem(new CredentialCaddy(cred));
	}

	public CredentialComboBox(GlobusCredential cred, boolean none) {
		this(none);
		this.setSelectedItem(new CredentialCaddy(cred));
	}

	public CredentialCaddy getSelectedCredentialCaddy() {
		CredentialCaddy caddy = ((CredentialCaddy) this.getSelectedItem());
		return caddy;
	}

	public GlobusCredential getSelectedCredential() throws Exception {
		CredentialCaddy caddy = ((CredentialCaddy) this.getSelectedItem());
		if (caddy.getIdentity().equals(DEFAULT_PROXY)) {
			try {
				caddy.setProxy(ProxyUtil.getDefaultProxy());
			} catch (Exception e) {
				throw new Exception("No default proxy found!!!");
			}
			if (caddy.getProxy().getTimeLeft() == 0) {
				throw new Exception("The default proxy has expired!!!");
			}
		}
		return caddy.getProxy();
	}

}
