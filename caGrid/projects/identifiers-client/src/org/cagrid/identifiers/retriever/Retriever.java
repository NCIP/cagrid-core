package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.domain.IdentifierData;

public interface Retriever {
	public String[] getRequiredKeys();
	public Object retrieve(IdentifierData ivs) throws Exception;
}
