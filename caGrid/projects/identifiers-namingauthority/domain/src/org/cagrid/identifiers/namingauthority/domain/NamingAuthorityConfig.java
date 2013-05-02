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
package org.cagrid.identifiers.namingauthority.domain;

import java.net.URI;

public class NamingAuthorityConfig implements java.io.Serializable {
	
	private URI naGridSvcURI;
	private URI naPrefixURI;
	private URI naBaseURI;
	
	public void setNaGridSvcURI( URI gridSvcURI ) {
		this.naGridSvcURI = gridSvcURI;
	}
	
	public URI getNaGridSvcURI() {
		return this.naGridSvcURI;
	}
	
	public void setNaPrefixURI( URI naPrefixURI ) {
		this.naPrefixURI = naPrefixURI;
	}
	
	public URI getNaPrefixURI() {
		return this.naPrefixURI;
	}
	
	public void setNaBaseURI( URI baseURI ) {
		this.naBaseURI = baseURI;
	}
	
	public URI getNaBaseURI() {
		return this.naBaseURI;
	}

}
