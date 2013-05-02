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
