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
package gov.nih.nci.cagrid.data.service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;

/** 
 *  ResourcePropertiesUtil
 *  Util to get resource properties
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 1, 2006 
 * @version $Id$ 
 */
public class ResourcePropertiesUtil {
	
	public static Properties getResourceProperties() throws Exception {
		Properties props = new Properties();
		
		MessageContext ctx = MessageContext.getCurrentContext();
		String servicePath = ctx.getTargetService();
		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/configuration";
		Context initialContext = new InitialContext();
		Object config = initialContext.lookup(jndiName);
		Class<?> configClass = config.getClass();
		Method[] configMethods = configClass.getMethods();
		for (int i = 0; i < configMethods.length; i++) {
			Method current = configMethods[i];
			if (current.getName().startsWith("get")
				&& current.getReturnType().equals(String.class)
				&& Modifier.isPublic(current.getModifiers())) {
				String key = current.getName().substring(3);
				String value = (String) current.invoke(config, new Object[] {});
				props.setProperty(key, value);
			}
		}
		
		return props;
	}
}
