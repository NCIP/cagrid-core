package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.domain.IdentifierData;


public interface RetrieverFactory {
    Retriever getRetriever(IdentifierData ivs) throws Exception;


    Retriever getRetriever(String name) throws Exception;
}
