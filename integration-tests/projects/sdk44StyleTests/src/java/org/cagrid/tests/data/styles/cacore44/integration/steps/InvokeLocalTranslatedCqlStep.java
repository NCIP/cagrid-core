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
package org.cagrid.tests.data.styles.cacore44.integration.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.cagrid.data.sdkquery44.translator.CQL2ParameterizedHQL;
import org.cagrid.data.sdkquery44.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.data.sdkquery44.translator.IsoDatatypesConstantValueResolver;
import org.cagrid.data.sdkquery44.translator.ParameterizedHqlQuery;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class InvokeLocalTranslatedCqlStep extends AbstractLocalCqlInvocationStep {
    
    private CQL2ParameterizedHQL translator = null;
    
    public InvokeLocalTranslatedCqlStep() {
        super();
    }
    
    
    private CQL2ParameterizedHQL getTranslator() {
        if (translator == null) {
            try {
                InputStream hbmConfigStream = getClass().getResourceAsStream("/hibernate.cfg.xml");
                assertNotNull("Hibernate config was null", hbmConfigStream);
                Configuration hibernateConfig = new Configuration();
                hibernateConfig.addInputStream(hbmConfigStream);
                hibernateConfig.buildMapping();
                hibernateConfig.configure();
                hbmConfigStream.close();

                String base = System.getProperty(TESTS_BASEDIR_PROPERTY);
                File sdkLocalClientDir = new File(base, SDK_LOCAL_CLIENT_DIR);
                File sdkConfDir = new File(sdkLocalClientDir, "conf");
                File constantsFile = new File(sdkConfDir, "IsoConstants.xml");
                // Spring loads the config file from a location relative to the JVM working dir,
                // which causes massive headaches under non-windows platforms if you try to specify
                // the "absolutePath" to the constants file.  Therefore, we're using the relative path
                // full of ../'s and stuff
                String relPath = Utils.getRelativePath(new File("."), constantsFile);
                ApplicationContext isoContext = new FileSystemXmlApplicationContext(relPath);
                translator = new CQL2ParameterizedHQL(
                    new HibernateConfigTypesInformationResolver(hibernateConfig, true), 
                    new IsoDatatypesConstantValueResolver(isoContext),
                    false);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error: " + ex.getMessage());
            }
        }
        
        return translator;
    }


    protected List<?> executeQuery(CQLQuery query) throws Exception {
        ParameterizedHqlQuery translated = getTranslator().convertToHql(query);
        return getService().query(new HQLCriteria(translated.getHql(), translated.getParameters()));
    }
}
