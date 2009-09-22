package org.cagrid.gaards.csm.service;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.AbstractResource;

public class BeanUtils {

	private XmlBeanFactory factory;

	public BeanUtils(AbstractResource csmConf, AbstractResource csmProperties) {
		this.factory = new XmlBeanFactory(csmConf);
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		cfg.setLocation(csmProperties);
		cfg.postProcessBeanFactory(factory);
	}

	public CSMProperties getCSMProperties() throws Exception {
		CSMProperties props = (CSMProperties) factory
				.getBean(Constants.CSM_PROPERTIES);
		return props;
	}
}
