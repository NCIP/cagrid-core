package gov.nih.nci.cagrid.authorization.pdp.impl;

import gov.nih.nci.cagrid.authorization.pdp.ObjectIdGenerator;
import gov.nih.nci.cagrid.authorization.pdp.PENode;
import gov.nih.nci.cagrid.authorization.pdp.PENodeSelector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathPENodeSelector implements PENodeSelector {

	private static Log logger = LogFactory
			.getLog(XPathPENodeSelector.class.getName());

	private Map xpathMap;
	private String privilege;

	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	

	public void setXpathMap(Map xpathMap) {
		this.xpathMap = xpathMap;
	}

	public Map getXpathMap() {
		return this.xpathMap;
	}

	public PENode[] selectPENodes(Document doc) {
		XPath xpathEngine = null;
		try{
			xpathEngine = XPathFactory.newInstance().newXPath();
		}catch(Exception ex){
			throw new RuntimeException("Error instantiating XPath: " + ex.getMessage(), ex);
		}
		Set nodes = new HashSet();
		for (Iterator i = getXpathMap().entrySet().iterator(); i.hasNext();) {
			Entry entry = (Entry)i.next();
			String xpath = (String) entry.getKey();
			ObjectIdGenerator idGen = (ObjectIdGenerator)entry.getValue();
			NodeList list = null;
			try {
				logger.debug("Evaluating '" + xpath + "' against:\n"
						+ XmlUtils.toString(doc));
				list = (NodeList) xpathEngine.evaluate(xpath, doc,
						XPathConstants.NODESET);
			} catch (XPathExpressionException ex) {
				throw new RuntimeException("Error evaluating '" + xpath + "': "
						+ ex.getMessage(), ex);
			}
			for (int j = 0; j < list.getLength(); j++) {
				Node node = list.item(j);
				String objectId = idGen.generateId(node);
				nodes.add(new PENodeImpl(node, objectId));
			}
		}
		return (PENode[]) nodes.toArray(new PENode[nodes.size()]);
	}
}
