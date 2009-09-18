package gov.nci.nih.cagrid.tests.cqlprocessors.util;

import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.message.MessageElement;

/** 
 *  CQLQueryResultsUtil
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 4, 2006 
 * @version $Id: CQLQueryResultsUtil.java,v 1.3 2006-08-30 18:10:08 mccon012 Exp $ 
 */
public class CQLQueryResultsUtil {
	
	public static CQLQueryResults createQueryResults(List rawObjects, InputStream configStream) {
		MessageContext context = null;
		if (configStream != null) {
			context = createMessageContext(configStream);
		} else {
			context = MessageContext.getCurrentContext();
		}
		CQLQueryResults results = new CQLQueryResults();
		LinkedList resultObjects = new LinkedList();
		Iterator objectIter = rawObjects.iterator();
		while (objectIter.hasNext()) {
			Object obj = objectIter.next();
			resultObjects.add(createObjectResult(obj, context));
		}
		CQLObjectResult[] objectResultArray = new CQLObjectResult[rawObjects.size()];
		resultObjects.toArray(objectResultArray);
		results.setObjectResult(objectResultArray);
		return results;
	}
	
	
	private static MessageContext createMessageContext(InputStream configStream) {
		EngineConfiguration config = new FileProvider(configStream);
		AxisClient client = new AxisClient(config);
		MessageContext context = new MessageContext(client);
		return context;
	}
	
	
	private static CQLObjectResult createObjectResult(Object obj, MessageContext context) {
		CQLObjectResult objectResult = new CQLObjectResult();
		//objectResult.setClassname(obj.getClass().getName());
		QName objectQname = context.getTypeMapping().getTypeQName(obj.getClass());
		if (objectQname == null) {
			try {
				Method m = obj.getClass().getMethod("getTypeDesc", new Class[0]);
				m.setAccessible(true);
				TypeDesc typeDesc = (TypeDesc) m.invoke(obj, new Object[0]);
				objectQname = typeDesc.getXmlType();
			} catch (Exception e) {
				// oh well, we tried
			}
		}
		if (objectQname == null) {
			throw new NullPointerException("No qname found for class " + obj.getClass().getName() 
				+ ". Check your client or server-config.wsdd");
		}
		MessageElement anyElement = new MessageElement(objectQname, obj);
		objectResult.set_any(new MessageElement[] {anyElement});
		return objectResult;
	}
}
