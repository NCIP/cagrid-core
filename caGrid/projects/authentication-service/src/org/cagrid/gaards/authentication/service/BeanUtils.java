package org.cagrid.gaards.authentication.service;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.AbstractResource;

public class BeanUtils {

	private XmlBeanFactory factory;

	public BeanUtils(AbstractResource conf,
			AbstractResource properties) throws Exception {
		this.factory = new XmlBeanFactory(conf);
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		cfg.setLocation(properties);
		cfg.postProcessBeanFactory(factory);
	}

	public AuthenticationProvider getAuthenticationProvider() throws Exception {
		AuthenticationProvider props = (AuthenticationProvider) factory
				.getBean("authenticationProvider");
		return props;
	}

}
