/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
