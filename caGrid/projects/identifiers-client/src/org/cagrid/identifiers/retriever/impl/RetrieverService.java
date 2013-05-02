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

import org.cagrid.identifiers.client.Util;
import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.retriever.Retriever;
import org.cagrid.identifiers.retriever.RetrieverFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class RetrieverService {
    private ApplicationContext appCtx;
    private RetrieverFactory factory;


    public RetrieverService() {
        init(new String[]{ Util.DEFAULT_SPRING_CONTEXT_RESOURCE}, "RetrieverFactory");
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


    public Object retrieve(String retrieverName, IdentifierData ivs) throws Exception {
        Retriever retriever = factory.getRetriever(retrieverName);
        return retriever.retrieve(ivs);
    }


    public Object retrieve(IdentifierData ivs) throws Exception {
        Retriever retriever = factory.getRetriever(ivs);
        return retriever.retrieve(ivs);
    }
}
