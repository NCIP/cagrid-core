package gov.nih.nci.cagrid.authorization.pdp;

import org.w3c.dom.Node;

public interface ObjectIdGenerator {
	
	String generateId(Node node);

}
