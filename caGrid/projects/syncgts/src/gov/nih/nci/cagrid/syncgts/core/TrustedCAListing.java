package gov.nih.nci.cagrid.syncgts.core;

import gov.nih.nci.cagrid.gts.bean.TrustedAuthority;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescriptor;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TrustedCAListing {

	private String service;
	private TrustedAuthority trustedAuthority;
	private SyncDescriptor descriptor;


	public TrustedCAListing(String service, TrustedAuthority ta, SyncDescriptor des) {
		this.service = service;
		this.trustedAuthority = ta;
		this.descriptor = des;
	}


	public String getService() {
		return service;
	}


	public TrustedAuthority getTrustedAuthority() {
		return trustedAuthority;
	}


	public SyncDescriptor getDescriptor() {
		return descriptor;
	}

}
