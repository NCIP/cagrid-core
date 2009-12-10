package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;

public interface Retriever {
	public Object retrieve(IdentifierValues ivs) throws Exception;
}
