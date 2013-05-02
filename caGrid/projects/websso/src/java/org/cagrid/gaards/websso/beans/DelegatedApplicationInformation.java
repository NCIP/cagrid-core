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
package org.cagrid.gaards.websso.beans;

public class DelegatedApplicationInformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String hostName = null;

	private String hostIdentity = null;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostIdentity() {
		return hostIdentity;
	}

	public void setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
	}

}
