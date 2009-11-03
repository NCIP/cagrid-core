package org.cagrid.gaards.dorian.policy;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HostAgreementDeserializer extends DeserializerImpl implements Deserializer {
	public QName xmlType;
	public Class javaType;

	protected static Log LOG = LogFactory.getLog(HostAgreementDeserializer.class.getName());


	public HostAgreementDeserializer(Class javaType, QName xmlType) {
		this.xmlType = xmlType;
		this.javaType = javaType;
	}


	public void onEndElement(String namespace, String localName, DeserializationContext context) {
		long startTime=System.currentTimeMillis();
		MessageElement msgElem = context.getCurElement();
		String dom = null;
		try {
			dom = msgElem.getAsString();
		} catch (Exception e) {
			LOG.error("Problem extracting HostAgreement message type! Result will be null!", e);
		}
		if (dom != null) {
			try {
				value = PolicyUtils.stringToHostAgreement(dom);
			} catch (Exception e) {
				LOG.error("Problem with castor marshalling!", e);
			} 
		}
		long duration=System.currentTimeMillis()- startTime;
		LOG.debug("Total time to deserialize("+localName+"):"+duration+" ms.");
	}
}
