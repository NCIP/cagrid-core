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
package org.cagrid.gaards.cds.service;

import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationSigningRequest;
import org.cagrid.gaards.cds.common.DelegationSigningResponse;

public class DelegatedCredential {
	private DelegationSigningRequest signingRequest;
	private DelegationSigningResponse signingResponse;

	public DelegatedCredential(DelegationSigningRequest req,
			DelegationSigningResponse res) {
		this.signingRequest = req;
		this.signingResponse = res;
	}

	public DelegationSigningRequest getSigningRequest() {
		return signingRequest;
	}

	public DelegationSigningResponse getSigningResponse() {
		return signingResponse;
	}
	
	public DelegationIdentifier getDelegationIdentifier(){
		return signingRequest.getDelegationIdentifier();
	}

}
