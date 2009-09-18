package org.cagrid.gaards.ui.gridgrouper.browser;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GroupPrivilegeCaddy {
	private String identity;

	private boolean admin = false;

	private boolean optin = false;

	private boolean optout = false;

	private boolean read = false;

	private boolean update = false;

	private boolean view = false;


	public GroupPrivilegeCaddy(String identity) {
		this.identity = identity;
	}


	public String getIdentity() {
		return identity;
	}


	public boolean hasAdmin() {
		return admin;
	}


	public void setAdmin(boolean admin) {
		this.admin = admin;
	}


	public boolean hasOptin() {
		return optin;
	}


	public void setOptin(boolean optin) {
		this.optin = optin;
	}


	public boolean hasOptout() {
		return optout;
	}


	public void setOptout(boolean optout) {
		this.optout = optout;
	}


	public boolean hasRead() {
		return read;
	}


	public void setRead(boolean read) {
		this.read = read;
	}


	public boolean hasUpdate() {
		return update;
	}


	public void setUpdate(boolean update) {
		this.update = update;
	}


	public boolean hasView() {
		return view;
	}


	public void setView(boolean view) {
		this.view = view;
	}


	public void setIdentity(String identity) {
		this.identity = identity;
	}

}
