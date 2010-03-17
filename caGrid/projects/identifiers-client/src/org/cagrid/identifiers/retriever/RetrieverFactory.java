package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.domain.IdentifierData;


public interface RetrieverFactory {
    Retriever getRetriever(IdentifierData id);
    Retriever getRetriever(String name);
}
