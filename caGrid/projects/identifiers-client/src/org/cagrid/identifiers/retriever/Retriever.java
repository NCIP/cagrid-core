package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.domain.IdentifierData;

public interface Retriever {
	public Object retrieve(IdentifierData ivs) throws Exception;
}
