package gov.nih.nci.cagrid.authorization.pdp;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface PENodeSelector {
	
	String getPrivilege();
	
	PENode[] selectPENodes(Document doc);

}
