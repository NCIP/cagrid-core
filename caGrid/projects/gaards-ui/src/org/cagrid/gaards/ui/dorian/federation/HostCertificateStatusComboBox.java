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
package org.cagrid.gaards.ui.dorian.federation;

import javax.swing.JComboBox;

import org.cagrid.gaards.dorian.federation.HostCertificateStatus;

public class HostCertificateStatusComboBox extends JComboBox {
	
	private static final long serialVersionUID = 1L;
	
	public HostCertificateStatusComboBox(boolean includeBlank) {
		if (includeBlank) {
			this.addItem("");
		}
		this.addItem(HostCertificateStatus.Active);
		this.addItem(HostCertificateStatus.Pending);
		this.addItem(HostCertificateStatus.Rejected);
		this.addItem(HostCertificateStatus.Suspended);
		this.addItem(HostCertificateStatus.Compromised);
	}

	public HostCertificateStatus getStatus() {
		if (getSelectedItem() instanceof HostCertificateStatus) {
			return (HostCertificateStatus) getSelectedItem();
		} else {
			return null;
		}
	}

}
