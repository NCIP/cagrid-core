package org.cagrid.data.sdkquery44.test;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.sdkquery44.translator.CQL2ParameterizedHQL;
import org.cagrid.data.sdkquery44.translator.ConstantValueResolver;
import org.cagrid.data.sdkquery44.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.data.sdkquery44.translator.IsoDatatypesConstantValueResolver;
import org.cagrid.data.sdkquery44.translator.TypesInformationResolver;
import org.hibernate.cfg.Configuration;

public class QueryTestsHelper {
    
    private static Log LOG = LogFactory.getLog(QueryTestsHelper.class);
    
    private static ApplicationService sdkService = null;
    private static TypesInformationResolver typesInfoResolver = null;
    private static ConstantValueResolver constantResolver = null;
    private static CQL2ParameterizedHQL queryTranslator = null;
    
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

}
