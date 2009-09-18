package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;

public interface RetrieverFactory {
	Retriever getRetriever( IdentifierValuesImpl ivs ) throws Exception;
	Retriever getRetriever( String name ) throws Exception;
}
