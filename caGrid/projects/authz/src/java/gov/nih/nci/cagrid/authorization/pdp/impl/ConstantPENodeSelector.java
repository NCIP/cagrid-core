package gov.nih.nci.cagrid.authorization.pdp.impl;

import gov.nih.nci.cagrid.authorization.pdp.PENode;
import gov.nih.nci.cagrid.authorization.pdp.PENodeSelector;

import org.w3c.dom.Document;

public class ConstantPENodeSelector implements PENodeSelector {

	private String privilege;
	private String objectId;
	
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	public String getPrivilege() {
		return this.privilege;
	}

	public PENode[] selectPENodes(Document doc) {
		return new PENode[]{new PENodeImpl(doc, getObjectId())};
	}

}
