package gov.nih.nci.cagrid.authorization.pdp.impl.id;

import gov.nih.nci.cagrid.authorization.pdp.ObjectIdGenerator;

import org.w3c.dom.Node;

public class NodeQNameObjectIdGenerator implements ObjectIdGenerator {

	public String generateId(Node node) {
		return "{" + node.getNamespaceURI() + "}" + node.getLocalName();
	}

}
