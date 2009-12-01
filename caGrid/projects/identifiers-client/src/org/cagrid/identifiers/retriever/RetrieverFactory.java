package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;


public interface RetrieverFactory {
    Retriever getRetriever(IdentifierValues ivs) throws Exception;


    Retriever getRetriever(String name) throws Exception;
}
