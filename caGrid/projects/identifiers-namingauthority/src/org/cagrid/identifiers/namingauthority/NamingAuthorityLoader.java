package org.cagrid.identifiers.namingauthority;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


public class NamingAuthorityLoader {
    private NamingAuthority namingAuthority;
    private static final String NA_BEAN_NAME = "NamingAuthority";
    private static Log LOG = LogFactory.getLog(NamingAuthorityLoader.class);


    public NamingAuthorityLoader() throws NamingAuthorityConfigurationException {
        init(new ClassPathResource("/applicationContext-na.xml"), new ClassPathResource("/na.properties"));
    }


    public NamingAuthorityLoader(Resource configuration, Resource properties)
        throws NamingAuthorityConfigurationException {
        init(configuration, properties);
    }


    public NamingAuthorityLoader(File configuration, File properties) throws NamingAuthorityConfigurationException {
        init(new FileSystemResource(configuration), new FileSystemResource(properties));
    }


    private void init(Resource configuration, Resource properties) throws NamingAuthorityConfigurationException {
        try {
            XmlBeanFactory factory = new XmlBeanFactory(configuration);
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            cfg.setLocation(properties);
            cfg.postProcessBeanFactory(factory);

            LOG.info("Initializing NamingAuthority from: " + configuration.getDescription()
                + " using properties from: " + properties.getDescription());
            this.namingAuthority = (NamingAuthority) factory.getBean(NA_BEAN_NAME, NamingAuthority.class);
        } catch (Exception e) {
            String message = "Problem inititializing the Naming Authority while loading configuration:"
                + e.getMessage();
            LOG.error(message, e);
            throw new NamingAuthorityConfigurationException(message, e);
        }
    }


    public NamingAuthority getNamingAuthority() {
        return namingAuthority;
    }
}
