package gov.nih.nci.cagrid.authorization.pdp.impl;

import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.config.ContainerConfig;
import org.globus.wsrf.impl.security.authorization.exceptions.InitializeException;
import org.globus.wsrf.security.authorization.PDPConfig;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

public abstract class AuthzUtils {
	
	private static Log logger = LogFactory.getLog(AuthzUtils.class.getName());
	
	public static final String BEANS_FILE = "beansFile";
	
	public static Set getGroups(Subject subject) {
		Set groups = new HashSet();
		for (Iterator i = subject.getPublicCredentials().iterator(); i
				.hasNext();) {
			Object o = i.next();
			if (o instanceof GroupI) {
				groups.add(o);
			}
		}
		return groups;
	}
	
	public static Object getBean(PDPConfig config, String scope, String id) throws InitializeException {
		logger.debug("Looking for property '" + scope + "-" + BEANS_FILE + "'.");
		String beansFile = (String) config.getProperty(scope, BEANS_FILE);
		if(beansFile == null){
			throw new InitializeException("No " + BEANS_FILE + " property specified");
		}
		return getBean(beansFile, scope);
	}
	public static Object getBean(String beansFile, String beanName) throws InitializeException {
		String fullPath = ContainerConfig.getBaseDirectory() + File.separator + beansFile;
		FileSystemResource fsr = new FileSystemResource(fullPath);
		if(!fsr.exists()){
			throw new InitializeException("Couldn't find " + BEANS_FILE + " at " + fullPath);
		}
		XmlBeanFactory fact = new XmlBeanFactory(fsr);
		return fact.getBean(beanName);	
	}

}
