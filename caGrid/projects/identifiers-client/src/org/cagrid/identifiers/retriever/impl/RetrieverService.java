package org.cagrid.identifiers.retriever.impl;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.retriever.Retriever;
import org.cagrid.identifiers.retriever.RetrieverFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class RetrieverService {
    private ApplicationContext appCtx;
    private RetrieverFactory factory;


    public RetrieverService() {
        init(new String[]{"/resources/spring/identifiers-client-context.xml"}, "RetrieverFactory");
    }


    public RetrieverService(String[] contextList, String factoryName) {
        init(contextList, factoryName);
    }


    private void init(String[] contextList, String factoryName) {
        appCtx = new ClassPathXmlApplicationContext(contextList);
        factory = (RetrieverFactory) appCtx.getBean(factoryName);
    }


    public RetrieverFactory getFactory() {
        return factory;
    }


    public Object retrieve(String retrieverName, IdentifierValues ivs) throws Exception {
        Retriever retriever = factory.getRetriever(retrieverName);
        return retriever.retrieve(ivs);
    }


    public Object retrieve(IdentifierValues ivs) throws Exception {
        Retriever retriever = factory.getRetriever(ivs);
        return retriever.retrieve(ivs);
    }
}
