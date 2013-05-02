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
package org.cagrid.tools.events;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Utils {

	public static XmlBeanFactory loadConfiguration() throws Exception {

		ClassPathResource cpr = new ClassPathResource("event-manager.xml");
		XmlBeanFactory factory = new XmlBeanFactory(cpr);
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		cfg.setLocation(new ClassPathResource("events.properties"));
		cfg.postProcessBeanFactory(factory);
		return factory;
	}

	public static EventAuditor getEventAuditor() throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (EventAuditor) factory.getBean("eventAuditor");
	}
	
	public static EventManager getEventManager() throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (EventManager) factory.getBean("eventManager");
	}
}
