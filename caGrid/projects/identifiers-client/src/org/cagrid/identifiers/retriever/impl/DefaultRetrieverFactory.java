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
package org.cagrid.identifiers.retriever.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.retriever.Retriever;
import org.cagrid.identifiers.retriever.RetrieverFactory;

public class DefaultRetrieverFactory implements RetrieverFactory {
	private Map<String, Retriever> retrievers;
	
	public DefaultRetrieverFactory(Map<String, Retriever> retrievers) {
		this.retrievers = retrievers;
	}
	
	public Retriever getRetriever( String name ) {
		return retrievers.get(name);
	}
	
	/* This function makes a best effort attempt to find the correct
	 * retriever object when a name is not specified.
	 * 
	 * If the factory has only one retriever, that retriever is always
	 * returned, regardless of the IdentifierData input.
	 * 
	 * A retriever is considered if all its required keys exist in
	 * the input IdentifierData. If multiple retrievers satisfy this
	 * criteria, the retriever with the highest number of keys is
	 * returned.
	 * 
	 * Retrievers with no required keys are also considered.
	 * 
	 * If multiple retrievers are tied for the highest number of keys,
	 * the first one that was found is the one returned.
	 */
	public Retriever getRetriever( IdentifierData ivs ) {
		if (retrievers == null) {
			return null;
		}
		
		if (retrievers.values().size() == 1) {
			return retrievers.values().iterator().next();
		}
		
		List<String> availableKeys = Arrays.asList(ivs.getKeys());
		Retriever retriever = null;
		
		for(Retriever r : retrievers.values()) {
			
			if (r == null)
				continue;
			
			List<String> requiredKeys = 
				Arrays.asList(r.getRequiredKeys());
			
			if (availableKeys.containsAll(requiredKeys)) {
				if (retriever != null) {
					if (requiredKeys.size() > retriever.getRequiredKeys().length) {
						retriever = r;
					}
				} else {
					retriever = r;
				}
			}
		}
		
		return retriever;
	}
}
