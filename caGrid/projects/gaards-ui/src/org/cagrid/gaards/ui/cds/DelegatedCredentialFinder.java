package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.globus.gsi.GlobusCredential;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class DelegatedCredentialFinder extends Runner {

	private CDSHandle cds;
	private GlobusCredential proxy;
	private DelegationDescriptorTable table;
	private boolean isSuccessful = false;
	private String error = null;

	public DelegatedCredentialFinder(CDSHandle handle, GlobusCredential proxy,
			DelegationDescriptorTable table) {
		this.cds = handle;
		this.proxy = proxy;
		this.table = table;

	}

	public void execute() {
		try {
			DelegationUserClient client = cds.getUserClient(proxy);
			table.addDelegationDescriptors(client
					.findCredentialsDelegatedToClient());
			isSuccessful = true;
		} catch (Exception e) {
			error = Utils.getExceptionMessage(e);
		}
	}

	public String getDelegationURI() {
		return cds.getServiceURL();
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public String getError() {
		return error;
	}

}
