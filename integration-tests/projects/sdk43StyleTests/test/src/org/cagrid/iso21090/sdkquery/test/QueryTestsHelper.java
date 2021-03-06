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
package org.cagrid.iso21090.sdkquery.test;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.sdkquery.translator.CQL2ParameterizedHQL;
import org.cagrid.iso21090.sdkquery.translator.ConstantValueResolver;
import org.cagrid.iso21090.sdkquery.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.iso21090.sdkquery.translator.IsoDatatypesConstantValueResolver;
import org.cagrid.iso21090.sdkquery.translator.TypesInformationResolver;
import org.cagrid.iso21090.sdkquery.translator.cql2.CQL2ToParameterizedHQL;
import org.cagrid.iso21090.sdkquery.translator.cql2.Cql2TypesInformationResolver;
import org.cagrid.iso21090.sdkquery.translator.cql2.HibernateConfigCql2TypesInformationResolver;
import org.hibernate.cfg.Configuration;

public class QueryTestsHelper {
    
    private static Log LOG = LogFactory.getLog(QueryTestsHelper.class);
    
    private static ApplicationService sdkService = null;
    private static TypesInformationResolver typesInfoResolver = null;
    private static Cql2TypesInformationResolver cql2TypesInfoResolver = null;
    private static ConstantValueResolver constantResolver = null;
    private static CQL2ParameterizedHQL queryTranslator = null;
    private static CQL2ToParameterizedHQL cql2QueryTranslator = null;
    
    private QueryTestsHelper() {
        // this is not the constructor you were looking for
    }
    
    
    public static synchronized ApplicationService getSdkApplicationService() throws Exception {
        if (sdkService == null) {
            long start = System.currentTimeMillis();
            // gets the local SDK service instance
            sdkService = ApplicationServiceProvider.getApplicationService();
            LOG.info("Application service initialized in " + (System.currentTimeMillis() - start));
            System.out.println("Application service initialized in " + (System.currentTimeMillis() - start));
        }
        return sdkService;
    }
    
    
    public static synchronized TypesInformationResolver getTypesInformationResolver() throws IOException {
        if (typesInfoResolver == null) {
            long start = System.currentTimeMillis();
            InputStream is = QueryTestsHelper.class.getResourceAsStream("/hibernate.cfg.xml");
            Configuration config = new Configuration();
            config.addInputStream(is);
            config.buildMappings();
            config.configure();
            typesInfoResolver = new HibernateConfigTypesInformationResolver(config, true);
            is.close();
            LOG.info("Types information resolver initialized in " + (System.currentTimeMillis() - start));
            System.out.println("Types information resolver initialized in " + (System.currentTimeMillis() - start));
        }
        return typesInfoResolver;
    }
    
    
    public static synchronized Cql2TypesInformationResolver getCql2TypesInformationResolver() throws IOException {
        if (cql2TypesInfoResolver == null) {
            long start = System.currentTimeMillis();
            InputStream is = QueryTestsHelper.class.getResourceAsStream("/hibernate.cfg.xml");
            Configuration config = new Configuration();
            config.addInputStream(is);
            config.buildMappings();
            config.configure();
            cql2TypesInfoResolver = new HibernateConfigCql2TypesInformationResolver(config, true);
            is.close();
            LOG.info("Types information resolver initialized in " + (System.currentTimeMillis() - start));
            System.out.println("Types information resolver initialized in " + (System.currentTimeMillis() - start));
        }
        return cql2TypesInfoResolver;
    }
    
    
    public static synchronized ConstantValueResolver getConstantValueResolver() {
        if (constantResolver == null) {
            long start = System.currentTimeMillis();
            // FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("sdk/local-client/conf/IsoConstants.xml");
            constantResolver = new IsoDatatypesConstantValueResolver();
            LOG.info("Constant value resolver initialized in " + (System.currentTimeMillis() - start));
            System.out.println("Constant value resolver initialized in " + (System.currentTimeMillis() - start));
        }
        return constantResolver;
    }
    
    
    public static synchronized CQL2ParameterizedHQL getCqlTranslator() throws IOException {
        if (queryTranslator == null) {
            queryTranslator = new CQL2ParameterizedHQL(getTypesInformationResolver(), getConstantValueResolver(), false);
        }
        return queryTranslator;
    }
    
    
    public static synchronized CQL2ToParameterizedHQL getCql2Translator() throws IOException {
        if (cql2QueryTranslator == null) {
            cql2QueryTranslator = new CQL2ToParameterizedHQL(
                getCql2TypesInformationResolver(), getConstantValueResolver(), false);
        }
        return cql2QueryTranslator;
    }

}
